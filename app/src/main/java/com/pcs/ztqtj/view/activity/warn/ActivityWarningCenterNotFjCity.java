package com.pcs.ztqtj.view.activity.warn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjColumnGradeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjColumnGradeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqColumnGrade;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdapterWarningCenterColmn;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhoneListAndHelp;
import com.pcs.ztqtj.view.activity.set.ActivityPushMain;
import com.pcs.ztqtj.view.fragment.warning.FragmentDisasterReporting;
import com.pcs.ztqtj.view.fragment.warning.FragmentWeatherRiskWarn;
import com.pcs.ztqtj.view.fragment.warning.FragmentWeatherWarningNotFj;
import com.pcs.ztqtj.view.fragment.warning.WarnFragmentUpdateImpl;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-预警中心
 */
public class ActivityWarningCenterNotFjCity extends FragmentActivityZtqWithPhoneListAndHelp implements View.OnClickListener {

    private GridView gridView = null;
    private AdapterWarningCenterColmn adapter = null;
    private TextView tvShare = null;
    private TextView tvPushSettings = null;
    private RelativeLayout layout;
    private TextView tvNoData;
    private List<YjZqColumnGrade> list_column = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private int currentFragmentPosition = -1;
    private View view_lines;

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
        btnHelp.setVisibility(View.GONE);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        layout = (RelativeLayout) findViewById(R.id.layout);
        view_lines = findViewById(R.id.view_lines);
    }

    private void initEvent() {
        tvShare.setOnClickListener(this);
        tvPushSettings.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showFragment(position);
                adapter.setLine(position);
            }
        });
    }

    private void initData() {
        if (fragmentList == null || fragmentList.size() == 0) {
            fragmentList = new ArrayList<>();
            fragmentList.add(initWeatherWarning());
            fragmentList.add(initWeatherRisk());
            fragmentList.add(initWarnBillList());
        }

        adapter = new AdapterWarningCenterColmn(this, list_column);
        gridView.setAdapter(adapter);
        gridView.setItemChecked(0, true);

        showFragment(0);

        // 当前城市
        if (ZtqCityDB.getInstance().getCityMain().isFjCity) {
            okHttpColumnGrade();
            gridView.setVisibility(View.VISIBLE);
            view_lines.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.GONE);
            view_lines.setVisibility(View.GONE);
        }
    }

    /**
     * 切换Fragment
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
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragmentList.get(toPosition).isAdded()) {
            fragmentTransaction.add(R.id.fragment, fragmentList.get(toPosition));
        }
        if (currentFragmentPosition == -1) {
            fragmentTransaction.show(fragmentList.get(toPosition)).commit();
        } else {
            fragmentTransaction.hide(fragmentList.get(currentFragmentPosition)).show(fragmentList.get(toPosition)).commit();
        }
        currentFragmentPosition = toPosition;
        return fragmentList.get(toPosition);
    }

    public void showFragment(int position) {
        if (position == 1) {
            initWeatherRisk();
        } else if (position == 2) {
            initWarnBillList();
        }
        replaceFragment(position);
        if (fragmentList != null && fragmentList.size() > position) {
            Fragment fragment = fragmentList.get(position);
            if (fragment instanceof WarnFragmentUpdateImpl) {
                WarnFragmentUpdateImpl impl = (WarnFragmentUpdateImpl) fragment;
                impl.update(position);
            }
        }
    }

    // 初始化气象预警
    private Fragment initWeatherWarning() {
        FragmentWeatherWarningNotFj fragmentWeatherWarning = new FragmentWeatherWarningNotFj();
        // 判断是否是从首页滚动预警进入本页面的
        boolean isDisWaring = false;
        String warningId = null;
        Bundle bundle = new Bundle();
        if (getIntent().hasExtra("warningId")) {
            warningId = getIntent().getStringExtra("warningId");
        }
        bundle.putString("warningId", warningId);
        if (getIntent().hasExtra("isDisWaring")) {
            isDisWaring = getIntent().getBooleanExtra("isDisWaring", false);
        }
        bundle.putBoolean("isDisWaring", isDisWaring);
        fragmentWeatherWarning.setArguments(bundle);
        return fragmentWeatherWarning;
    }

    private List<YjColumnGradeDown> list_column_dowm = new ArrayList<>();

    //风险预警
    private Fragment initWeatherRisk() {
        list_column_dowm.clear();
        if (list_column != null && list_column.size() > 1) {
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
        Fragment fragment = new FragmentDisasterReporting();
        list_column_dowm.clear();
        if (list_column != null && list_column.size() > 2) {
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
        if (show) {
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

    /**
     * 获取预警栏目
     */
    private void okHttpColumnGrade() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL + "column_grade";
                    Log.e("column_grade", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("column_grade", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!TextUtil.isEmpty(obj.toString())) {
                                                dismissProgressDialog();
                                                PackYjColumnGradeDown down = new PackYjColumnGradeDown();
                                                down.fillData(obj.toString());
                                                list_column.clear();
                                                list_column.addAll(down.list_2);
                                                gridView.setNumColumns(list_column.size());
                                                adapter.notifyDataSetChanged();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
