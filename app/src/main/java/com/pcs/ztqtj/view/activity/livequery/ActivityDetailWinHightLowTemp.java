package com.pcs.ztqtj.view.activity.livequery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjProDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjProUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown.FltjZd;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjLowZdzDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjLowZdzDown.WdtjLowZdz;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProLowDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProLowUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProMaxDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProMaxUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjZdzDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjZdzDown.WdtjZdz;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureHight;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureLow;
import com.pcs.ztqtj.control.adapter.livequery.AdapterWind;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 高低温风速详情
 */
public class ActivityDetailWinHightLowTemp extends FragmentActivityZtqBase {
    private MyListView livequery_auto;
    private MyListView livequery_24_hour;
    private TextView description_title_search2, description_title_search3;
    private ImageButton btn_right;
    private String whatType;
    private MyReceiver receiver = new MyReceiver();

    private int whatColumn = -1;
    // 风速
    private List<FltjZd> windAutoList;
    private List<FltjZd> windcurrentList;
    private AdapterWind windAutoAtper;
    private AdapterWind windCurrentAtper;

    // 低温
    private List<WdtjLowZdz> lowAutoTemper;
    private List<WdtjLowZdz> lowCurrentTemper;
    private AdapterTempertureLow lowAutoListViewAdatper;
    private AdapterTempertureLow lowCurrentListViewAdatper;

    // 高温
    private List<WdtjZdz> hightAutoTemper;
    private List<WdtjZdz> hightCurrentTemper;
    private AdapterTempertureHight hightAutoListViewAdatper;
    private AdapterTempertureHight hightCurrentListViewAdatper;

