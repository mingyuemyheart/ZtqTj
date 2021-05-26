package com.pcs.ztqtj.view.fragment.citylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.AroundCityBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackAroundCityDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterHotCity;
import com.pcs.ztqtj.control.adapter.data_query.SpaceItemDecoration;
import com.pcs.ztqtj.control.inter.ClickImpl;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.citylist.CityChoicableImpl;

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
 * 添加城市-天津周边
 */
public class FragmentSHHotCity extends Fragment {

    private RecyclerView recyclerView;
    private AdapterHotCity adapterHotCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sh_hot_city_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.grid);
    }

    private void initData() {
        okHttpAroundArea();
    }

    private List<Boolean> getSelectCityList(List<AroundCityBean> cityList) {
        List<PackLocalCity> listCityInfo = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments == null) {
            // 定位城市
            PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
            // 已选城市
            PackLocalCityInfo packChoiced = ZtqCityDB.getInstance().getCurrentCityInfo();
            if (ZtqLocationTool.getInstance().getIsAutoLocation()) {
                // 自动定位，添加定位城市
                listCityInfo.add(packLocation);
                // 去除重复城市
                ZtqCityDB.getInstance().delCityFromList(packChoiced.localCityList, packLocation);
                ZtqCityDB.getInstance().setCurrentCityInfo(packChoiced);
            }

//    列表去重
            for (int i = 0; i < packChoiced.localCityList.size(); i++) {
                boolean addToList = true;
                for (int j = 0; j < listCityInfo.size(); j++) {
                    if (listCityInfo.get(j).ID.equals(packChoiced.localCityList.get(i).ID)) {
                        addToList = false;
                        break;
                    }
                }
                if (addToList) {
                    listCityInfo.add(packChoiced.localCityList.get(i));
                }
            }
        } else {
            List<PackLocalCity> passCityList = (List<PackLocalCity>) arguments.getSerializable("citylist");
            if (passCityList != null) {
                listCityInfo.addAll(passCityList);
            }
        }
        List<Boolean> idList = new ArrayList<>();
        labelA:
        for (AroundCityBean aroundCity : cityList) {
            for (PackLocalCity selectCity : listCityInfo) {
                if (aroundCity.id.equals(selectCity.ID)) {
                    idList.add(true);
                    continue labelA;
                }
            }
            idList.add(false);
        }
        return idList;
    }

    private ClickImpl<AroundCityBean> recyclerViewClickListener = new ClickImpl<AroundCityBean>() {
        @Override
        public void onClick(AroundCityBean value) {
            // 城市选择后的事件处理
            CityChoicableImpl activity = (CityChoicableImpl) getActivity();
            if (activity != null && value != null) {
                PackLocalCity city = ZtqCityDB.getInstance().getAllCityByID(value.id);
                if (city != null) {
                    activity.choiceCity(city, city);
                }
            }
        }
    };

    /**
     * 获取周边站号
     */
    private void okHttpAroundArea() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"around_area";
                    Log.e("around_area", url);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("around_area", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("around_area")) {
                                                    JSONObject fbObj = bobj.getJSONObject("around_area");
                                                    if (!TextUtil.isEmpty(fbObj.toString())) {
                                                        PackAroundCityDown down = new PackAroundCityDown();
                                                        down.fillData(fbObj.toString());
                                                        if (down != null && down.cityList != null && down.cityList.size() != 0) {
                                                            List<Boolean> selectCityList = getSelectCityList(down.cityList);
                                                            adapterHotCity = new AdapterHotCity(getActivity(),down.cityList, selectCityList, recyclerViewClickListener);
                                                            recyclerView.setAdapter(adapterHotCity);
                                                            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                                            int lr = CommUtils.Dip2Px(getContext(), 30);
                                                            int tb = CommUtils.Dip2Px(getContext(), 20);
                                                            recyclerView.addItemDecoration(new SpaceItemDecoration(lr, tb, 3));
                                                        }
                                                    }
                                                }
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
