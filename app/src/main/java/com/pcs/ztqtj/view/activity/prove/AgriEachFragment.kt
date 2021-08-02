package com.pcs.ztqtj.view.activity.prove

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import kotlinx.android.synthetic.main.fragment_agri_each.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 苗情农情互动
 */
class AgriEachFragment: Fragment() {

    private val dataList: ArrayList<ProveDto> = ArrayList()
    private var mAdapter: AgriEachAdapter? = null
    private var mReceiver: MyBroadCastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agri_each, null)
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
        intentFilter.addAction(CONST.BROADCAST_REFRESH_EACH)
        activity!!.registerReceiver(mReceiver, intentFilter)
    }

    private inner class MyBroadCastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (TextUtils.equals(intent!!.action, CONST.BROADCAST_REFRESH_EACH)) {
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
        mAdapter = AgriEachAdapter(activity, dataList)
        listView.adapter = mAdapter

        refresh()
    }

    private fun okHttpList() {
        Thread {
            val flag = arguments!!.getString("flag")
            val param = JSONObject()
            param.put("token", MyApplication.TOKEN)
            val info = JSONObject()
            if (TextUtils.isEmpty(flag)) {//全部问答
                info.put("userId", "")
            } else {//我的问答
                info.put("userId", MyApplication.UID)
            }
            info.put("type", "2")//type：1（意见反馈），2（苗情速报）
            param.put("paramInfo", info)
            val json: String = param.toString()
            Log.e("feedBackList", json)
            val url = "${CONST.BASE_URL}feed_back/feedBackList"
            Log.e("feedBackList", url)
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
                        Log.e("feedBackList", result)
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
                                            if (!itemObj.isNull("createTime")) {
                                                dto.createTime = itemObj.getString("createTime")
                                            }
                                            if (!itemObj.isNull("content")) {
                                                dto.ask = itemObj.getString("content")
                                            }
                                            if (!itemObj.isNull("picture")) {
                                                dto.imgUrl = itemObj.getString("picture")
                                            }
                                            if (!itemObj.isNull("children")) {
                                                val children = itemObj.getJSONObject("children")
                                                if (!children.isNull("content")) {
                                                    dto.anwser = children.getString("content")
                                                }
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

}