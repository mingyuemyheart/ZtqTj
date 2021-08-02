package com.pcs.ztqtj.view.activity.agricuture

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import kotlinx.android.synthetic.main.activity_agricuture_fact.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 专项服务-行业气象-农业气象-农作物发育情况-农情实况数据
 */
class ActivityAgricutureFact: FragmentActivityZtqBase() {

    private var mAdapter: AdapterAgricutureFact? = null
    private val dataList: ArrayList<AgriDto> = ArrayList()
    private val detailList: ArrayList<AgriDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agricuture_fact)
        initWidget()
        initListView()
    }

    private fun initWidget() {
        if (intent.hasExtra("title")) {
            val title = intent.getStringExtra("title")
            if (title != null) {
                titleText = title
            }
        }
        llTitle.visibility = View.VISIBLE
        okHttpList()
    }

    private fun initListView() {
        mAdapter = AdapterAgricutureFact(this)
        listView.adapter = mAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val dto = dataList[position]
            okHttpDetail(dto.stationid)
        }
    }

    private inner class AdapterAgricutureFact(private val mContext: Context) : BaseAdapter() {

        private inner class Holder {
            var tvStationName: TextView? = null
            var tvTemp: TextView? = null
            var tvHumidity: TextView? = null
        }

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, itemView: View?, parent: ViewGroup): View {
            var view = itemView
            val holder: Holder
            if (view == null) {
                holder = Holder()
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_agricuture_fact, null)
                holder.tvStationName = view.findViewById<View>(R.id.tvStationName) as TextView
                holder.tvTemp = view.findViewById<View>(R.id.tvTemp) as TextView
                holder.tvHumidity = view.findViewById<View>(R.id.tvHumidity) as TextView
                view.tag = holder
            } else {
                holder = view.tag as Holder
            }

            val data: AgriDto = dataList[position]
            if (data.stationname != null) {
                holder.tvStationName!!.text = data.stationname
            }
            if (data.ttt150cm != null) {
                holder.tvTemp!!.text = data.ttt150cm
            }
            if (data.rh150cm != null) {
                holder.tvHumidity!!.text = data.rh150cm
            }
            return view!!
        }
    }

    private fun okHttpList() {
        showProgressDialog()
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val info = JSONObject()
                info.put("stationId", "N6007")
                param.put("paramInfo", info)
                val json = param.toString()
                Log.e("agmeobs", json)
                val url = CONST.BASE_URL + "agmeobs"
                Log.e("agmeobs", url)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        val result = response.body!!.string()
                        runOnUiThread {
                            dismissProgressDialog()
                            Log.e("agmeobs", result)
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    dataList.clear()
                                    val array = JSONArray(result)
                                    for (i in 0 until array.length()) {
                                        val dto = AgriDto()
                                        val itemObj = array.getJSONObject(i)
                                        if (!itemObj.isNull("stationid")) {
                                            dto.stationid = itemObj.getString("stationid")
                                        }
                                        if (!itemObj.isNull("stationname")) {
                                            dto.stationname = itemObj.getString("stationname")
                                        }
                                        if (!itemObj.isNull("rh150cm")) {
                                            dto.rh150cm = itemObj.getString("rh150cm")
                                        }
                                        if (!itemObj.isNull("ttt150cm")) {
                                            dto.ttt150cm = itemObj.getString("ttt150cm")
                                        }
                                        if (!itemObj.isNull("datatime")) {
                                            dto.datatime = itemObj.getString("datatime")
                                        }
                                        dataList.add(dto)
                                    }
                                    if (mAdapter != null) {
                                        mAdapter!!.notifyDataSetChanged()
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun okHttpDetail(stationId: String) {
        if (TextUtils.isEmpty(stationId)) {
            return
        }
        showProgressDialog()
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val info = JSONObject()
                info.put("stationId", stationId)
                param.put("paramInfo", info)
                val json = param.toString()
                val url = CONST.BASE_URL + "agmeobs24h"
                Log.e("agmeobs24h", url)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        val result = response.body!!.string()
                        runOnUiThread {
                            dismissProgressDialog()
                            Log.e("agmeobs24h", result)
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    detailList.clear()
                                    val array = JSONArray(result)
                                    for (i in 0 until array.length()) {
                                        val dto = AgriDto()
                                        val itemObj = array.getJSONObject(i)
                                        if (!itemObj.isNull("stationid")) {
                                            dto.stationid = itemObj.getString("stationid")
                                        }
                                        if (!itemObj.isNull("stationname")) {
                                            dto.stationname = itemObj.getString("stationname")
                                        }
                                        if (!itemObj.isNull("rh150cm")) {
                                            dto.rh150cm = itemObj.getString("rh150cm")
                                        }
                                        if (!itemObj.isNull("ttt150cm")) {
                                            dto.ttt150cm = itemObj.getString("ttt150cm")
                                        }
                                        if (!itemObj.isNull("datatime")) {
                                            dto.datatime = itemObj.getString("datatime")
                                        }
                                        detailList.add(dto)
                                    }
                                    dialogDetail(this@ActivityAgricutureFact, detailList)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * 详情图标对话框
     */
    private fun dialogDetail(context: Context, list: ArrayList<AgriDto>) {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_agri_detail, null)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val llContainer = view.findViewById<LinearLayout>(R.id.llContainer)

        val agriDetailChartView = AgriDetailChartView(context)
        agriDetailChartView.setData(list)
        llContainer.removeAllViews()
//        llContainer.addView(agriDetailChartView)
        llContainer.addView(agriDetailChartView, CommonUtil.widthPixels(context)*2, LinearLayout.LayoutParams.MATCH_PARENT)

        val dialog = Dialog(context, R.style.CustomProgressDialog)
        dialog.setContentView(view)
        dialog.show()
        ivClose.setOnClickListener { dialog.dismiss() }
    }

}