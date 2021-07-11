package com.pcs.ztqtj.view.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterLifeFragment;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.SpaceItemDecoration;
import com.pcs.ztqtj.view.activity.life.ActivityChannelList;
import com.pcs.ztqtj.view.activity.life.ActivityMeteorologicalScience;
import com.pcs.ztqtj.view.activity.life.expert_interpretation.ActivityExpertList;
import com.pcs.ztqtj.view.activity.life.health.ActivityHealthWeather;
import com.pcs.ztqtj.view.activity.life.travel.ActivityTravelView;
import com.pcs.ztqtj.view.activity.product.media.ActivityMediaList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生活气象
 */
public class FragmentLife extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initGradView();
    }

    private void initView() {
        ImageView rl = getView().findViewById(R.id.rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ActivityHealthWeather.class));
            }
        });
    }

    /* 初始化条目 */
    private void initGradView() {
        RecyclerView recycleview = getView().findViewById(R.id.recycleview);
        String[] life_list = getResources().getStringArray(R.array.life_list);
        TypedArray ar = getResources().obtainTypedArray(R.array.life_icon_list);
        TypedArray arGray = getResources().obtainTypedArray(R.array.life_icon_list_gray);
        int[] life_icon_list = new int[ar.length()];
        int[] life_icon_list_gray = new int[arGray.length()];
        for (int i = 0; i < ar.length(); i++) {
            life_icon_list[i] = ar.getResourceId(i, 0);
            life_icon_list_gray[i] = arGray.getResourceId(i, 0);
        }
        ar.recycle();

        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < life_list.length; i++) {
            String[] item = life_list[i].split(",");
            Map<String, Object> map = new HashMap<>();
            map.put("t", item[0]);
            if (MyApplication.LIMITINFO.contains(item[1])) {
                map.put("i", life_icon_list[i]);
            } else {
                map.put("i", life_icon_list_gray[i]);
            }
            data.add(map);
        }
        recycleview.addItemDecoration(new SpaceItemDecoration(CommUtils.Dip2Px(getActivity(), 25), CommUtils.Dip2Px(getActivity(), 25), 3));
        recycleview.setAdapter(new AdapterLifeFragment(getActivity(), data));
        recycleview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    /**
     * java.lang.IllegalStateException: No activity
     * 错误解决方案
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
