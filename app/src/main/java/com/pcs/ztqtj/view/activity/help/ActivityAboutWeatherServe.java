package com.pcs.ztqtj.view.activity.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterHelpOrgList;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.OrgInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.OrgTelInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceOrgSearchDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceOrgSearchUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceOrgTelSearchDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceOrgTelSearchUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 气象服务帮助：气象服务单位联系方式
 *
 * @author chenjh
 */
public class ActivityAboutWeatherServe extends FragmentActivityZtqBase {
    private MyReceiver receiver = new MyReceiver();
    private PackServiceOrgTelSearchUp packServiceOrgTelSearchUp = new PackServiceOrgTelSearchUp();
    private int position = 0;
    private String titleText = "";
    private View textScroll;
    private TextView textInfo;
    private ListView myListView;
    private LinearLayout listview_layout;

    private TextView addressTextView;
    private TextView telTextView;
    private ImageButton telBtn;

    private List<OrgInfo> orgList = new ArrayList<OrgInfo>();// 从服务端获取的气象服务单位
    private AdapterHelpOrgList adapter;

    private DialogOneButton updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutweatherserve);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        textScroll = findViewById(R.id.text_scroll);
        textInfo = (TextView) findViewById(R.id.text_info);
        myListView = (ListView) findViewById(R.id.mylistviw);
        listview_layout = (LinearLayout) findViewById(R.id.listview_layout);
    }

    private void initEvent() {
        myListView.setOnItemClickListener(myOnItemClickListener);
    }

    public void initData() {
        Intent intent = getIntent();
        titleText = intent.getStringExtra("title");
        position = intent.getIntExtra("position", 0);
        setTitleText(titleText);
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        switch (position) {
            case 0:
                listview_layout.setVisibility(View.GONE);
                textInfo.setText(getString(R.string.weatherserve_info));
                break;
            case 1:
                listview_layout.setVisibility(View.GONE);
                textInfo.setText(getString(R.string.weather_product_authority_info));
                break;
            case 3:
                textScroll.setVisibility(View.GONE);
                getLocalData();
                break;
        }
    }

    private void getLocalData() {
        PackServiceOrgSearchUp packServiceOrgSearchUp = new PackServiceOrgSearchUp();
        PackServiceOrgSearchDown packServiceOrgSearchDown = (PackServiceOrgSearchDown) PcsDataManager.getInstance().getNetPack(packServiceOrgSearchUp.getName());
        if (packServiceOrgSearchDown == null) {
            return;
        }
        orgList = packServiceOrgSearchDown.orgSearchList;

        if (orgList.size() > 0) {

            adapter = new AdapterHelpOrgList(getApplication(), orgList);
            myListView.setAdapter(adapter);
            listview_layout.setVisibility(View.VISIBLE);
        }

    }

    private void getOrgTelInfo(OrgInfo info) {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        if (packServiceOrgTelSearchUp == null) {
            packServiceOrgTelSearchUp = new PackServiceOrgTelSearchUp();
        }
        packServiceOrgTelSearchUp.id = info.org_id;
        PcsDataManager.getInstance().removeLocalData(packServiceOrgTelSearchUp.getName());
        PcsDataDownload.addDownload(packServiceOrgTelSearchUp);
    }

    private OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            getOrgTelInfo(orgList.get(position));
        }
    };

    /**
     * 弹出框
     *
     * @param orgTelInfo
     */
    private void showOrgTelView(OrgTelInfo orgTelInfo) {

        final String address = orgTelInfo.address;
        final String mobile = orgTelInfo.mobile;
        final String tel = orgTelInfo.tel;

        if (updateDialog == null) {
            View view = LayoutInflater.from(ActivityAboutWeatherServe.this)
                    .inflate(R.layout.setfragmetnt_dialog_tel_layout, null);

            addressTextView = (TextView) view.findViewById(R.id.address_tv);
            telTextView = (TextView) view.findViewById(R.id.tel_tv);

            telBtn = (ImageButton) view.findViewById(R.id.tel_btn);
            updateDialog = new DialogOneButton(ActivityAboutWeatherServe.this,
                    view, "确定", new DialogListener() {
                @Override
                public void click(String str) {
                    updateDialog.dismiss();
                }
            });
        }
        updateDialog.setTitle(orgTelInfo.name);
        addressTextView.setText(address);
        telTextView.setText(tel);

        if (!TextUtils.isEmpty(tel)) {
            telTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    call_phone(tel);
                }
            });
            telBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    call_phone(tel);
                }
            });
        } else {
            telBtn.setVisibility(View.GONE);
        }

        updateDialog.show();
    }

    private void call_phone(String tel) {
        // 第一种方式：到了拨号界面，但是实际的拨号是由用户点击实现的。
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + tel);
        intent.setData(data);
        startActivity(intent);

        // //第二种方式：直接拨打了输入的号码
        // Intent intent = new Intent(Intent.ACTION_CALL);
        // Uri data = Uri.parse("tel:" + tel);
        // intent.setData(data);
        // startActivity(intent);
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            if (packServiceOrgTelSearchUp.getName().equals(name)) {//
                PackServiceOrgTelSearchDown packServiceOrgTelSearchDown = (PackServiceOrgTelSearchDown) PcsDataManager.getInstance().getNetPack(name);
                if (packServiceOrgTelSearchDown == null) {
                    return;
                }

                showOrgTelView(packServiceOrgTelSearchDown.orgTelInfo);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
