package com.pcs.ztqtj.view.activity.prove

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_weather_prove.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 气象证明
 */
class WeatherProveFragment: BaseFragment() {

    private val dataList: ArrayList<ProveDto> = ArrayList()
    private var mAdapter: WeatherProveAdapter? = null
    private var mReceiver: MyBroadCastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_prove, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBroadCast()
        initRefreshLayout()
        initListView()
    }

    private fun initBroadCast() {
        mReceiver = MyBroadCastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(CONST.BROADCAST_REFRESH_PROVE)
        activity!!.registerReceiver(mReceiver, intentFilter)
    }

    private inner class MyBroadCastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (TextUtils.equals(intent!!.action, CONST.BROADCAST_REFRESH_PROVE)) {
                refresh()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            activity!!.unregisterReceiver(mReceiver)
        }
    }

    /**
     * 初始化下拉刷新布局
     */
    private fun initRefreshLayout() {
        refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4)
        refreshLayout.setProgressViewEndTarget(true, 400)
        refreshLayout.isRefreshing = true
        refreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    /**
     * 重新加载数据
     */
    private fun refresh() {
        dataList.clear()
        if (mAdapter != null) {
            mAdapter!!.notifyDataSetChanged()
        }
        okHttpList()
    }

    private fun initListView() {
        mAdapter = WeatherProveAdapter(activity, dataList)
        listView.adapter = mAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val data = dataList[position]
            dialogExit(activity!!, "删除", "确定删除该条证明?", data.id)
        }

        refresh()
    }

    private fun okHttpList() {
        Thread {
            val flag = arguments!!.getString("flag")
            val url = "${CONST.BASE_URL}disaster_proof/disasterProofList"
            Log.e("disasterProofList", url)
            val param = JSONObject()
            param.put("token", MyApplication.TOKEN)
            val info = JSONObject()
            info.put("userId", MyApplication.UID)
            info.put("flag", flag)
            param.put("paramInfo", info)
            val json: String = param.toString()
            Log.e("disasterProofList", json)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    if (activity == null) {
                        return
                    }
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        refreshLayout.isRefreshing = false
                        Log.e("disasterProofList", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("result")) {
                                    dataList.clear()
                                    val data = obj.get("result")
                                    if (!TextUtils.isEmpty(data.toString())) {
                                        val array = obj.getJSONArray("result")
                                        for (i in 0 until array.length()) {
                                            val dto = ProveDto()
                                            val itemObj = array.getJSONObject(i)
                                            if (!itemObj.isNull("id")) {
                                                dto.id = itemObj.getString("id")
                                            }
                                            if (!itemObj.isNull("contactsName")) {
                                                dto.contactsName = itemObj.getString("contactsName")
                                            }
                                            if (!itemObj.isNull("type")) {
                                                dto.type = itemObj.getString("type")
                                            }
                                            if (!itemObj.isNull("policyNumber")) {
                                                dto.policyNumber = itemObj.getString("policyNumber")
                                            }
                                            if (!itemObj.isNull("createTime")) {
                                                dto.createTime = itemObj.getString("createTime")
                                            }
                                            if (!itemObj.isNull("status")) {
                                                dto.status = itemObj.getString("status")
                                            }
                                            if (!itemObj.isNull("auditOpinion")) {
                                                dto.auditOpinion = itemObj.getString("auditOpinion")
                                            }
                                            if (!itemObj.isNull("pdfPath")) {
                                                dto.pdfPath = itemObj.getString("pdfPath")
                                            }
                                            if (!itemObj.isNull("imgPath")) {
                                                dto.imgPath = itemObj.getString("imgPath")
                                            }
                                            dataList.add(dto)
                                        }
                                    }

                                    if (mAdapter != null) {
                                        mAdapter!!.notifyDataSetChanged()
                                    }
                                    if (dataList.size <= 0) {
                                        tvNoData.visibility = View.VISIBLE
                                    } else {
                                        tvNoData.visibility = View.GONE
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }.start()
    }

    /**
     * 退出对话框
     */
    private fun dialogExit(context: Context, title: String?, content: String?, id: String?) {
        val inflater = context.getSystemService(FragmentActivityZtqBase.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_exit, null)
        val tipTitle = view.findViewById<TextView>(R.id.tipTitle)
        val tipContent = view.findViewById<TextView>(R.id.tipContent)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        val tvSure = view.findViewById<TextView>(R.id.tvSure)
        if (TextUtils.isEmpty(title)) {
            tipTitle.text = "提示"
        } else {
            tipTitle.text = title
        }
        tipContent.text = content
        val dialog = Dialog(context, R.style.CustomProgressDialog)
        dialog.setContentView(view)
        dialog.show()
        tvCancel.setOnClickListener { dialog.dismiss() }
        tvSure.setOnClickListener {
            dialog.dismiss()
            okHttpDelete(id)
        }
    }

    private fun okHttpDelete(id: String?) {
        Thread {
            val url = "${CONST.BASE_URL}disaster_proof/remove"
            Log.e("remove", url)
            val param = JSONObject()
            param.put("token", MyApplication.TOKEN)
            val info = JSONObject()
            info.put("id", id)
            param.put("paramInfo", info)
            val json: String = param.toString()
            Log.e("remove", json)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    if (activity == null) {
                        return
                    }
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    activity!!.runOnUiThread {
                        Log.e("remove", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("status")) {
                                    val status = obj.getString("status")
                                    if (TextUtils.equals(status, "success")) {
                                        okHttpList()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }.start()
    }

}