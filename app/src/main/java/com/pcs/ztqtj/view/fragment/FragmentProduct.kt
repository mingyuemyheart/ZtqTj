package com.pcs.ztqtj.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import com.pcs.ztqtj.R
import com.pcs.ztqtj.control.adapter.AdapterProductProduct
import com.pcs.ztqtj.control.tool.ShareTools
import com.pcs.ztqtj.model.ZtqCityDB
import com.pcs.ztqtj.util.ColumnDto
import com.pcs.ztqtj.util.CommonUtil
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery
import com.pcs.ztqtj.view.activity.product.ActivityOceanMap
import com.pcs.ztqtj.view.activity.product.ActivitySatelliteCloudChart
import com.pcs.ztqtj.view.activity.product.ActivityWeatherRadar
import com.pcs.ztqtj.view.activity.product.ActivityWeatherSummary
import com.pcs.ztqtj.view.activity.product.lightning.ActivityLightningMonitor
import com.pcs.ztqtj.view.activity.product.numericalforecast.ActivityDetailCenterPro
import com.pcs.ztqtj.view.activity.product.numericalforecast.ActivityNumericalForecast
import com.pcs.ztqtj.view.activity.product.situation.ActivitySituation
import com.pcs.ztqtj.view.activity.product.typhoon.ActivityTyphoon
import com.pcs.ztqtj.view.activity.set.ActivityProgramerManager
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH
import kotlinx.android.synthetic.main.fragment_product.*

/**
 * 监测预报
 */
class FragmentProduct : BaseFragment() {

    private val dataList1: ArrayList<ColumnDto> = ArrayList()
    private var mAdapter1: AdapterProductProduct? = null
    private val dataList2: ArrayList<ColumnDto> = ArrayList()
    private var mAdapter2: AdapterProductProduct? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initGridView1()
        initGridView2()
    }

    /**
     * 初始化UI
     */
    private fun initView() {
        product_top_right_button.setOnClickListener {
            startActivity(Intent(activity, ActivityProgramerManager::class.java))
        }
        ShareTools.getInstance(activity).reqShare()

        val dto: ColumnDto = arguments!!.getParcelable("data")
        if (dto != null) {
            for (i in 0 until dto.childList.size) {
                val data = dto.childList[i]
                if (i == 0) {
                    if (data.dataName != null) {
                        tvZH.text = data.dataName
                    }
                    dataList1.clear()
                    dataList1.addAll(data.childList)
                } else if (i == 1) {
                    if (data.dataName != null) {
                        tvFX.text = data.dataName
                    }
                    dataList2.clear()
                    dataList2.addAll(data.childList)
                }
            }
            if (mAdapter1 != null) {
                mAdapter1!!.notifyDataSetChanged()
            }
            if (mAdapter2 != null) {
                mAdapter2!!.notifyDataSetChanged()
            }
        }
    }

    private fun initGridView1() {
        mAdapter1 = AdapterProductProduct(activity, dataList1)
        gridView1.adapter = mAdapter1
        gridView1.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val data = dataList1[position]
            if (!CommonUtil.isCanAccess(data.flag)) {
                return@OnItemClickListener
            }
            intentActivity(data)
        }
    }

    private fun initGridView2() {
        mAdapter2 = AdapterProductProduct(activity, dataList2)
        gridView2.adapter = mAdapter2
        gridView2.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val data = dataList2[position]
            if (!CommonUtil.isCanAccess(data.flag)) {
                return@OnItemClickListener
            }
            intentActivity(data)
        }
    }

    private fun intentActivity(data: ColumnDto) {
        var intent: Intent? = null
        when(data.dataCode) {
            "10103020101" -> {//雷达回波
                intent = Intent(activity, ActivityWeatherRadar::class.java)
            }
            "10103020102" -> {//卫星云图
                intent = Intent(activity, ActivitySatelliteCloudChart::class.java)
            }
            "10103020103" -> {//台风路径
                intent = Intent(activity, ActivityTyphoon::class.java)
            }
            "10103020104" -> {//实况查询
                intent = Intent(activity, ActivityLiveQuery::class.java)
                val cityMain = ZtqCityDB.getInstance().cityMain
                val bundle = Bundle()
                bundle.putSerializable("city", cityMain)
                intent.putExtras(bundle)
            }
            "10103020105" -> {//整点天气
                intent = Intent(activity, ActivityLiveQueryDetail::class.java)
                intent.putExtra("stationName", "")
                intent.putExtra("item", "temp")
            }
            "10103020106" -> {// 闪电监测
                intent = Intent(activity, ActivityLightningMonitor::class.java)
            }
            "10103020107" -> {// 空气质量
                val packCity = ZtqCityDB.getInstance().cityMain
                if (packCity?.ID == null) {
                    return
                }
                intent = if (packCity.isFjCity) {
                    Intent(activity, ActivityAirQualitySH::class.java)
                } else {
                    ActivityAirQualityQuery.setCity(packCity.ID, packCity.CITY)
                    Intent(activity, ActivityAirQualityQuery::class.java)
                }
                intent.putExtra("id", packCity.ID)
                intent.putExtra("name", packCity.NAME)
            }
            "10103020108" -> {// 天气形势
                intent = Intent(activity, ActivitySituation::class.java)
            }
            "10103020109" -> {// 城市积水
                intent = Intent(activity, ActivityWebView::class.java)
                intent.putExtra("url", "https://tianjin.welife100.com/Monitor/monitor")
                intent.putExtra("shareContent", data.dataName)
            }
            "10103020201" -> {// 气象报告
                intent = Intent(activity, ActivityWeatherSummary::class.java)
            }
            "10103020202" -> {// 指导预报
                intent = Intent(activity, ActivityDetailCenterPro::class.java)
                intent.putExtra("t", data.dataName)
                intent.putExtra("c", "106")
            }
            "10103020203" -> {// 模式预报
                intent = Intent(activity, ActivityNumericalForecast::class.java)
            }
            "10103020204" -> {// 海洋气象
                intent = Intent(activity, ActivityOceanMap::class.java)
            }
        }
        intent!!.putExtra("title", data.dataName)
        startActivity(intent)
    }

//    DialogTwoButton dialogLogin = null;
//    private void showLoginDialog() {
//        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_message, null);
//        view.setText("该功能仅限内部人员使用，请先登录！");
//        dialogLogin = new DialogTwoButton(getContext(), view, "返回", "登录", new DialogFactory.DialogListener() {
//            @Override
//            public void click(String str) {
//                dialogLogin.dismiss();
//                if (str.equals("返回")) {
//
//                } else if (str.equals("登录")) {
//                    Intent intent = new Intent(getContext(), ActivityPhotoLogin.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        dialogLogin.show();
//    }
//
//    private DialogOneButton dialogPermission = null;
//    private void showNoPermission() {
//        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_message, null);
//        view.setText("暂无此权限！");
//        dialogPermission = new DialogOneButton(getContext(), view, "确定", new DialogFactory.DialogListener() {
//            @Override
//            public void click(String str) {
//                dialogPermission.dismiss();
//            }
//        });
//        dialogPermission.show();
//    }
//
//    private void checkPermission(String channelId, Class<?> cls) {
//        if(ZtqCityDB.getInstance().isLoginService()) {
//            if(MyApplication.LIMITINFO.contains(channelId)) {
//                startActivity(new Intent(getContext(), cls));
//            } else {
//                showNoPermission();
//            }
//        } else {
//            showLoginDialog();
//        }
//    }

}
