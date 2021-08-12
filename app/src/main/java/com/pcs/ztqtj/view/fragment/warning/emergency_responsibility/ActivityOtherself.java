package com.pcs.ztqtj.view.fragment.warning.emergency_responsibility;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZRPersonInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackZRPersonInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjMyReport;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.disaster.AdatperReporting;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhoneListAndHelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 * chen_jx
 */

public class ActivityOtherself extends FragmentActivityZtqWithPhoneListAndHelp implements View.OnClickListener {

    private TextView tv_otherself_status, tv_otherself_time;
    private ListView lv_otherself;
    private AdatperReporting adapter;
    protected ImageButton btnHelp = null;
    private int year, month;
    private ArrayList<String> mlist_stutus;
    private String name, phone, user_id;
    private TextView tv_other_name, tv_other_phone, tv_respon_list, tv_other_search;
    private PackZRPersonInfoUp packZRPersonInfoUp = new PackZRPersonInfoUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherself);
        setTitleText("应急责任人名单");
        initView();
        initData();
        initEvent();
    }

    public void initView() {
        tv_otherself_status = (TextView) findViewById(R.id.tv_otherself_status);
        tv_otherself_status.setOnClickListener(this);
        tv_otherself_time = (TextView) findViewById(R.id.tv_otherself_time);
        tv_otherself_time.setOnClickListener(this);
        lv_otherself = (ListView) findViewById(R.id.lv_otherself);
        btnHelp = (ImageButton) findViewById(R.id.btn_right);
        btnHelp.setVisibility(View.GONE);
        tv_other_name = (TextView) findViewById(R.id.tv_other_name);
        tv_other_phone = (TextView) findViewById(R.id.tv_other_phone);
        tv_other_phone.setOnClickListener(this);
        tv_respon_list = (TextView) findViewById(R.id.tv_respon_list);
        tv_other_search = (TextView) findViewById(R.id.tv_other_search);
        tv_other_search.setOnClickListener(this);
    }

    public void initData() {
        user_id = MyApplication.UID;
        adapter = new AdatperReporting(ActivityOtherself.this, lists);
        lv_otherself.setAdapter(adapter);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = (c.get(Calendar.MONTH) + 1);
        mlist_stutus = new ArrayList<>();
        mlist_stutus.add("所有状态");
        mlist_stutus.add("已通过");
        mlist_stutus.add("待审核");
        mlist_stutus.add("被驳回");

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");

        tv_other_name.setText("责任人：" + name);
        tv_other_phone.setText("电话号码：" + phone);

        tv_otherself_status.setText(mlist_stutus.get(0));
        tv_otherself_time.setText(year + "-" + month);
    }

    public void initEvent() {
        lv_otherself.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ActivityOtherself.this, ActivityOtherDetail.class);
                intent.putExtra("id", lists.get(i).id);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });

        req();

    }

    public void req() {
        Calendar c = Calendar.getInstance();
        String t_time = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1);
        String[] str = t_time.split("-");
        if (Integer.valueOf(str[1]) < 10) {
            str[1] = "0" + str[1];
        }
        showProgressDialog();
        time = str[0] + "-" + str[1];
        packZRPersonInfoUp.mobile = phone;
        packZRPersonInfoUp.status = "";
        packZRPersonInfoUp.pub_time = time;
        //下载
        PcsDataBrocastReceiver.registerReceiver(ActivityOtherself.this, mReceivers);
        PcsDataDownload.addDownload(packZRPersonInfoUp);
    }

    private List<YjMyReport> lists = new ArrayList<>();

    private PcsDataBrocastReceiver mReceivers = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }

            if (nameStr.equals(packZRPersonInfoUp.getName())) {
                //等待框
                dismissProgressDialog();
                PackYjZRPersonInfoDown packDown = (PackYjZRPersonInfoDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    PcsDataDownload.addDownload(packZRPersonInfoUp);
                    return;
                }
                //PcsDataBrocastReceiver.unregisterReceiver(getActivity(), mReceivers);
                if (packDown.list_2.size() > 0) {
                    lists.clear();
                    lists.addAll(packDown.list_2);
                    adapter.notifyDataSetChanged();
                    tv_respon_list.setVisibility(View.GONE);
                } else {
                    lists.clear();
                    tv_respon_list.setVisibility(View.VISIBLE);
                    tv_respon_list.setText(time + ",您" + tv_otherself_status.getText().toString() + "的灾情报告为 0 条");
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_otherself_status:
                createTimePopupWindow(tv_otherself_status, mlist_stutus, "1")
                        .showAsDropDown(tv_otherself_status);
                break;
            case R.id.tv_otherself_time:
                createTimePopupWindow(tv_otherself_time, getTimes(year, month), "2")
                        .showAsDropDown(tv_otherself_time);
                break;
            case R.id.tv_other_phone:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.tv_other_search:
                showProgressDialog();
                packZRPersonInfoUp.mobile = phone;
                packZRPersonInfoUp.status = status;
                packZRPersonInfoUp.pub_time = time;
                PcsDataDownload.addDownload(packZRPersonInfoUp);
                break;
        }
    }

    private int screenHight = 0;
    private String time = "", status = "";

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createTimePopupWindow(final TextView dropDownView, final List<String> dataeaum, final String
            flag) {
        AdapterData dataAdapter = new AdapterData(ActivityOtherself.this, dataeaum);
        View popcontent = LayoutInflater.from(ActivityOtherself.this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(ActivityOtherself.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((int) (dropDownView.getWidth() * 1.5));
        // 调整下拉框长度
        screenHight = Util.getScreenHeight(ActivityOtherself.this);
        if (dataeaum.size() < 9) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.6));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showTimeStr = dataeaum.get(position);
                dropDownView.setText(showTimeStr);
                if (flag == "1") {
                    if (position == 0) {
                        status = "";
                    } else if (position == 2) {
                        status = (position - 2) + "";
                    } else if (position == 1) {
                        status = position + "";
                    } else {
                        status = (position - 1) + "";
                    }

                } else {
                    time = dataeaum.get(position);
                }
            }
        });
        return pop;
    }

    public ArrayList<String> getTimes(int year, int month) {
        ArrayList<String> str = new ArrayList<>();
        if (month == 12) {
            str.add(year + "-" + month);
            str.add(year + "-" + (month - 1));
            str.add(year + "-" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month == 11) {
            str.add(year + "-" + month);
            str.add(year + "-" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month == 10) {
            str.add(year + "-" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month >= 6) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month == 5) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add((year - 1) + "-12");
        } else if (month == 4) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
        } else if (month == 3) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
            str.add((year - 1) + "-10");
        } else if (month == 2) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
            str.add((year - 1) + "-10");
            str.add((year - 1) + "-09");
        } else if (month == 1) {
            str.add(year + "-0" + month);
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
            str.add((year - 1) + "-10");
            str.add((year - 1) + "-09");
            str.add((year - 1) + "-08");
        }
        return str;
    }
}
