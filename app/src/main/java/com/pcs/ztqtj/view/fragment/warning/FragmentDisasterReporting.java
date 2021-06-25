package com.pcs.ztqtj.view.fragment.warning;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjColumnGradeDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.fragment.warning.disaster_reporting.FragmentDisasterInfo;
import com.pcs.ztqtj.view.fragment.warning.disaster_reporting.FragmentDisasterMyreport;
import com.pcs.ztqtj.view.fragment.warning.disaster_reporting.FragmentDisasterUp;
import com.pcs.ztqtj.view.fragment.warning.disaster_reporting.FragmentDisaterMyreportDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-预警中心-灾害直报
 */
public class FragmentDisasterReporting extends Fragment {

    private RadioGroup radioGroup = null;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private FragmentDisasterUp fragmentDisasterUp = new FragmentDisasterUp();
    private FragmentDisasterMyreport fragmentDisasterMyreport = new FragmentDisasterMyreport();
    private FragmentDisasterInfo fragmentDisasterInfo = new FragmentDisasterInfo();
    private ArrayList<YjColumnGradeDown> list_column;
    private RadioButton rb01,rb02,rb03;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_disaster_reporting, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        radioGroup = (RadioGroup) getView().findViewById(R.id.radiogroups);
        rb01= (RadioButton) getView().findViewById(R.id.btn_disaster_up);
        rb02= (RadioButton) getView().findViewById(R.id.btn_disaster_report);
        rb03= (RadioButton) getView().findViewById(R.id.btn_disaster_info);
        initChildFragment();
    }

    public void initEvent() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.btn_disaster_up:
                        changeFragment(0);
                        break;
                    case R.id.btn_disaster_report:
                        changeFragment(1);
                        Log.e(null, "changeFragment: ---"+1);
                        //updateFragment(2,"");
                        break;
                    case R.id.btn_disaster_info:
                        changeFragment(2);
                        break;
                }
            }
        });

        rb02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentDisaterMyreportDetail.getInstance().changeFragment();
                changeFragment(1);
            }
        });

        rb03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentDisaterMyreportDetail.getInstance().changeFragment();
                changeFragment(2);
            }
        });
}

    public void initData() {
        Bundle bundle = getArguments();
        list_column = bundle.getParcelableArrayList("list");
        if (list_column.size()!=0){
            rb01.setText(list_column.get(0).name);
            rb02.setText(list_column.get(1).name);
            rb03.setText(list_column.get(2).name);
        }
        mListFragment.add(fragmentDisasterUp);
        mListFragment.add(fragmentDisasterMyreport);
        mListFragment.add(fragmentDisasterInfo);
        changeFragment(0);
    }

    private void initChildFragment() {
        Fragment fragment = FragmentDisaterMyreportDetail.getInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if(!fragment.isAdded()) {
            transaction.add(R.id.fra_disaster, FragmentDisaterMyreportDetail.getInstance());
        }
        transaction.hide(FragmentDisaterMyreportDetail.getInstance());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    private Fragment fragment, fragment1, fragment2, fragment3;

    public void changeFragment(int index) {
        fragment = mListFragment.get(index);
        fragment1 = mListFragment.get(0);
        fragment2 = mListFragment.get(1);
        fragment3 = mListFragment.get(2);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            if (fragment2.isAdded()) {
                transaction.hide(fragment2);
            } else {
                transaction.add(R.id.fra_disaster, fragment2);
            }
            if (fragment3.isAdded()) {
                transaction.hide(fragment3);
            } else {
                transaction.add(R.id.fra_disaster, fragment3);
            }
            if (fragment == fragment2) {
                transaction.hide(fragment3);
                transaction.hide(fragment1);
            } else if (fragment == fragment3) {
                transaction.hide(fragment1);
                transaction.hide(fragment2);
            }else if (fragment==fragment1){
                transaction.hide(fragment3);
                transaction.hide(fragment2);
            }
            transaction.add(R.id.fra_disaster, fragment).commit();
        } else {
            transaction.hide(fragment3);
            transaction.hide(fragment1);
            transaction.hide(fragment2);
            transaction.show(fragment).commit();
        }
    }

    public void updateFragment(int position, String str) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (position) {
            case 1:
                FragmentDisasterMyreport fragment = (FragmentDisasterMyreport) mListFragment.get(position);
                transaction.hide(fragment3).hide(fragment1).commit();
                fragment.updateFragment(str);
                break;
            case 2:
                FragmentDisasterMyreport fragments = (FragmentDisasterMyreport) mListFragment.get(1);
                //transaction.hide(fragment3).hide(fragment1).commit();
                fragments.updateFragment("");
                //fragments.SetHide();
                break;
            case 3:
                FragmentDisasterUp fragmentss = (FragmentDisasterUp) mListFragment.get(0);
                //transaction.hide(fragment3).hide(fragment1).commit();
                fragmentss.setvisibity(str);
                break;
            case 4:
                FragmentDisasterInfo fragmentsss = (FragmentDisasterInfo) mListFragment.get(2);
                fragmentsss.RefreshDate();
                break;
        }
    }


    public void clickButton(@IdRes int id) {
        radioGroup.check(id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(Fragment frag : getChildFragmentManager().getFragments()) {
            frag.onActivityResult(requestCode, resultCode, data);
        }
    }
}
