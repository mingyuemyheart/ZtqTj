package com.pcs.ztqtj.view.activity.life.health;

import android.os.Bundle;
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.pcs.ztqtj.MyApplication

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.life.HealthDto
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_health_detail.*
import kotlinx.android.synthetic.main.activity_health_detail.llContainer
import kotlinx.android.synthetic.main.fragment_health_weather.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 健康气象详情
 */
class ActivityHealthDetail : FragmentActivityZtqBase() {

    private var mAdapter: HealthDetailAdapter? = null
    private val dataList: ArrayList<HealthDto> = ArrayList()
    private val monthList: ArrayList<HealthDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_detail)
        initWidget()
        initListView()
    }

    private fun initWidget() {
        titleText = "指数详情"
    }

    private fun initListView() {
        mAdapter = HealthDetailAdapter(this, dataList)
        listView.adapter = mAdapter
        okHttpList()
        okHttpMonth()
    }

    /**
     * 获取健康气象指数
     */
    private fun okHttpList() {
        showProgressDialog()
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val json = param.toString()
                val url = CONST.BASE_URL + "healthyindex_list"
                Log.e("healthyindex_list", url)
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
                            Log.e("healthyindex_list", result)
                            dismissProgressDialog()
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    dataList.clear()
                                    var dto = HealthDto()
                                    dto.isFirst = true
                                    dto.indexName = ""
                                    dto.indexDesc = ""
                                    dto.indexContent = ""
                                    dto.indexMonth = "1,2,3,4,5,6,7,8,9,10,11,12"
                                    dto.indexIcon = ""
                                    dataList.add(dto)
                                    val obj = JSONObject(result)
                                    if (!obj.isNull("result")) {
                                        val array = obj.getJSONArray("result")
                                        for (i in 0 until array.length()) {
                                            dto = HealthDto()
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
                        val result = response.body!!.string()
                        runOnUiThread {
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

                                            addItem(dto.indexIcon, dto.indexName)
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

    private fun addItem(indexIcon: String, indexName: String) {
        val llItem = LinearLayout(this@ActivityHealthDetail)
        llItem.orientation = LinearLayout.VERTICAL
        llItem.gravity = Gravity.CENTER_HORIZONTAL
        llItem.setPadding(CommonUtil.dip2px(this, 5f).toInt(), CommonUtil.dip2px(this, 5f).toInt(), CommonUtil.dip2px(this, 5f).toInt(), CommonUtil.dip2px(this, 5f).toInt())
        val paramItem = LinearLayout.LayoutParams(CommonUtil.dip2px(this@ActivityHealthDetail, 80f).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        llItem.layoutParams = paramItem

        val imageView = ImageView(this@ActivityHealthDetail)
        if (!TextUtils.isEmpty(indexIcon)) {
            val imgUrl = getString(R.string.msyb)+indexIcon
            Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.no_pic)
        }
        val param = LinearLayout.LayoutParams(CommonUtil.dip2px(this@ActivityHealthDetail, 30f).toInt(), CommonUtil.dip2px(this@ActivityHealthDetail, 30f).toInt())
        param.bottomMargin = CommonUtil.dip2px(this@ActivityHealthDetail, 5f).toInt()
        imageView.layoutParams = param
        llItem.addView(imageView)

        val tv = TextView(this@ActivityHealthDetail)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tv.setTextColor(ContextCompat.getColor(this@ActivityHealthDetail, R.color.text_color4))
        if (indexName != null) {
            tv.text = indexName
        }
        val paramTv = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tv.gravity = Gravity.CENTER
        tv.layoutParams = paramTv
        llItem.addView(tv)

        llContainer.addView(llItem)


        val dividier = TextView(this@ActivityHealthDetail)
        dividier.setBackgroundColor(ContextCompat.getColor(this@ActivityHealthDetail, R.color.bg_title))
        val paramDivider = LinearLayout.LayoutParams(CommonUtil.dip2px(this@ActivityHealthDetail, 1f).toInt(), LinearLayout.LayoutParams.MATCH_PARENT)
        dividier.layoutParams = paramDivider
        llContainer.addView(dividier)
    }

}
