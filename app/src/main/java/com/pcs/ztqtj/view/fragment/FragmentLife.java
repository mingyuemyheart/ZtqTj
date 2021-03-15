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
 * 气象生活
 *
 * @author chenjh
 */
public class FragmentLife extends Fragment {

    private GridView gridView;

    private String[] life_list;
    private int[] life_icon_list;
    private ImageView rl;
    private RecyclerView recycleview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initGradView();
    }

    private void initView() {
        rl = (ImageView) getView().findViewById(R.id.rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 健康气象
                Intent intent = new Intent();
                intent.setClass(getActivity(), ActivityHealthWeather.class);
                getActivity().startActivity(intent);
            }
        });
    }

    /* 初始化条目 */
    private void initGradView() {
        recycleview = (RecyclerView) getView().findViewById(R.id.recycleview);
        gridView = (GridView) getView().findViewById(R.id.product_gridview);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    // <!-- 气象生活 栏目顺序 0、旅游气象；1、气象影视;2、天气新闻；3、专家解读 ;4、灾害防御;5、气象科普.
                    // -->
                    case 3:
                        //专家解读
                        intent.setClass(getActivity(), ActivityExpertList.class);
                        startActivity(intent);
                        break;
                    case 0:
                        // 旅游气象
                        intent.setClass(getActivity(), ActivityTravelView.class);
                        getActivity().startActivity(intent);

                        break;
                    case 1:
                        // 气象影视
//					intent.setClass(getActivity(), ActivityFamilyCity.class);
                        intent.setClass(getActivity(), ActivityMediaList.class);
                        startActivity(intent);
                        break;
                    case 2:

                        intent.setClass(getActivity(), ActivityChannelList.class);
                        intent.putExtra("title", "天气新闻");
                        intent.putExtra("channel_id", "100005");
                        getActivity().startActivity(intent);
                        break;

                    case 4:
//					灾害防御
                        intent.setClass(getActivity(), ActivityChannelList.class);
                        intent.putExtra("title", "灾害防御");
                        intent.putExtra("channel_id", "100007");
                        getActivity().startActivity(intent);
                        break;
                    case 5:
                        // 气象科普
                        intent.setClass(getActivity(), ActivityMeteorologicalScience.class);
                        getActivity().startActivity(intent);

                        break;
                }
            }
        });

        life_list = getResources().getStringArray(R.array.life_list);
        TypedArray ar = getResources().obtainTypedArray(R.array.life_icon_list);
        int len = ar.length();
        life_icon_list = new int[len];
        for (int i = 0; i < len; i++)
            life_icon_list[i] = ar.getResourceId(i, 0);

        ar.recycle();

        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < life_list.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("t", life_list[i]);
            map.put("i", life_icon_list[i]);
            data.add(map);
        }
        recycleview.addItemDecoration(new SpaceItemDecoration(CommUtils.Dip2Px(getActivity(), 25), CommUtils.Dip2Px
                (getActivity(), 25), 3));
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
