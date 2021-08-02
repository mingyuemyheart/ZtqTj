package com.pcs.ztqtj.view.activity.life.health;

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.life.HealthDto
import com.pcs.ztqtj.view.myview.MyPagerAdapter
import kotlinx.android.synthetic.main.activity_health_weather.*
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
class ActivityHealthWeather : FragmentActivityZtqBase(), AMapLocationListener {

    private var lat = 39.084158
    private var lng = 117.200983
    private var position = "天津"
    private val dataList: ArrayList<HealthDto> = ArrayList()
    private val fragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_weather)
        initWidget()
    }

    private fun initWidget() {
        if (intent.hasExtra("title")) {
            val title = intent.getStringExtra("title")
            if (title != null) {
                titleText = title
            }
        }

        if (CommonUtil.isLocationOpen(this)) {
            startLocation()
        } else {
            locationComplete()
        }
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        val mLocationOption = AMapLocationClientOption() //初始化定位参数
        val mLocationClient = AMapLocationClient(this) //初始化定位
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.isNeedAddress = true //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isOnceLocation = true //设置是否只定位一次,默认为false
        mLocationOption.isMockEnable = false //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.interval = 2000 //设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption) //给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(this)
        mLocationClient.startLocation() //启动定位
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (amapLocation != null && amapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
            if (amapLocation.province.startsWith("天津")) {
                lat = amapLocation.latitude
                lng = amapLocation.longitude
                position = amapLocation.aoiName
            } else {
                lat = 38.925
                lng = 116.915278
                position = "天津"
            }
            locationComplete()
        }
    }

    private fun locationComplete() {
        okHttpHealthFact()
    }

    /**
     * 初始化viewPager
     */
    private fun initViewPager() {
        fragments.clear()
        llContainer!!.removeAllViews()

        for (i in 0 until dataList.size) {
            val llItem = LinearLayout(this)
            llItem.orientation = LinearLayout.VERTICAL
            llItem.gravity = Gravity.CENTER_VERTICAL
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.weight = 1f
            llItem.layoutParams = param

            val data = dataList[i]
            val tv = TextView(this)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            tv.gravity = Gravity.CENTER
            tv.setOnClickListener(MyOnClickListener(i))
            llItem.addView(tv)

            val tvTime = TextView(this)
            tvTime.text = data.dataTime
            tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            tvTime.gravity = Gravity.CENTER
            tvTime.setOnClickListener(MyOnClickListener(i))
            llItem.addView(tvTime)

            val tvBar = TextView(this)
            val paramBar = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            paramBar.width = tv.layoutParams.width
            paramBar.height = CommonUtil.dip2px(this, 3f).toInt()
            paramBar.topMargin = CommonUtil.dip2px(this, 3f).toInt()
            tvBar.layoutParams = paramBar
            llItem.addView(tvBar)

            when(i) {
                0 -> {
                    tv.text = "24小时"
                    tv.setTextColor(ContextCompat.getColor(this, R.color.bg_title))
                    tvTime.setTextColor(ContextCompat.getColor(this, R.color.bg_title))
                    tvBar.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_title))
                }
                else -> {
                    tv.text = "48小时"
                    tv.setTextColor(ContextCompat.getColor(this, R.color.dgray))
                    tvTime.setTextColor(ContextCompat.getColor(this, R.color.text_color4))
                    tvBar.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
                }
            }
            llContainer.addView(llItem)

            val fragment = HealthWeatherFragment()
            val bundle = Bundle()
            bundle.putString("position", position)
            bundle.putDouble("lat", lat)
            bundle.putDouble("lng", lng)
            bundle.putParcelable("data", data)
            fragment.arguments = bundle
            fragments.add(fragment)
        }
        viewPager.setSlipping(true) //设置ViewPager是否可以滑动
        viewPager.offscreenPageLimit = fragments.size
        viewPager.setOnPageChangeListener(MyOnPageChangeListener())
        viewPager.adapter = MyPagerAdapter(supportFragmentManager, fragments)
    }

    private inner class MyOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageSelected(arg0: Int) {
            if (llContainer != null) {
                for (i in 0 until llContainer.childCount) {
                    val llItem = llContainer.getChildAt(i) as LinearLayout
                    val tv = llItem.getChildAt(0) as TextView
                    val tvTime = llItem.getChildAt(1) as TextView
                    val tvBar = llItem.getChildAt(2) as TextView
                    if (i == arg0) {
                        tv.setTextColor(ContextCompat.getColor(this@ActivityHealthWeather, R.color.bg_title))
                        tvTime.setTextColor(ContextCompat.getColor(this@ActivityHealthWeather, R.color.bg_title))
                        tvBar.setBackgroundColor(ContextCompat.getColor(this@ActivityHealthWeather, R.color.bg_title))
                    } else {
                        tv.setTextColor(ContextCompat.getColor(this@ActivityHealthWeather, R.color.dgray))
                        tvTime.setTextColor(ContextCompat.getColor(this@ActivityHealthWeather, R.color.text_color4))
                        tvBar.setBackgroundColor(ContextCompat.getColor(this@ActivityHealthWeather, R.color.light_gray))
                    }
                }
            }
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    /**
     * 头标点击监听
     * @author shawn_sun
     */
    private inner class MyOnClickListener(private val index: Int) : View.OnClickListener {
        override fun onClick(v: View) {
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true)
            }
        }
    }

    /**
     * 获取健康气象实况
     */
    private fun okHttpHealthFact() {
        showProgressDialog()
        Thread {
            try {
                val param = JSONObject()
                param.put("token", MyApplication.TOKEN)
                val info = JSONObject()
                info.put("lat", lat)
                info.put("lon", lng)
                param.put("paramInfo", info)
                val json = param.toString()
                Log.e("healthy_aqi", json)
                val url = CONST.BASE_URL + "healthy_aqi"
                Log.e("healthy_aqi", url)
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
                            Log.e("healthy_aqi", result)
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    dataList.clear()
                                    val obj = JSONObject(result)
                                    if (!obj.isNull("result")) {
                                        val array = obj.getJSONArray("result")
                                        for (i in 0 until array.length()) {
                                            val dto = HealthDto()
                                            val itemObj = array.getJSONObject(i)
                                            if (!itemObj.isNull("dataTime")) {
                                                dto.dataTime = itemObj.getString("dataTime")
                                            }
                                            if (!itemObj.isNull("aqi")) {
                                                dto.aqi = itemObj.getString("aqi")
                                            }
                                            if (!itemObj.isNull("aqiLevel")) {
                                                dto.aqiLevel = itemObj.getString("aqiLevel")
                                            }
                                            if (!itemObj.isNull("aqiTips")) {
                                                dto.aqiTips = itemObj.getString("aqiTips")
                                            }
                                            if (!itemObj.isNull("hazeTips")) {
                                                dto.hazeTips = itemObj.getString("hazeTips")
                                            }
                                            if (!itemObj.isNull("aqiswTips")) {
                                                dto.aqiswTips = itemObj.getString("aqiswTips")
                                            }
                                            if (!itemObj.isNull("wrqxtjTips")) {
                                                dto.wrqxtjTips = itemObj.getString("wrqxtjTips")
                                            }
                                            if (!itemObj.isNull("wrqxtj")) {
                                                dto.wrqxtj = itemObj.getString("wrqxtj")
                                            }
                                            dataList.add(dto)
                                        }
                                        initViewPager()
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

}
