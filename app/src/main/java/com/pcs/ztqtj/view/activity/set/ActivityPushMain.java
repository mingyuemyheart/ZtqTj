package com.pcs.ztqtj.view.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushHelpDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushHelpUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterZTQFamily;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置推送类
 *
 * @author tya
 */
public class ActivityPushMain extends FragmentActivityZtqBase {

    private ListView listview = null;
    private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
    private CheckBox cb;
    private TextView tvTips;
    private MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_main);
        setTitleText("推送设置");
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.list_push);
        cb = (CheckBox) findViewById(R.id.cb);
        tvTips = (TextView) findViewById(R.id.tv_push_tips);
    }

    public void initListener() {
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                switch (position) {
                    case 0:
                        // 气象预警推送
                        gotoAcitvity(ActivityWeatherBasedWarning.class, listData
                                .get(0).get("t"));
                        break;
                    case 1:
                        // 实况预警推送
                        gotoAcitvity(AcitvityWarnLivePush.class, listData.get(1)
                                .get("t"));
                        break;
                    case 2:
                        // 天气预报推送
                        gotoAcitvity(ActivityWeatherPush.class, listData.get(2)
                                .get("t"));
                        break;
                    case 3:
                        // 温馨提示推送
                        gotoAcitvity(ActivityReminder.class,
                                listData.get(3).get("t"));
                        break;
                    case 4:
                        // 气象服务推送
                        gotoAcitvity(ActivityServicePushSet.class, listData.get(4).get("t"));
                        break;
                    default:
                        break;
                }
            }
        });
        cb.setChecked(LocalDataHelper.getRingtone(this));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalDataHelper.savePushDialogRingtone(ActivityPushMain.this, isChecked);
            }
        });
    }

    private void initData() {
        receiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        Map<String, String> itemTrailer = new HashMap<String, String>();
        itemTrailer.put("t", "预警信息推送");
        itemTrailer.put("i", R.drawable.icon_set_item_warnpush + "");
        Map<String, String> itemWarn = new HashMap<String, String>();
        itemWarn.put("t", "实况告警推送");
        itemWarn.put("i", R.drawable.icon_set_item_warnlive + "");
        Map<String, String> itemWeahter = new HashMap<String, String>();
        itemWeahter.put("t", "天气预报推送");
        itemWeahter.put("i", R.drawable.icon_set_item_weatherwarn + "");
        Map<String, String> itemReminder = new HashMap<String, String>();
        itemReminder.put("t", "温馨提示推送");
        itemReminder.put("i", R.drawable.icon_set_item_reminder + "");

        Map<String, String> itemServer = new HashMap<String, String>();
        itemServer.put("t", "专项服务推送");
        itemServer.put("i", R.drawable.icon_set_item_reminder + "");

        listData.add(itemTrailer);
        listData.add(itemWarn);
        listData.add(itemWeahter);
        listData.add(itemReminder);
        listData.add(itemServer);

        AdapterZTQFamily adapter = new AdapterZTQFamily(this, listData);
        listview.setAdapter(adapter);
        req();
    }

    private void req() {
        showProgressDialog();
        PackPushHelpUp up = new PackPushHelpUp();
        PcsDataDownload.addDownload(up);
    }

    /**
     * 跳转到下一级页面
     *
     * @param intentactivity 跳转的class
     * @param titletext      绑定的标题
     */
    private void gotoAcitvity(Class intentactivity, String titletext) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        bundle.putSerializable("city", cityMain);
        bundle.putString("title", titletext);
        intent.putExtras(bundle);
        intent.setClass(this, intentactivity);
        startActivity(intent);
        rightInAnimation();
    }

    private void gotoHelp(String url) {
        Intent intent = new Intent(this, ActivityGuideDetail.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "推送设置");
        intent.putExtra("is_show_share", "0");
        startActivity(intent);
    }

    /**
     * 页面切换动画
     */
    private void rightInAnimation() {
        this.overridePendingTransition(R.anim.slide_right_in,
                R.anim.slide_left_out);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(PackPushHelpUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                final PackPushHelpDown down = (PackPushHelpDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null || down.helpList == null || down.helpList.size() == 0) {
                    tvTips.setVisibility(View.GONE);
                    return;
                }
                final String url = down.helpList.get(0).html_url;
                if(!TextUtils.isEmpty(url)) {
                    tvTips.setVisibility(View.VISIBLE);
                    tvTips.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(url)) {
                                gotoHelp(url);
                            }
                        }
                    });
                } else {
                    tvTips.setVisibility(View.GONE);
                }
            }
        }
    }

}