    private PackWdtjProLowUp packWdtjProLowUp = new PackWdtjProLowUp();
    private PackWdtjProMaxUp packWdtjProMaxUp = new PackWdtjProMaxUp();
    private PackFltjProUp packFltjProUp = new PackFltjProUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wind);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        setTitleText(getIntent().getStringExtra("title"));
        whatType = getIntent().getStringExtra("type");
        initView();
        initData();
        initEvent();

    }

    private void initEvent() {
        livequery_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                } else {
                    Intent intent = new Intent(ActivityDetailWinHightLowTemp.this, ActivityLiveQueryDetail.class);
                    String stationName = "";
                    if (whatType.equals("wind")) {
                        FltjZd bean = windAutoList.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "wind");
                    } else if (whatType.equals("hight_t")) {
                        WdtjZdz bean = hightAutoTemper.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "temp");
                    } else if (whatType.equals("low_t")) {
                        WdtjLowZdz bean = lowAutoTemper.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "temp");
                    }
                    intent.putExtra("stationName", stationName);

                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView() {
        livequery_auto = (MyListView) findViewById(R.id.livequery_auto_min_table);
        livequery_24_hour = (MyListView) findViewById(R.id.livequery_day_min_table);
        description_title_search2 = (TextView) findViewById(R.id.description_title_search2);
        description_title_search3 = (TextView) findViewById(R.id.description_title_search3);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        setBtnRight(R.drawable.btn_refresh, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 动画
                btn_right.setClickable(false);
                Animation animation = AnimationUtils.loadAnimation(ActivityDetailWinHightLowTemp.this, R.anim.rotate_repeat_1000);
                LinearInterpolator lin = new LinearInterpolator();
                animation.setInterpolator(lin);
                btn_right.startAnimation(animation);
                handler.sendEmptyMessageDelayed(0, 1500);
                // initData(w);
                if (whatType.equals("wind")) {
                    upWindData();
                } else if (whatType.equals("hight_t")) {
                    updateHightTemperData();
                } else if (whatType.equals("low_t")) {
                    updateLowTemperData();
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopRefreshBtnAnim();
        }
    };

    /**
     * 停止刷新按钮动画
     */
    private void stopRefreshBtnAnim() {

        btn_right.setClickable(true);
        // 动画
        btn_right.clearAnimation();
    }

    private void initData() {
        if (whatType.equals("wind")) {
            initDataWind();
        } else if (whatType.equals("hight_t")) {
            initDataHightTemper();
        } else if (whatType.equals("low_t")) {
            initDataLowTemper();
        }
    }

    private void updateLowTemperData() {
        WdtjLowZdz titletemp = new PackWdtjLowZdzDown().new WdtjLowZdz();
        titletemp.county = "站点";
        titletemp.time = "日期/时段";
        titletemp.min_wd = "气温°C";
        lowAutoTemper.add(titletemp);
        lowCurrentTemper.add(titletemp);
        try {
            packWdtjProLowUp.type = "1";
            PackWdtjProLowDown  wdtjProLowDown = (PackWdtjProLowDown) PcsDataManager.getInstance().getNetPack(packWdtjProLowUp.getName());
            if (wdtjProLowDown != null) {
                dismissProgressDialog();
                lowAutoTemper.clear();
                lowAutoTemper.add(titletemp);
                lowAutoTemper.addAll(wdtjProLowDown.datalist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            packWdtjProLowUp.type = "2";
            PackWdtjProLowDown   wdtjProLow24Down = (PackWdtjProLowDown) PcsDataManager.getInstance().getNetPack(packWdtjProLowUp.getName());
            if (null != wdtjProLow24Down) {
                dismissProgressDialog();
                lowCurrentTemper.clear();
                lowCurrentTemper.add(titletemp);
                lowCurrentTemper.addAll(wdtjProLow24Down.datalist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lowAutoListViewAdatper.notifyDataSetChanged();
        lowCurrentListViewAdatper.notifyDataSetChanged();
    }

    /**
     * 低温详情
     */
    private void initDataLowTemper() {
        description_title_search2.setText("全省实况气温最低统计表");
        description_title_search3.setText("全省近24小时最低排名");
        lowAutoTemper = new ArrayList<WdtjLowZdz>();
        lowCurrentTemper = new ArrayList<WdtjLowZdz>();
        lowAutoListViewAdatper = new AdapterTempertureLow(ActivityDetailWinHightLowTemp.this, lowAutoTemper);
        lowCurrentListViewAdatper = new AdapterTempertureLow(ActivityDetailWinHightLowTemp.this, lowCurrentTemper);
        livequery_auto.setAdapter(lowAutoListViewAdatper);
        livequery_24_hour.setAdapter(lowCurrentListViewAdatper);
        request(3);
    }

    /**
     * 最高温详情
     */
    private void initDataHightTemper() {
        description_title_search2.setText("全省高温实况统计表");
        description_title_search3.setText("全省近24小时最高气温排名");
        hightAutoTemper = new ArrayList<WdtjZdz>();
        hightCurrentTemper = new ArrayList<WdtjZdz>();
        hightAutoListViewAdatper = new AdapterTempertureHight(ActivityDetailWinHightLowTemp.this, hightAutoTemper);
        hightCurrentListViewAdatper = new AdapterTempertureHight(ActivityDetailWinHightLowTemp.this, hightCurrentTemper);
        livequery_auto.setAdapter(hightAutoListViewAdatper);
        livequery_24_hour.setAdapter(hightCurrentListViewAdatper);
        request(2);
    }

    private void updateHightTemperData() {
        WdtjZdz titleMaxRain = new PackWdtjZdzDown().new WdtjZdz();
        titleMaxRain.county = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.max_wd = "气温℃";
        hightAutoTemper.add(titleMaxRain);
        hightCurrentTemper.add(titleMaxRain);
        PackWdtjProMaxDown wdtjProMaxDown = new PackWdtjProMaxDown();
        try {
            packWdtjProMaxUp.type = "1";
            wdtjProMaxDown = (PackWdtjProMaxDown) PcsDataManager.getInstance().getNetPack(packWdtjProMaxUp.getName());
            if (null != wdtjProMaxDown) {
                dismissProgressDialog();
                hightAutoTemper.clear();
                hightAutoTemper.add(titleMaxRain);
                for (int i = 0; i < wdtjProMaxDown.datalist.size(); i++) {
                    hightAutoTemper.add(wdtjProMaxDown.datalist.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PackWdtjProMaxDown wdtjProMax24Down = new PackWdtjProMaxDown();

        try {
            packWdtjProMaxUp.type = "2";
            wdtjProMax24Down = (PackWdtjProMaxDown) PcsDataManager.getInstance().getNetPack(packWdtjProMaxUp.getName());
            if (null != wdtjProMax24Down) {
                hightCurrentTemper.clear();
                hightCurrentTemper.add(titleMaxRain);
                for (int i = 0; i < wdtjProMax24Down.datalist.size(); i++) {
                    hightCurrentTemper.add(wdtjProMax24Down.datalist.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hightAutoListViewAdatper.notifyDataSetChanged();
        hightCurrentListViewAdatper.notifyDataSetChanged();
    }

    /**
     * 风速详情
     */
    private void initDataWind() {
        description_title_search2.setText("全省站点当前瞬时风速排名");
        description_title_search3.setText("全省站点24小时极大风速统计");
        windAutoList = new ArrayList<>();
        windcurrentList = new ArrayList<>();
        windAutoAtper = new AdapterWind(ActivityDetailWinHightLowTemp.this, windAutoList);
        windCurrentAtper = new AdapterWind(ActivityDetailWinHightLowTemp.this, windcurrentList);
        livequery_auto.setAdapter(windAutoAtper);
        livequery_24_hour.setAdapter(windCurrentAtper);
        request(4);
    }

    private void upWindData() {
        // 列表头部说明
        FltjZd titleMaxRain = new PackFltjZdDown().new FltjZd();
        titleMaxRain.county = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.winddirection = "风向";
        titleMaxRain.windFengLi = "风力";
        titleMaxRain.windpower = "风速m/s";
        windAutoList.add(titleMaxRain);
        windcurrentList.add(titleMaxRain);
        // 风况查询—省级排名 前9名 24小时
        PackFltjProDown fltjProDown = new PackFltjProDown();
        try {
            packFltjProUp.type = "1";
            fltjProDown = (PackFltjProDown) PcsDataManager.getInstance().getNetPack(packFltjProUp.getName());
            if (null != fltjProDown) {
                dismissProgressDialog();
                windAutoList.clear();
                windAutoList.add(titleMaxRain);
                for (int i = 0; i < fltjProDown.datalist.size(); i++) {
                    windAutoList.add(fltjProDown.datalist.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PackFltjProDown fltjPro24Down = new PackFltjProDown();
        try {
            packFltjProUp.type = "2";
            fltjPro24Down = (PackFltjProDown) PcsDataManager.getInstance().getNetPack(packFltjProUp.getName());
            if (null != fltjPro24Down) {
                windcurrentList.clear();
                windcurrentList.add(titleMaxRain);
                for (int i = 0; i < fltjPro24Down.datalist.size(); i++) {
                    windcurrentList.add(fltjPro24Down.datalist.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        windAutoAtper.notifyDataSetChanged();
        windCurrentAtper.notifyDataSetChanged();
    }

    /**
     * 请求数据，
     *
     * @param message 4为风速 3为低温 2为高温
     */
    private void request(int message) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        whatColumn = message;
        switch (message) {
            case 4:
//			风况
                PackFltjProUp fltjPro24Up = new PackFltjProUp();
                // 风况查询—省级排名 前9名 24小时
                fltjPro24Up = new PackFltjProUp();
                fltjPro24Up.type = "2";
                PcsDataDownload.addDownload(fltjPro24Up);
                PackFltjProUp fltjProUp;// 风况查询—省级排名 前9名
                // 风况查询—省级排名 前9名
                fltjProUp = new PackFltjProUp();
                fltjProUp.type = "1";
                PcsDataDownload.addDownload(fltjProUp);
                break;
            case 3:
//			低温
                PackWdtjProLowUp wdtjProLowUp = new PackWdtjProLowUp();
                wdtjProLowUp.type = "1";
                PcsDataDownload.addDownload(wdtjProLowUp);
                PackWdtjProLowUp wdtjProLow24Up = new PackWdtjProLowUp();
                wdtjProLow24Up.type = "2";
                PcsDataDownload.addDownload(wdtjProLow24Up);
                break;
            case 2:
//			高温
                PackWdtjProMaxUp wdtjProUp = new PackWdtjProMaxUp();
                wdtjProUp.type = "1";
                PcsDataDownload.addDownload(wdtjProUp);
                PackWdtjProMaxUp wdtjPro24Up = new PackWdtjProMaxUp();
                wdtjPro24Up.type = "2";
                PcsDataDownload.addDownload(wdtjPro24Up);
                break;
        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            switch (whatColumn) {
                case 4:
                    upWindData();
                    whatColumn = -1;
                    break;
                case 3:
                    updateLowTemperData();
                    whatColumn = -1;
                    break;
                case 2:
                    updateHightTemperData();
                    whatColumn = -1;
                    break;
            }
        }
    }
}