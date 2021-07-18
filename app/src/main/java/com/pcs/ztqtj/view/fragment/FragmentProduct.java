package com.pcs.ztqtj.view.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterProductGridView;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.ztqtj.view.activity.product.ActivityOceanMap;
import com.pcs.ztqtj.view.activity.product.ActivitySatelliteCloudChart;
import com.pcs.ztqtj.view.activity.product.ActivityWeatherRadar;
import com.pcs.ztqtj.view.activity.product.ActivityWeatherSummary;
import com.pcs.ztqtj.view.activity.product.lightning.ActivityLightningMonitor;
import com.pcs.ztqtj.view.activity.product.numericalforecast.ActivityDetailCenterPro;
import com.pcs.ztqtj.view.activity.product.numericalforecast.ActivityNumericalForecast;
import com.pcs.ztqtj.view.activity.product.situation.ActivitySituation;
import com.pcs.ztqtj.view.activity.product.typhoon.ActivityTyphoon;
import com.pcs.ztqtj.view.activity.set.ActivityProgramerManager;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报
 */
public class FragmentProduct extends Fragment {

    private GridView productView;
    private GridView product_analysis;
    private List<Map<String, Object>> dataList, dataList_analysis;
    private String[] product_list;
    private String[] product_list_analysis;
    private int[] product_icon_list,product_icon_list_gray;
    private int[] product_icon_analysis,product_icon_analysis_gray;
    private final List<Intent> intents = new ArrayList<>();
    private final List<Intent> intents_analysis = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    /**
     * 初始化UI
     **/
    private void initView(View view) {
        productView = view.findViewById(R.id.product_gridview);
        product_analysis = view.findViewById(R.id.product_gridview_2);
        ImageButton product_top_right_button = view.findViewById(R.id.product_top_right_button);
        product_top_right_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ActivityProgramerManager.class));
            }
        });
    }

    /**
     * 初始化数据
     **/
    private void initData() {
        okHttpSstq();

        product_list = getResources().getStringArray(R.array.product_list);
        product_list_analysis = getResources().getStringArray(R.array.product_analysis);
        TypedArray arZH = getResources().obtainTypedArray(R.array.product_icon_list);
        product_icon_list = new int[arZH.length()];
        for (int i = 0; i < arZH.length(); i++) {
            product_icon_list[i] = arZH.getResourceId(i, 0);
        }
        arZH.recycle();
        TypedArray arZHGray = getResources().obtainTypedArray(R.array.product_icon_list_gray);
        product_icon_list_gray = new int[arZHGray.length()];
        for (int i = 0; i < arZHGray.length(); i++) {
            product_icon_list_gray[i] = arZHGray.getResourceId(i, 0);
        }
        arZHGray.recycle();

        TypedArray arFX = getResources().obtainTypedArray(R.array.product_icon_list_analysis);
        product_icon_analysis = new int[arFX.length()];
        for (int j = 0; j < arFX.length(); j++) {
            product_icon_analysis[j] = arFX.getResourceId(j, 0);
        }
        arFX.recycle();
        TypedArray arFXGray = getResources().obtainTypedArray(R.array.product_icon_list_analysis_gray);
        product_icon_analysis_gray = new int[arFXGray.length()];
        for (int j = 0; j < arFXGray.length(); j++) {
            product_icon_analysis_gray[j] = arFXGray.getResourceId(j, 0);
        }
        arFXGray.recycle();

        dataList = new ArrayList<>();
        dataList_analysis = new ArrayList<>();
        // 气象雷达、卫星云图、台风路径、天气综述、数值预报、整点实况、指点天气（叠加各项可定位置的预报功能）、交通气象、海洋天气、气象影视、福建汛情
        intents.add(new Intent(getActivity(), ActivityWeatherRadar.class));//雷达回波
        intents.add(new Intent(getActivity(), ActivitySatelliteCloudChart.class));//卫星云图
        intents.add(new Intent(getActivity(), ActivityTyphoon.class));//台风
        intents.add(new Intent(getActivity(), ActivityLiveQuery.class));//实况查询
        intents.add(new Intent(getActivity(), ActivityLiveQueryDetail.class));//整点天气
//        intents.add(new Intent(getActivity(), AcivityObservation.class));//下垫面
        intents.add(new Intent(getActivity(), ActivityLightningMonitor.class));// 闪电定位
//        intents.add(new Intent(getActivity(), ActivityFloodSummaryGridView.class));
//        intents.add(new Intent(getActivity(), ActivityWaterFlood.class));//水利汛情
        intents.add(new Intent(getActivity(), ActivityAirQualityQuery.class));//空气质量
        intents.add(new Intent(getActivity(), ActivitySituation.class));//天气形势
        intents.add(new Intent(getActivity(), ActivityWebView.class));//城市积水
//        intents.add(new Intent(getActivity(),ActivityDataQuery.class));//资料查询

        intents_analysis.add(new Intent(getActivity(), ActivityWeatherSummary.class));//气象报告
        intents_analysis.add(new Intent(getActivity(), ActivityDetailCenterPro.class));//指导预报
        intents_analysis.add(new Intent(getActivity(), ActivityNumericalForecast.class));//模式预报
//        intents_analysis.add(new Intent(getActivity(), ActivityAgricultureWeatherColumn.class)); // 农业气象
//        intents_analysis.add(new Intent(getActivity(), ActivityTrafficWeather.class));//交通气象
//        intents_analysis.add(new Intent(getActivity(), ActivityTraffic.class));//交通气象
//        intents_analysis.add(new Intent(getActivity(), ActivityImWeather.class));//海洋气象
        intents_analysis.add(new Intent(getActivity(), ActivityOceanMap.class));//海洋气象
//        intents_analysis.add(new Intent(getActivity(), ActivityLocationWarning.class));//定点服务

        getSelectItem();
        AdapterProductGridView adapter = new AdapterProductGridView(getActivity(), dataList);
        productView.setAdapter(adapter);
        productView.setOnItemClickListener(myOnItemClickListener);
        AdapterProductGridView adapter_analysis = new AdapterProductGridView(getActivity(), dataList_analysis);
        product_analysis.setAdapter(adapter_analysis);
        product_analysis.setOnItemClickListener(myOnItemClickListener2);

        ShareTools.getInstance(getActivity()).reqShare();
    }

    /**
     * 获取选择的项
     **/
    private void getSelectItem() {
        dataList.clear();
        for (int i = 0; i < product_list.length; i++) {
            String[] item = product_list[i].split(",");
            Map<String, Object> map = new HashMap<>();
            map.put("id", i);
            if (MyApplication.LIMITINFO.contains(item[1])) {
                map.put("rid", product_icon_list[i]);
            } else {
                map.put("rid", product_icon_list_gray[i]);
            }
            map.put("title", item[0]);
            dataList.add(map);
        }
        dataList_analysis.clear();
        for (int j = 0; j < product_list_analysis.length; j++) {
            String[] item = product_list_analysis[j].split(",");
            Map<String, Object> map = new HashMap<>();
            map.put("id", j);
            if (MyApplication.LIMITINFO.contains(item[1])) {
                map.put("rid", product_icon_analysis[j]);
            } else {
                map.put("rid", product_icon_analysis_gray[j]);
            }
            map.put("title", item[0]);
            dataList_analysis.add(map);
        }
    }

    private final OnItemClickListener myOnItemClickListener2 = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map<String, Object> map = dataList_analysis.get(i);
            int id = (Integer) map.get("id");
            String title = (String) map.get("title");
            if (title.equals("指导预报")) {
                Intent intent = new Intent(getActivity(), ActivityDetailCenterPro.class);
                intent.putExtra("t", title);
                intent.putExtra("c", "106");
                startActivity(intent);
            } else {
                startActivity(intents_analysis.get(id));
            }
        }
    };

    private final OnItemClickListener myOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
            Map<String, Object> map = dataList.get(position);
            String title = (String) map.get("title");
            if (title.equals("整点天气")) {
                Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
                intent.putExtra("stationName", stationName);
                intent.putExtra("item", "temp");
                startActivity(intent);
            } else if (title.equals("实况查询")) {
                PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
                Intent intent = new Intent(getActivity(), ActivityLiveQuery.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("city", cityMain);
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (title.equals("空气质量")) {
                PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
                if (packCity == null || packCity.ID == null) {
                    return;
                }
                Intent intent;
                if (packCity.isFjCity) {
                    intent = new Intent(getActivity(), ActivityAirQualitySH.class);
                } else {
                    ActivityAirQualityQuery.setCity(packCity.ID, packCity.CITY);
                    intent = new Intent(getActivity(), ActivityAirQualityQuery.class);
                }
                intent.putExtra("id", packCity.ID);
                intent.putExtra("name", packCity.NAME);
                startActivity(intent);
            } else if (title.equals("城市积水")) {
                Intent intent = new Intent(getActivity(), ActivityWebView.class);
                intent.putExtra("title", title);
                intent.putExtra("url", "https://tianjin.welife100.com/Monitor/monitor");
                intent.putExtra("shareContent", title);
                startActivity(intent);
            } else {
                int id = (Integer) map.get("id");
                startActivity(intents.get(id));
            }
        }
    };

