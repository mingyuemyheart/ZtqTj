package com.pcs.ztqtj.view.activity.life.health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.life.HealthDto
import kotlinx.android.synthetic.main.fragment_health_weather.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * 健康气象
 */
class HealthWeatherFragment: Fragment() {

    private var aqi = 0
    private var mAdapter: HealthWeatherAdapter? = null
    private val dataList: ArrayList<HealthDto> = ArrayList()
    private val monthList: ArrayList<HealthDto> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_health_weather, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidget()
        initGridView()
    }

    private fun initWidget() {
        tvDetail.setOnClickListener {
            startActivity(Intent(activity, ActivityHealthDetail::class.java))
        }

        val position = arguments!!.getString("position")
        if (position != null) {
            tvPosition.text = position
        }
        val data: HealthDto = arguments!!.getParcelable("data")
        if (data != null) {
            if (data.aqi != null) {
                aqi = data.aqi.toInt()
                var valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_1)
                when {
                    aqi <= 50 -> {
                        // 优
                        valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_1)
                    }
                    aqi in 51..100 -> {
                        // 良
                        valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_2)
                    }
                    aqi in 101..150 -> {
                        // 轻度污染
                        valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_3)
                    }
                    aqi in 151..200 -> {
                        // 中度污染
                        valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_4)
                    }
                    aqi in 201..300 -> {
                        // 重度污染
                        valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_5)
                    }
                    aqi > 300 -> {
                        // 严重污染
                        valueColor = ContextCompat.getColor(activity!!, R.color.air_quality_6)
                    }
                }
                if (circle_progress_view != null) {
                    circle_progress_view.setValue(aqi.toFloat(), valueColor, data.aqiLevel)
                }
            }
            if (data.hazeTips != null) {
                tvHaze.text = data.hazeTips
            }
            if (data.aqiswTips != null) {
                tvWrw.text = data.aqiswTips
            }
            if (data.wrqxtj != null) {
                tvTjLevel.text = data.wrqxtj+"级"
            }
            if (data.wrqxtjTips != null) {
                tvTj.text = data.wrqxtjTips
            }
            if (data.aqiTips != null) {
                tvAqiTips.text = data.aqiTips
            }
        }

        val calendar: Calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)+1
        tvMonth.text = month.toString()

        okHttpHealthIndex()
        okHttpMonth()
    }

    private fun initGridView() {
        mAdapter = HealthWeatherAdapter(activity, dataList)
        gridView.adapter = mAdapter
    }

    /**
     * 获取健康气象指数
     */
    private fun okHttpHealthIndex() {
        Thread {
            try {
                val lat = arguments!!.getDouble("lat", 0.0)
                val lng = arguments!!.getDouble("lng", 0.0)
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val info = JSONObject()
                info.put("lat", lat)
                info.put("lon", lng)
                param.put("paramInfo", info)
                val json = param.toString()
                Log.e("healthyindex_latest", json)
                val url = CONST.BASE_URL + "healthyindex_latest"
                Log.e("healthyindex_latest", url)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        if (!isAdded) {
                            return
                        }
                        val result = response.body!!.string()
                        activity!!.runOnUiThread {
                            Log.e("healthyindex_latest", result)
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    dataList.clear()
                                    val obj = JSONObject(result)
                                    if (!obj.isNull("result")) {
                                        val array = obj.getJSONArray("result")
                                        for (i in 0 until array.length()) {
                                            val dto = HealthDto()
                                            val itemObj = array.getJSONObject(i)
                                            if (!itemObj.isNull("code")) {
                                                dto.code = itemObj.getString("code")
                                            }
                                            if (!itemObj.isNull("name")) {
                                                dto.name = itemObj.getString("name")
                                            }
                                            if (!itemObj.isNull("level")) {
                                                dto.level = itemObj.getString("level")
                                            }
                                            if (!itemObj.isNull("tips")) {
                                                dto.tips = itemObj.getString("tips")
                                            }
                                            if (!itemObj.isNull("icon")) {
                                                dto.indexIcon = itemObj.getString("icon")
                                            }
                                            dataList.add(dto)
                                        }
                                        if (mAdapter != null) {
                                            mAdapter!!.notifyDataSetChanged()
                                        }
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

    /**
     * 获取本月关注指数
     */
    private fun okHttpMonth() {
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val json = param.toString()
                val url = CONST.BASE_URL + "healthyindex_month"
                Log.e("healthyindex_month", url)
                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        if (!isAdded) {
                            return
                        }
                        val result = response.body!!.string()
                        activity!!.runOnUiThread {
                            Log.e("healthyindex_month", result)
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    llContainer.removeAllViews()
                                    monthList.clear()
                                    val obj = JSONObject(result)
                                    if (!obj.isNull("result")) {
                                        val array = obj.getJSONArray("result")
                                        for (i in 0 until array.length()) {
                                            val dto = HealthDto()
                                            val itemObj = array.getJSONObject(i)
                                            if (!itemObj.isNull("indexName")) {
                                                dto.indexName = itemObj.getString("indexName")
                                            }
                                            if (!itemObj.isNull("indexDesc")) {
                                                dto.indexDesc = itemObj.getString("indexDesc")
                                            }
                                            if (!itemObj.isNull("indexContent")) {
                                                dto.indexContent = itemObj.getString("indexContent")
                                            }
                                            if (!itemObj.isNull("indexMonth")) {
                                                dto.indexMonth = itemObj.getString("indexMonth")
                                            }
                                            if (!itemObj.isNull("indexIcon")) {
                                                dto.indexIcon = itemObj.getString("indexIcon")
                                            }
                                            monthList.add(dto)

                                            addItem(dto.indexName, i)
                                        }
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

    private fun addItem(indexName: String, index: Int) {
        val llItem = LinearLayout(activity)
        llItem.orientation = LinearLayout.HORIZONTAL
        llItem.gravity = Gravity.CENTER_VERTICAL
        llItem.tag = index
        llItem.setPadding(CommonUtil.dip2px(activity, 5f).toInt(), CommonUtil.dip2px(activity, 5f).toInt(), CommonUtil.dip2px(activity, 5f).toInt(), CommonUtil.dip2px(activity, 5f).toInt())
        val paramItem = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        llItem.layoutParams = paramItem

        val tv = TextView(activity)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        if (indexName != null) {
            tv.text = indexName
        }
        if (index == 0) {
            tv.setTextColor(ContextCompat.getColor(activity!!, R.color.bg_title))
        } else {
            tv.setTextColor(ContextCompat.getColor(activity!!, R.color.text_color4))
        }
        val paramTv = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paramTv.marginEnd = CommonUtil.dip2px(activity, 10f).toInt()
        tv.layoutParams = paramTv
        llItem.addView(tv)

        val dividier = TextView(activity)
        dividier.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.text_color4))
        val paramDivider = LinearLayout.LayoutParams(CommonUtil.dip2px(activity, 1f).toInt(), LinearLayout.LayoutParams.MATCH_PARENT)
        dividier.layoutParams = paramDivider
        llItem.addView(dividier)

        llContainer.addView(llItem)

        llItem.setOnClickListener { v ->
            val vTag = v!!.tag.toString().toInt()
            for (m in 0 until llContainer.childCount) {
                val item = llContainer.getChildAt(m) as LinearLayout
                val itemTag = item.tag.toString().toInt()
                val tvName = item.getChildAt(0) as TextView
                if (vTag == itemTag) {
                    tvName.setTextColor(ContextCompat.getColor(activity!!, R.color.bg_title))
                    try {
                        val dto = monthList[vTag]
                        if (dto.indexDesc != null) {
                            tvDesc.text = dto.indexDesc
                        }
                        if (dto.indexContent != null) {
                            tvContent.text = dto.indexContent
                        }
                    } catch (e : IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    tvName.setTextColor(ContextCompat.getColor(activity!!, R.color.text_color4))
                }
            }
        }

        if (index == 0) {
            try {
                val dto = monthList[index]
                if (dto.indexDesc != null) {
                    tvDesc.text = dto.indexDesc
                }
                if (dto.indexContent != null) {
                    tvContent.text = dto.indexContent
                }
            } catch (e : IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
    }

}