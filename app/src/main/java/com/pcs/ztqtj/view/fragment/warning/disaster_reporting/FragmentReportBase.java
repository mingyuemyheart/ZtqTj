package com.pcs.ztqtj.view.fragment.warning.disaster_reporting;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * 灾情报告详情父类
 * Created by tyaathome on 2017/8/17.
 */

public class FragmentReportBase extends Fragment {
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden) {
            getFragmentManager().beginTransaction().hide(FragmentDisaterMyreportDetail.getInstance()).commit();
        }
        super.onHiddenChanged(hidden);
    }

    /**
     * 提交报告详情fragment
     * @param id
     */
    protected void commitFragment(String id) {
        Fragment fragment = FragmentDisaterMyreportDetail.getInstance();
        FragmentDisaterMyreportDetail.getInstance().setCurrentFragment(this);
        FragmentDisaterMyreportDetail.getInstance().updateFragment(id);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.hide(this);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    protected  void refreshDate(){
        if (!FragmentDisaterMyreportDetail.getInstance().isHidden()){
            Fragment fragment = FragmentDisasterMyreport.getInstance();
            FragmentDisasterMyreport.getInstance().setCurrentFragment(this);
            FragmentDisasterMyreport.getInstance().updateFragment("");
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.show(fragment);
            transaction.hide(this);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();
        }
    }
}
