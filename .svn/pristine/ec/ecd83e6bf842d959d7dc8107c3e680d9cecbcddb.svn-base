package com.pcs.ztqtj.view.activity.push;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterWarningTagType;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushQueryTagTypeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushQueryTagTypeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushSetTagTypeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushSetTagTypeUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/8/10.
 */

public class ActivityWarningTypeDialog extends Activity {

    private PackPushQueryTagTypeUp packUp = new PackPushQueryTagTypeUp();
    private PackPushSetTagTypeUp setTagTypeUp = new PackPushSetTagTypeUp();
    private TextView tvTitle;
    private GridView gridView;
    private ProgressBar progressBar;
    private AdapterWarningTagType adapter;
    private Button btnSubmit, btnClose;
    private MyReceiver receiver = new MyReceiver();
    private String type = "1";
    private String tag = "";
    private List<PackPushQueryTagTypeDown.PushTagTypeBean> listData = new ArrayList<>();
    private static final int MARGIN_WIDTH = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_warning_type);
        setFinishOnTouchOutside(false);
        initScreenSize();
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels- CommUtils.Dip2Px(this, MARGIN_WIDTH);
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        gridView = (GridView) findViewById(R.id.gridview_type);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnClose = (Button) findViewById(R.id.btn_close);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        isShowProgress(true);
    }

    private void initEvent() {
        btnSubmit.setOnClickListener(clickListener);
        btnClose.setOnClickListener(clickListener);
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        type = getIntent().getStringExtra("type");
        tag = getIntent().getStringExtra("tag");
        setTitle();
        adapter = new AdapterWarningTagType(listData);
        gridView.setAdapter(adapter);
        reqType();
    }

    private void isShowProgress(boolean isShow) {
        if(isShow) {
            progressBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    private void setTitle() {
        String title = "";
        if(type.equals("1")) {
            title = "请选择预警类型：";
        } else if(type.equals("2")) {
            title = "请选择预警等级：";
        }
        tvTitle.setText(title);
    }

    /**
     * 提交
     */
    private void submit() {
        String str = "";
        for(PackPushQueryTagTypeDown.PushTagTypeBean bean : listData) {
            str += bean.ischeck + ",";
        }
        Log.e("TAG", str);
        reqSetTagType();
    }

    /**
     * 关闭
     */
    private void close() {
        finish();
    }

    private List<String> getTagList() {
        List<String> result = new ArrayList<>();
        for(PackPushQueryTagTypeDown.PushTagTypeBean bean : listData) {
            if(bean.ischeck.equals("1")) {
                result.add(bean.id);
            }
        }
        return result;
    }

    /**
     * 请求标签类型
     */
    private void reqType() {
        packUp.tag = tag;
        packUp.type = type;
        PcsDataDownload.addDownload(packUp);
    }

    private void reqSetTagType() {
        setTagTypeUp.tag = tag;
        setTagTypeUp.type = type;
        setTagTypeUp.q_list = getTagList();
        PcsDataDownload.addDownload(setTagTypeUp);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_submit :
                    submit();
                    break;
                case R.id.btn_close :
                    close();
                    break;
            }
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(nameStr.equals(packUp.getName())) {
                isShowProgress(false);
                PackPushQueryTagTypeDown down = (PackPushQueryTagTypeDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                listData.clear();
                listData.addAll(down.info_list);
                adapter.notifyDataSetChanged();
            }
            if(nameStr.equals(setTagTypeUp.getName())) {
                PackPushSetTagTypeDown down = (PackPushSetTagTypeDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                if(down.result.equals("1")) {
                    close();
                } else {
                    Toast.makeText(ActivityWarningTypeDialog.this, "提交失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
