package com.pcs.ztqtj.view.activity.warn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjColumnGradeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjColumnGradeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjColumnGradeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqColumnGrade;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdapterWarningCenterColmn;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhoneListAndHelp;
import com.pcs.ztqtj.view.activity.set.ActivityPushMain;
import com.pcs.ztqtj.view.fragment.warning.FragmentDisasterReporting;
import com.pcs.ztqtj.view.fragment.warning.FragmentWeatherRiskWarn;
import com.pcs.ztqtj.view.fragment.warning.FragmentWeatherWarningNotFj;
import com.pcs.ztqtj.view.fragment.warning.WarnFragmentUpdateImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 预警中心
 */
public class ActivityWarningCenterNotFjCity extends FragmentActivityZtqWithPhoneListAndHelp implements View.OnClickListener {
    private GridView gridView = null;
    private AdapterWarningCenterColmn adapter = null;
    private TextView tvShare = null;
    private TextView tvPushSettings = null;
    private LinearLayout llBottomButton = null;
    private RelativeLayout layout;
    private TextView tvNoData;
    private List<YjZqColumnGrade> list_column = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private int currentFragmentPosition = -1;
    private View view_lines;
    private MyReceiver receiver = new MyReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitleText(R.string.warning_center);
        super.setContentView(R.layout.activity_warning_center_notfjcity);
        initView();
        initEvent();
        initData();
    }
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview_warning);
        tvShare = (TextView) findViewById(R.id.tv_share);
        tvPushSettings = (TextView) findViewById(R.id.tv_push_settings);
        llBottomButton = (LinearLayout) findViewById(R.id.ll_bottom_button);
        btnHelp.setVisibility(View.GONE);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        layout = (RelativeLayout) findViewById(R.id.layout);
        view_lines=findViewById(R.id.view_lines);
    }
    private void initEvent() {
        tvShare.setOnClickListener(this);
        tvPushSettings.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                if(position == 0) {
//                    showBottomButton(true);
//                } else {
//                    showBottomButton(false);
//                }
                showFragment(position);
                adapter.setLine(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private PackYjColumnGradeUp columnGradeUp=new PackYjColumnGradeUp();
    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        if(fragmentList == null || fragmentList.size() == 0) {
            fragmentList = new ArrayList<>();
            fragmentList.add(initWeatherWarning());
            fragmentList.add(initWeatherRisk());
            fragmentList.add(initWarnBillList());
        }

        if(ZtqCityDB.getInstance().getCityMain().isFjCity) {
            showProgressDialog();
            columnGradeUp.column_type="20";
            PcsDataDownload.addDownload(columnGradeUp);
        }
        adapter = new AdapterWarningCenterColmn(this, list_column);
        gridView.setAdapter(adapter);
        gridView.setItemChecked(0, true);

        showFragment(0);

        // 当前城市
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity.isFjCity){
            gridView.setVisibility(View.VISIBLE);
            view_lines.setVisibility(View.VISIBLE);
        }else{
            gridView.setVisibility(View.GONE);
            view_lines.setVisibility(View.GONE);
        }
    }

    /**
     * 切换Fragment
     *
     * @param toPosition 点击的position
     */
    public Fragment replaceFragment(int toPosition) {
        if (currentFragmentPosition == toPosition) {
            if (fragmentList.size() > toPosition) {
                return fragmentList.get(toPosition);
            } else {
                return null;
            }
        }
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragmentList.get(toPosition).isAdded()) {
            fragmentTransaction.add(R.id.fragment, fragmentList.get(toPosition));
        }
        if(currentFragmentPosition == -1) {
            fragmentTransaction.show(fragmentList.get(toPosition)).commit();
        } else {
            fragmentTransaction.hide(fragmentList.get(currentFragmentPosition)).show(fragmentList.get(toPosition)).commit();
        }
        currentFragmentPosition = toPosition;
        return fragmentList.get(toPosition);
    }

    public void showFragment(int position) {
        if (position==1){
            initWeatherRisk();
        }else if (position==2){
            initWarnBillList();
        }
        replaceFragment(position);
        if(fragmentList != null && fragmentList.size() > position) {
            Fragment fragment = fragmentList.get(position);
            if(fragment instanceof WarnFragmentUpdateImpl) {
                WarnFragmentUpdateImpl impl = (WarnFragmentUpdateImpl) fragment;
                impl.update(position);
            }
        }
    }

    private void showBottomButton(boolean b) {
        if(b) {
            llBottomButton.setVisibility(View.VISIBLE);
        } else {
            llBottomButton.setVisibility(View.GONE);
        }
    }

    // 初始化气象预警
    private Fragment initWeatherWarning() {
        FragmentWeatherWarningNotFj fragmentWeatherWarning = new FragmentWeatherWarningNotFj();
        // 判断是否是从首页滚动预警进入本页面的
        WarnBean bean = (WarnBean) getIntent().getSerializableExtra("warninfo");
        if (bean != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("warninfo", bean);
            fragmentWeatherWarning.setArguments(bundle);
        }
        return fragmentWeatherWarning;
    }
    private List<YjColumnGradeDown> list_column_dowm = new ArrayList<YjColumnGradeDown>();

    //风险预警
    private Fragment initWeatherRisk() {
        list_column_dowm.clear();
        if(list_column != null && list_column.size() > 1) {
            list_column_dowm.addAll(list_column.get(1).list);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list_column_dowm);
        FragmentWeatherRiskWarn fragment = new FragmentWeatherRiskWarn();
        fragment.setArguments(bundle);
        return fragment;
    }


    // 灾情直报
    private Fragment initWarnBillList() {
        Fragment fragment=new FragmentDisasterReporting();
        list_column_dowm.clear();
        if(list_column != null && list_column.size() > 2) {
            list_column_dowm.addAll(list_column.get(2).list);
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list_column_dowm);
        fragment.setArguments(bundle);
        return fragment;
    }
    public View getShareButton() {
        return tvShare;
    }

    public void showNoData(boolean show) {
        if(show) {
            layout.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                break;
            case R.id.tv_push_settings:
                Intent intent = new Intent(ActivityWarningCenterNotFjCity.this, ActivityPushMain.class);
                intent.putExtra("city", ZtqCityDB.getInstance().getCityMain());
                intent.putExtra("title", "推送设置");
                startActivity(intent);
                break;
        }
    }
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(columnGradeUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackYjColumnGradeDown down = (PackYjColumnGradeDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                list_column.clear();
                list_column.addAll(down.list_2);
                gridView.setNumColumns(list_column.size());
                adapter.notifyDataSetChanged();
            }

        }
    }
}
