package com.pcs.ztqtj.view.fragment;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

public class BaseFragment extends Fragment {

    /**
     * java.lang.IllegalStateException: No activity 错误解决方案
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