//    DialogTwoButton dialogLogin = null;
//    private void showLoginDialog() {
//        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_message, null);
//        view.setText("该功能仅限内部人员使用，请先登录！");
//        dialogLogin = new DialogTwoButton(getContext(), view, "返回", "登录", new DialogFactory.DialogListener() {
//            @Override
//            public void click(String str) {
//                dialogLogin.dismiss();
//                if (str.equals("返回")) {
//
//                } else if (str.equals("登录")) {
//                    Intent intent = new Intent(getContext(), ActivityPhotoLogin.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        dialogLogin.show();
//    }
//
//    private DialogOneButton dialogPermission = null;
//    private void showNoPermission() {
//        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_message, null);
//        view.setText("暂无此权限！");
//        dialogPermission = new DialogOneButton(getContext(), view, "确定", new DialogFactory.DialogListener() {
//            @Override
//            public void click(String str) {
//                dialogPermission.dismiss();
//            }
//        });
//        dialogPermission.show();
//    }
//
//    private void checkPermission(String channelId, Class<?> cls) {
//        if(ZtqCityDB.getInstance().isLoginService()) {
//            if(MyApplication.LIMITINFO.contains(channelId)) {
//                startActivity(new Intent(getContext(), cls));
//            } else {
//                showNoPermission();
//            }
//        } else {
//            showLoginDialog();
//        }
//    }

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

    private String stationName = "";
    /**
     * 获取实况信息
     */
    private void okHttpSstq() {
        final PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", packCity.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("sstq", json);
                    final String url = CONST.BASE_URL+"sstq";
                    Log.e("sstq", url);
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
                            if (!isAdded()) {
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("sstq", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq")) {
                                                    JSONObject sstqobj = bobj.getJSONObject("sstq");
                                                    if (!TextUtil.isEmpty(sstqobj.toString())) {
                                                        PackSstqDown packSstq = new PackSstqDown();
                                                        packSstq.fillData(sstqobj.toString());
                                                        if (!TextUtils.isEmpty(packSstq.stationname)) {
                                                            stationName = packSstq.stationname;
                                                        } else {
                                                            stationName = packCity.NAME;
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
