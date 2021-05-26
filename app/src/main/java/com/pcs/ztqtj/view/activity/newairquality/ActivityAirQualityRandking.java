package com.pcs.ztqtj.view.activity.newairquality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 空气质量，今日排行，昨日排行
 */
public class ActivityAirQualityRandking extends FragmentActivityZtqBase implements View.OnClickListener{
    private PackKeyDescDown packKey = new PackKeyDescDown();
    private RadioButton rb_today,rb_yestoday;
    private RadioGroup radioGroup;
    private ImageButton btn_right;
    private boolean checkItem=true;
    private static String s_area_name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airquality_ranking);
        Intent intent=getIntent();
        s_area_name=intent.getStringExtra("name");
        setTitleText("空气质量");
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radiogroups);
        rb_today= (RadioButton) findViewById(R.id.btn_air_t);
        rb_yestoday= (RadioButton) findViewById(R.id.btn_air_y);
        btn_right= (ImageButton)findViewById(R.id.btn_right);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setOnClickListener(this);
    }

    public void initEvent() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.btn_air_t:
                        changeFragment(0);
                        break;
                    case R.id.btn_air_y:
                        changeFragment(1);
                        break;
                }
            }
        });
    }
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private FragmentAirRandkingT fragmentAirRandkingT=new FragmentAirRandkingT();
    private FragmentAirRandkingY fragmentAirRandkingY=new FragmentAirRandkingY();


    public void initData() {
        mListFragment.add(fragmentAirRandkingT);
        mListFragment.add(fragmentAirRandkingY);
        changeFragment(0);
    }

    public void changeFragment(int index) {
        if (index==0){
            checkItem=true;
        }else{
            checkItem=false;
        }
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("name",s_area_name);
        if (index==0){
            fragmentAirRandkingT.setArguments(bundle);
            tran.replace(R.id.fra_airfragment, fragmentAirRandkingT);
            tran.commitAllowingStateLoss();
        }else{
            fragmentAirRandkingY.setArguments(bundle);
            tran.replace(R.id.fra_airfragment, fragmentAirRandkingY);
            tran.commitAllowingStateLoss();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_right:
//                if (checkItem){
//                    changeFragment(0);
//                }else{
//                    changeFragment(1);
//                }
                break;
        }
    }


    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.equals(PackKeyDescUp.NAME)) {
                packKey = (PackKeyDescDown) PcsDataManager.getInstance().getNetPack(name);
                if (packKey == null) {
                    return;
                }
            }
        }
    }






}
