package com.pcs.ztqtj.view.activity.prove

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.*
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.pcs.ztqtj.MyApplication
import com.pcs.ztqtj.R
import com.pcs.ztqtj.util.CONST
import com.pcs.ztqtj.util.OkHttpUtil
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase
import com.pcs.ztqtj.view.activity.set.ActivityDisclaimer
import com.pcs.ztqtj.view.myview.wheelview.NumericWheelAdapter
import com.pcs.ztqtj.view.myview.wheelview.OnWheelScrollListener
import com.pcs.ztqtj.view.myview.wheelview.WheelView
import kotlinx.android.synthetic.main.activity_weather_prove_submit.*
import kotlinx.android.synthetic.main.fragment_weather_prove.*
import kotlinx.android.synthetic.main.layout_date.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 气象证明提交
 */
class WeatherProveSubmitActivity: FragmentActivityZtqBase(), View.OnClickListener, AMapLocationListener {

    private val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private var category = "1" //1:个人，2：单位
    private var type = "1" //1：保险理赔，2：其他专用
    private var lat = 0.0
    private var lng = 0.0

    //保险公司
    private var companyAdapter: CommonSpinnerAdapter? = null
    private val companyList: ArrayList<ProveDto> = ArrayList()
    private var companyName = ""//公司名称

    //灾种
    private var typeAdapter: CommonSpinnerAdapter? = null
    private val typeList: ArrayList<ProveDto> = ArrayList()
    private var typeName = ""//灾种名称

    //受灾主体
    private var bodyAdapter: CommonSpinnerAdapter? = null
    private val bodyList: ArrayList<ProveDto> = ArrayList()
    private var bodyName = ""//受灾主体名称

    //身份证
    private var cardAdapter: ShowPictureAdapter? = null
    private val cardList: ArrayList<ProveDto> = ArrayList()
    private var cardPics = ""

    //营业执照
    private var zhizhaoAdapter: ShowPictureAdapter? = null
    private val zhizhaoList: ArrayList<ProveDto> = ArrayList()
    private var zhizhaoPics = ""

    //受灾照片
    private var disAdapter: ShowPictureAdapter? = null
    private val disList: ArrayList<ProveDto> = ArrayList()
    private var disPics = ""

    //保单照片
    private var baodanAdapter: ShowPictureAdapter? = null
    private val baodanList: ArrayList<ProveDto> = ArrayList()
    private var baodanPics = ""

