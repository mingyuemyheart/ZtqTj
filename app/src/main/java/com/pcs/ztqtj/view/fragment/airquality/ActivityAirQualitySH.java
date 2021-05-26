package com.pcs.ztqtj.view.fragment.airquality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 空气质量-天津
 */
public class ActivityAirQualitySH extends FragmentActivityZtqBase {
    private RadioGroup radioGroup;
    private static String s_area_name;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airquality_sh);
        Intent intent = getIntent();
        s_area_name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");
        setTitleText("空气质量");
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radiogroups);
    }

    public void initEvent() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.btn_air_pre:
                        changeFragment(0);
                        break;
                    case R.id.btn_air_live:
                        changeFragment(1);
                        break;
                }
            }
        });
    }

    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private FragmentAirQualityPre fragmentAirQualityPre = new FragmentAirQualityPre();
    private FragmentAirQualityLive fragmentAirQualityLive = new FragmentAirQualityLive();


    public void initData() {
        mListFragment.add(fragmentAirQualityLive);
        mListFragment.add(fragmentAirQualityPre);
        changeFragment(1);
    }

    public void changeFragment(int index) {
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("name", s_area_name);
        bundle.putString("id", id);
        if (index == 0) {
            fragmentAirQualityLive.setDate(id,s_area_name);
            fragmentAirQualityLive.setArguments(bundle);
            tran.replace(R.id.airfragment, fragmentAirQualityLive);
            tran.commitAllowingStateLoss();
        } else {
            fragmentAirQualityPre.setDate(id,s_area_name);
            fragmentAirQualityPre.setArguments(bundle);
            tran.replace(R.id.airfragment, fragmentAirQualityPre);
            tran.commitAllowingStateLoss();
        }

    }
}