    private val maxCount1 = 3
    private val maxCount2 = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_prove_submit)
        initWidget()
    }

    private fun initWidget() {
        titleText = "气象灾害证明"
        tvPerson.setOnClickListener(this)
        tvUnit.setOnClickListener(this)
        rbPerson.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                type = "1"
                rbPerson.setTextColor(ContextCompat.getColor(this, R.color.bg_title))
                rbUnit.isChecked = false
                rbUnit.setTextColor(ContextCompat.getColor(this, R.color.dgray))
                clComp.visibility = View.VISIBLE
                clNum.visibility = View.VISIBLE
                clUse.visibility = View.GONE
            }
        }
        rbUnit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                type = "2"
                rbUnit.setTextColor(ContextCompat.getColor(this, R.color.bg_title))
                rbPerson.isChecked = false
                rbPerson.setTextColor(ContextCompat.getColor(this, R.color.dgray))
                clComp.visibility = View.GONE
                clNum.visibility = View.GONE
                clUse.visibility = View.VISIBLE
            }
        }
        tvPosition.setOnClickListener(this)
        tvTime.text = sdf1.format(Date())
        tvTime.setOnClickListener(this)
        tvSampleCard.setOnClickListener(this)
        tvSampleZhizhao.setOnClickListener(this)
        tvSampleDis.setOnClickListener(this)
        tvSampleBaodan.setOnClickListener(this)
        tvNegtive.setOnClickListener(this)
        tvPositive.setOnClickListener(this)
        tvSubmit.setOnClickListener(this)
        tvGuide.setOnClickListener(this)

        startLocation()
        initPrompt()
        initWheelView()
        initSavePerson()
        initSpinner()
        initGridViewCard()
        initGridViewZhizhao()
        initGridViewDis()
        initGridViewBaodan()
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
            lat = amapLocation.latitude
            lng = amapLocation.longitude
            tvPosition.text = if (amapLocation.province.contains(amapLocation.city)) {
                amapLocation.city + amapLocation.district + amapLocation.street + amapLocation.aoiName
            } else {
                amapLocation.province + amapLocation.city + amapLocation.district + amapLocation.street + amapLocation.aoiName
            }
        }
    }

    /**
     * 初始化免责声明
     */
    private fun initPrompt() {
        val str1 = "*该项服务仅限于天津市辖区范围内使用，最终解释权归天津市气象服务中心所有。"
        val str2 = "免责声明"
        val buffer = StringBuffer()
        buffer.append(str1).append(str2)
        val builder = SpannableStringBuilder(buffer.toString())
        val builderSpan1 = ForegroundColorSpan(Color.RED)
        val builderSpan2 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.bg_title))
        builder.setSpan(builderSpan1, 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(builderSpan2, str1.length, str1.length + str2.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tvPrompt.text = builder
        tvPrompt.setOnClickListener(this)
    }

    private fun initWheelView() {
        val c = Calendar.getInstance()
        val curYear = c[Calendar.YEAR]
        val curMonth = c[Calendar.MONTH] + 1 //通过Calendar算出的月数要+1
        val curDate = c[Calendar.DATE]
        val curHour = c[Calendar.HOUR_OF_DAY]
        val curMinute = c[Calendar.MINUTE]
        val curSecond = c[Calendar.SECOND]

        val numericWheelAdapter1 = NumericWheelAdapter(this, 1950, curYear)
        numericWheelAdapter1.setLabel("年")
        year.viewAdapter = numericWheelAdapter1
        year.isCyclic = false //是否可循环滑动
        year.addScrollingListener(scrollListener)
        year.visibleItems = 7

        val numericWheelAdapter2 = NumericWheelAdapter(this, 1, 12, "%02d")
        numericWheelAdapter2.setLabel("月")
        month.viewAdapter = numericWheelAdapter2
        month.isCyclic = false
        month.addScrollingListener(scrollListener)
        month.visibleItems = 7

        initDay(curYear, curMonth)
        day.isCyclic = false
        day.visibleItems = 7

        val numericWheelAdapter3 = NumericWheelAdapter(this, 0, 23, "%02d")
        numericWheelAdapter3.setLabel("时")
        hour.viewAdapter = numericWheelAdapter3
        hour.isCyclic = false
        hour.addScrollingListener(scrollListener)
        hour.visibleItems = 7

        val numericWheelAdapter4 = NumericWheelAdapter(this, 0, 59, "%02d")
        numericWheelAdapter4.setLabel("分")
        minute.viewAdapter = numericWheelAdapter4
        minute.isCyclic = false
        minute.addScrollingListener(scrollListener)
        minute.visibleItems = 7

        val numericWheelAdapter5 = NumericWheelAdapter(this, 0, 59, "%02d")
        numericWheelAdapter5.setLabel("秒")
        second.viewAdapter = numericWheelAdapter5
        second.isCyclic = false
        second.addScrollingListener(scrollListener)
        second.visibleItems = 7

        year.currentItem = curYear - 1950
        month.currentItem = curMonth - 1
        day.currentItem = curDate - 1
        hour.currentItem = curHour
        minute.currentItem = curMinute
        second.currentItem = curSecond
    }

    private val scrollListener: OnWheelScrollListener = object : OnWheelScrollListener {
        override fun onScrollingStarted(wheel: WheelView) {}
        override fun onScrollingFinished(wheel: WheelView) {
            val nYear = year!!.currentItem + 1950 //年
            val nMonth: Int = month.currentItem + 1 //月
            initDay(nYear, nMonth)
        }
    }

    /**
     */
    private fun initDay(arg1: Int, arg2: Int) {
        val numericWheelAdapter = NumericWheelAdapter(this, 1, getDay(arg1, arg2), "%02d")
        numericWheelAdapter.setLabel("日")
        day.viewAdapter = numericWheelAdapter
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */
    private fun getDay(year: Int, month: Int): Int {
        var day = 30
        var flag = false
        flag = when (year % 4) {
            0 -> true
            else -> false
        }
        day = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> if (flag) 29 else 28
            else -> 30
        }
        return day
    }

    /**
     */
    private fun setTextViewValue() {
        val yearStr = (year!!.currentItem + 1950).toString()
        val monthStr = if (month.currentItem + 1 < 10) "0" + (month.currentItem + 1) else (month.currentItem + 1).toString()
        val dayStr = if (day.currentItem + 1 < 10) "0" + (day.currentItem + 1) else (day.currentItem + 1).toString()
        val hourStr = if (hour.currentItem + 1 < 10) "0" + (hour.currentItem) else (hour.currentItem).toString()
        val minuteStr = if (minute.currentItem + 1 < 10) "0" + (minute.currentItem) else (minute.currentItem).toString()
        val secondStr = if (second.currentItem + 1 < 10) "0" + (second.currentItem) else (second.currentItem).toString()
        tvTime.text = "$yearStr-$monthStr-$dayStr ${hourStr}:${minuteStr}:${secondStr}"
    }

    private fun bootTimeLayoutAnimation() {
        if (layoutDate!!.visibility == View.GONE) {
            timeLayoutAnimation(true, layoutDate)
            layoutDate!!.visibility = View.VISIBLE
        } else {
            timeLayoutAnimation(false, layoutDate)
            layoutDate!!.visibility = View.GONE
        }
    }

    /**
     * 时间图层动画
     * @param flag
     * @param view
     */
    private fun timeLayoutAnimation(flag: Boolean, view: View?) {
        //列表动画
        val animationSet = AnimationSet(true)
        val animation: TranslateAnimation = if (!flag) {
            TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f)
        } else {
            TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f)
        }
        animation.duration = 200
        animationSet.addAnimation(animation)
        animationSet.fillAfter = true
        view!!.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                view.clearAnimation()
            }
        })
    }

    /**
     * 初始化被保险人
     */
    private fun initSavePerson() {
        val str1 = "请上传"
        val str2 = "被保险人"
        val str3 = "身份证正反面照片"
        val buffer = StringBuffer()
        buffer.append(str1).append(str2).append(str3)
        val builder = SpannableStringBuilder(buffer.toString())
        val builderSpan1 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.dgray))
        val builderSpan2 = ForegroundColorSpan(Color.RED)
        val builderSpan3 = ForegroundColorSpan(ContextCompat.getColor(this, R.color.dgray))
        builder.setSpan(builderSpan1, 0, str1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(builderSpan2, str1.length, str1.length+str2.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        builder.setSpan(builderSpan3, str1.length+str2.length, str1.length+str2.length+str3.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tvSafe.text = builder
        tvSampleCard.setOnClickListener(this)
    }

    private fun initSpinner() {
        companyAdapter = CommonSpinnerAdapter(this, companyList)
        spCompany.adapter = companyAdapter
        spCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                val data = companyList[pos]
                companyName = data.companyName
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        typeAdapter = CommonSpinnerAdapter(this, typeList)
        spType.adapter = typeAdapter
        spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                val data = typeList[pos]
                typeName = data.companyName
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        bodyAdapter = CommonSpinnerAdapter(this, bodyList)
        spBody.adapter = bodyAdapter
        spBody.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                val data = bodyList[pos]
                bodyName = data.companyName
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        okHttpSpinners(companyList, companyAdapter, pbCompany, "insurance_company_list")
        okHttpSpinners(typeList, typeAdapter, pbType, "disaster_category_list")
        okHttpSpinners(bodyList, bodyAdapter, pbBody, "victim_list")
    }

    /**
     * 获取保险公司
     */
    private fun okHttpSpinners(list: ArrayList<ProveDto>, adapter: CommonSpinnerAdapter?, pb: ProgressBar, dataUrl: String) {
        pb.visibility = View.VISIBLE
        Thread {
            val url = "${CONST.BASE_URL}$dataUrl"
            Log.e(dataUrl, url)
            val param = JSONObject()
            param.put("token", MyApplication.TOKEN)
            val json: String = param.toString()
            Log.e(dataUrl, json)
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        Log.e(dataUrl, result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                list.clear()
                                val array = JSONArray(result)
                                for (i in 0 until array.length()) {
                                    val dto = ProveDto()
                                    val itemObj = array.getJSONObject(i)
                                    if (!itemObj.isNull("name")) {
                                        dto.companyName = itemObj.getString("name")
                                    }
                                    list.add(dto)
                                }
                                if (adapter != null) {
                                    adapter!!.notifyDataSetChanged()
                                }
                                pb.visibility = View.GONE
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
     * 添加图片按钮
     */
    private fun initAddPicButton(list: ArrayList<ProveDto>, maxCount: Int) {
        for (i in 0 until list.size) {
            if (TextUtils.isEmpty(list[i].imgUrl)) {
                list.removeAt(i)
            }
        }
        if (list.size < maxCount-1) {
            val dto = ProveDto()
            dto.drawable = R.drawable.icon_add_pic
            dto.imgUrl = ""
            list.add(dto)
        }
    }

    /**
     * 身份证
     */
    private fun initGridViewCard() {
        initAddPicButton(cardList, maxCount1)
        cardAdapter = ShowPictureAdapter(this, cardList)
        gridViewCard.adapter = cardAdapter
        gridViewCard.setOnItemClickListener { parent, view, position, id ->
            val data = cardList[position]
            if (TextUtils.isEmpty(data.imgUrl)) {//点击了添加按钮
                val intent = Intent(this, SelectPictureActivity::class.java)
                intent.putExtra("count", cardList.size)
                intent.putExtra("maxCount", maxCount1)
                startActivityForResult(intent, 1001)
            } else {//预览
                val intent = Intent(this, DisplayPictureActivity::class.java)
                intent.putExtra(CONST.WEB_URL, data.imgUrl)
                startActivity(intent)
            }
        }
    }

    /**
     * 营业执照
     */
    private fun initGridViewZhizhao() {
        initAddPicButton(zhizhaoList, maxCount2)
        zhizhaoAdapter = ShowPictureAdapter(this, zhizhaoList)
        gridViewZhizhao.adapter = zhizhaoAdapter
        gridViewZhizhao.setOnItemClickListener { parent, view, position, id ->
            val data = zhizhaoList[position]
            if (TextUtils.isEmpty(data.imgUrl)) {//点击了添加按钮
                val intent = Intent(this, SelectPictureActivity::class.java)
                intent.putExtra("count", zhizhaoList.size)
                intent.putExtra("maxCount", maxCount2)
                startActivityForResult(intent, 1002)
            } else {//预览
                val intent = Intent(this, DisplayPictureActivity::class.java)
                intent.putExtra(CONST.WEB_URL, data.imgUrl)
                startActivity(intent)
            }
        }
    }

    /**
     * 受灾照片
     */
    private fun initGridViewDis() {
        initAddPicButton(disList, maxCount2)
        disAdapter = ShowPictureAdapter(this, disList)
        gridViewDis.adapter = disAdapter
        gridViewDis.setOnItemClickListener { parent, view, position, id ->
            val data = disList[position]
            if (TextUtils.isEmpty(data.imgUrl)) {//点击了添加按钮
                val intent = Intent(this, SelectPictureActivity::class.java)
                intent.putExtra("count", disList.size)
                intent.putExtra("maxCount", maxCount2)
                startActivityForResult(intent, 1003)
            } else {//预览
                val intent = Intent(this, DisplayPictureActivity::class.java)
                intent.putExtra(CONST.WEB_URL, data.imgUrl)
                startActivity(intent)
            }
        }
    }

    /**
     * 保单照片
     */
    private fun initGridViewBaodan() {
        initAddPicButton(baodanList, maxCount2)
        baodanAdapter = ShowPictureAdapter(this, baodanList)
        gridViewBaodan.adapter = baodanAdapter
        gridViewBaodan.setOnItemClickListener { parent, view, position, id ->
            val data = baodanList[position]
            if (TextUtils.isEmpty(data.imgUrl)) {//点击了添加按钮
                val intent = Intent(this, SelectPictureActivity::class.java)
                intent.putExtra("count", baodanList.size)
                intent.putExtra("maxCount", maxCount2)
                startActivityForResult(intent, 1004)
            } else {//预览
                val intent = Intent(this, DisplayPictureActivity::class.java)
                intent.putExtra(CONST.WEB_URL, data.imgUrl)
                startActivity(intent)
            }
        }
    }

    /**
     * 样例对话框
     */
    private fun dialogSample(context: Context, title: String?, drawable: Int) {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_sample, null)
        val tipTitle = view.findViewById<TextView>(R.id.tipTitle)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val tvSure = view.findViewById<TextView>(R.id.tvSure)
        if (TextUtils.isEmpty(title)) {
            tipTitle.text = "提示"
        } else {
            tipTitle.text = title
        }
        imageView.setImageResource(drawable)
        val dialog = Dialog(context, R.style.CustomProgressDialog)
        dialog.setContentView(view)
        dialog.show()
        tvSure.setOnClickListener {
            dialog.dismiss()
        }
    }

    /**
     * 退出对话框
     */
    private fun dialogExit(context: Context, title: String?, content: String?) {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogExit(this, "提示", "确定退出当前编辑页面？")
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                1001 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val list: ArrayList<ProveDto> = bundle.getParcelableArrayList("dataList")
                            for (i in list.size-1 downTo 0) {
                                cardList.add(0, list[i])
                            }
                            initAddPicButton(cardList, maxCount1)
                            if (cardAdapter != null) {
                                cardAdapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
                1002 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val list: ArrayList<ProveDto> = bundle.getParcelableArrayList("dataList")
                            for (i in list.size-1 downTo 0) {
                                zhizhaoList.add(0, list[i])
                            }
                            initAddPicButton(zhizhaoList, maxCount2)
                            if (zhizhaoAdapter != null) {
                                zhizhaoAdapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
                1003 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val list: ArrayList<ProveDto> = bundle.getParcelableArrayList("dataList")
                            for (i in list.size-1 downTo 0) {
                                disList.add(0, list[i])
                            }
                            initAddPicButton(disList, maxCount2)
                            if (disAdapter != null) {
                                disAdapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
                1004 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            val list: ArrayList<ProveDto> = bundle.getParcelableArrayList("dataList")
                            for (i in list.size-1 downTo 0) {
                                baodanList.add(0, list[i])
                            }
                            initAddPicButton(baodanList, maxCount2)
                            if (baodanAdapter != null) {
                                baodanAdapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                }
                2001 -> {
                    if (data != null) {
                        val bundle = data.extras
                        if (bundle != null) {
                            lat = bundle.getDouble("lat")
                            lng = bundle.getDouble("lng")
                            tvPosition.text = bundle.getString("position")
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.tvPrompt -> {
                val intent = Intent(this, ActivityDisclaimer::class.java)
                val bundle = Bundle()
                bundle.putString("title", "免责声明")
                intent.putExtras(bundle)
                startActivity(intent)
            }
            R.id.tvPerson -> {
                category = "1"
                tvPerson.setTextColor(Color.WHITE)
                tvUnit.setTextColor(ContextCompat.getColor(this, R.color.dgray))
                tvPerson.setBackgroundResource(R.drawable.bg_column_blue)
                tvUnit.setBackgroundResource(R.drawable.bg_column_white)

                clCard.visibility = View.VISIBLE
                clZhizhao.visibility = View.GONE
            }
            R.id.tvUnit -> {
                category = "2"
                tvPerson.setTextColor(ContextCompat.getColor(this, R.color.dgray))
                tvUnit.setTextColor(Color.WHITE)
                tvPerson.setBackgroundResource(R.drawable.bg_column_white)
                tvUnit.setBackgroundResource(R.drawable.bg_column_blue)

                clCard.visibility = View.GONE
                clZhizhao.visibility = View.VISIBLE
            }
            R.id.tvPosition -> {
                val intent = Intent(this, SelectPositionActivity::class.java)
                intent.putExtra("lat", lat)
                intent.putExtra("lng", lng)
                intent.putExtra("position", tvPosition.text.toString())
                startActivityForResult(intent, 2001)
            }
            R.id.tvSampleCard -> dialogSample(this, "身份证样例", R.drawable.sample_card)
            R.id.tvSampleZhizhao -> dialogSample(this, "营业执照样例", R.drawable.sample_zhizhao)
            R.id.tvSampleDis -> dialogSample(this, "受灾图片样例", R.drawable.sample_dis)
            R.id.tvSampleBaodan -> dialogSample(this, "保单样例", R.drawable.sample_baodan)
            R.id.tvTime -> {
                bootTimeLayoutAnimation()
            }
            R.id.tvNegtive -> bootTimeLayoutAnimation()
            R.id.tvPositive -> {
                setTextViewValue()
                bootTimeLayoutAnimation()
            }
            R.id.tvSubmit -> {
                okHttpPostFiles()
            }
            R.id.tvGuide -> {
                Toast.makeText(this, "暂无，敬请期待", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 校验信息
     */
    private fun checkPost() : Boolean {
        if (TextUtils.isEmpty(etName.text.toString())) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(etPhone.text.toString())) {
            Toast.makeText(this, "请输入联系电话", Toast.LENGTH_SHORT).show()
            return false
        }

        if (clUse.visibility == View.GONE) {
            if (TextUtils.isEmpty(companyName)) {
                Toast.makeText(this, "请选择保险公司名称", Toast.LENGTH_SHORT).show()
                return false
            }
            if (TextUtils.isEmpty(etNum.text.toString())) {
                Toast.makeText(this, "请输入保单号", Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            if (TextUtils.isEmpty(etUse.text.toString())) {
                Toast.makeText(this, "请输入用途", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (TextUtils.isEmpty(typeName)) {
            Toast.makeText(this, "请选择灾种", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(tvTime.text.toString())) {
            Toast.makeText(this, "请选择受灾时间", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(tvPosition.text.toString())) {
            Toast.makeText(this, "请选择受灾地点", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(bodyName)) {
            Toast.makeText(this, "请选择受灾主体", Toast.LENGTH_SHORT).show()
            return false
        }

        if (clZhizhao.visibility == View.GONE) {
            var cardSize = 0
            for (i in 0 until cardList.size) {
                val dto = cardList[i]
                if (!TextUtils.isEmpty(dto.imgUrl)) {
                    cardSize++
                }
            }
            if (cardSize == 0) {
                Toast.makeText(this, "请上传身份证照片", Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            var zhizhaoSize = 0
            for (i in 0 until zhizhaoList.size) {
                val dto = zhizhaoList[i]
                if (!TextUtils.isEmpty(dto.imgUrl)) {
                    zhizhaoSize++
                }
            }
            if (zhizhaoSize == 0) {
                Toast.makeText(this, "请上传营业执照或法人证书照片", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        var disSize = 0
        for (i in 0 until disList.size) {
            val dto = disList[i]
            if (!TextUtils.isEmpty(dto.imgUrl)) {
                disSize++
            }
        }
        if (disSize == 0) {
            Toast.makeText(this, "请上传受灾照片", Toast.LENGTH_SHORT).show()
            return false
        }

        var baodanSize = 0
        for (i in 0 until baodanList.size) {
            val dto = baodanList[i]
            if (!TextUtils.isEmpty(dto.imgUrl)) {
                baodanSize++
            }
        }
        if (baodanSize == 0) {
            Toast.makeText(this, "请上传保单照片", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /**
     * 上传图片
     */
    private fun okHttpPostFiles() {
        if (!checkPost()) {
            return
        }
        showProgressDialog()
        val url = "${CONST.BASE_URL}disaster_proof/multipartupload"
        val builder = MultipartBody.Builder()
        builder.addFormDataPart("token", MyApplication.TOKEN)

        if (clZhizhao.visibility == View.GONE) {
            for (i in 0 until cardList.size) {
                val data = cardList[i]
                if (!TextUtils.isEmpty(data.imgUrl)) {
                    val file = File(data.imgUrl)
                    if (file.exists()) {
                        builder.addFormDataPart("idPicfiles", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                    }
                }
            }
        } else {
            for (i in 0 until zhizhaoList.size) {
                val data = zhizhaoList[i]
                if (!TextUtils.isEmpty(data.imgUrl)) {
                    val file = File(data.imgUrl)
                    if (file.exists()) {
                        builder.addFormDataPart("businessPicfiles", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                    }
                }
            }
        }
        for (i in 0 until disList.size) {
            val data = disList[i]
            if (!TextUtils.isEmpty(data.imgUrl)) {
                val file = File(data.imgUrl)
                if (file.exists()) {
                    builder.addFormDataPart("disasterPicfiles", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                }
            }
        }
        for (i in 0 until baodanList.size) {
            val data = baodanList[i]
            if (!TextUtils.isEmpty(data.imgUrl)) {
                val file = File(data.imgUrl)
                if (file.exists()) {
                    builder.addFormDataPart("policyPicfiles", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                }
            }
        }

        val body: RequestBody = builder.build()
        Thread {
            OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        dismissProgressDialog()
                        Toast.makeText(this@WeatherProveSubmitActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        dismissProgressDialog()
                        Log.e("submit-file", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("result")) {
                                    val objRes = obj.getJSONObject("result")
                                    if (!objRes.isNull("idPics")) {
                                        val array = objRes.getJSONArray("idPics")
                                        for (i in 0 until array.length()) {
                                            val pic = array.get(i).toString()
                                            cardPics += if (i == array.length()-1) {
                                                pic
                                            } else {
                                                "$pic|"
                                            }
                                        }
                                    }
                                    if (!objRes.isNull("businessPics")) {
                                        val array = objRes.getJSONArray("businessPics")
                                        for (i in 0 until array.length()) {
                                            val pic = array.get(i).toString()
                                            zhizhaoPics += if (i == array.length()-1) {
                                                pic
                                            } else {
                                                "$pic|"
                                            }
                                        }
                                    }
                                    if (!objRes.isNull("disasterPics")) {
                                        val array = objRes.getJSONArray("disasterPics")
                                        for (i in 0 until array.length()) {
                                            val pic = array.get(i).toString()
                                            disPics += if (i == array.length()-1) {
                                                pic
                                            } else {
                                                "$pic|"
                                            }
                                        }
                                    }
                                    if (!objRes.isNull("policyPics")) {
                                        val array = objRes.getJSONArray("policyPics")
                                        for (i in 0 until array.length()) {
                                            val pic = array.get(i).toString()
                                            baodanPics += if (i == array.length()-1) {
                                                pic
                                            } else {
                                                "$pic|"
                                            }
                                        }
                                    }

                                    okHttpPostContent()
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
     * 报送内容
     */
    private fun okHttpPostContent() {
        if (!checkPost()) {
            return
        }
        showProgressDialog()
        val url = "${CONST.BASE_URL}disaster_proof/save"
        val param  = JSONObject()
        param.put("token",MyApplication.TOKEN)
        val info = JSONObject()
        info.put("userId", MyApplication.UID)
        info.put("category", category)
        info.put("contactsName", etName.text.toString())
        info.put("telephone", etPhone.text.toString())
        info.put("type", type)
        if (clUse.visibility == View.GONE) {
            info.put("companyName", companyName)
            info.put("policyNumber", etNum.text.toString())
        } else {
            info.put("purpose", etUse.text.toString())
        }
        info.put("disasterName", typeName)
        info.put("disasterTime", tvTime.text.toString())
        info.put("disasterArea", tvPosition.text.toString())
        info.put("disasterSubject", bodyName)
        if (!TextUtils.isEmpty(etDesc.text.toString())) {
            info.put("disasterDesc ", etDesc.text.toString())
        }
        info.put("lat", lat)
        info.put("lon", lng)
        info.put("idPic", cardPics)
        info.put("business_pic", zhizhaoPics)
        info.put("disasterPic", disPics)
        info.put("policyPic", baodanPics)
        param.put("paramInfo", info)
        val json : String = param.toString()
        Log.e("submit-content", json)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, json)
        Thread {
            OkHttpUtil.enqueue(Request.Builder().post(body).url(url).addHeader("Authorization", MyApplication.TOKEN).build(), object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@WeatherProveSubmitActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return
                    }
                    val result = response.body!!.string()
                    runOnUiThread {
                        dismissProgressDialog()
                        Log.e("submit-content", result)
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                val obj = JSONObject(result)
                                if (!obj.isNull("status")) {
                                    val status = obj.getString("status")
                                    if (TextUtils.equals(status, "success")) {
                                        Toast.makeText(this@WeatherProveSubmitActivity, "提交成功！", Toast.LENGTH_SHORT).show()

                                        //刷新气象证明广播
                                        val i = Intent()
                                        i.action = CONST.BROADCAST_REFRESH_PROVE
                                        sendBroadcast(i)

                                        setResult(RESULT_OK)
                                        finish()
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