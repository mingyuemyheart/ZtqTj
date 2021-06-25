package com.pcs.ztqtj.view.activity.newairquality;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirRankNew;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescDown.DicListBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirChoiceCity;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirChoiceType;
import com.pcs.ztqtj.control.adapter.air_qualitydetail.AdapterAirRanking;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
 * 空气质量-昨日排行
 */
public class FragmentAirRandkingY extends Fragment implements OnClickListener {
    private ListView cityList;
    private View tv_ph_down;
    private View tv_ph_up;
    private Button pm_city;
    private Button pm_province;
    private Button pm_rank_name;
    private AdapterAirRanking adapter;
    private TextView whatAQI;
    private ImageButton btn_right;
    private ProgressDialog mProgress;
    private List<DicListBean> dataeaum = new ArrayList<>();

    private PackKeyDescDown packKey = new PackKeyDescDown();

    private List<AirRankNew> airListData = new ArrayList<>();
    private List<AirRankNew> airListDataParent = new ArrayList<>();

    private List<AirRankNew> listCityPop = new ArrayList<>();
    private List<AirRankNew> listProvincePop = new ArrayList<>();
    private String s_area_name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.activity_airquality_ranking_t, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        s_area_name = bundle.getString("name");
        initListView();
        initListTitle();
        initEvent();
        initDate();
    }

    private void initDate() {
        whatAQI.setText(Html.fromHtml("<u>什么是空气质量指数(AQI)?</u>"));
        adapter = new AdapterAirRanking(getActivity(),s_area_name);
        cityList.setAdapter(adapter);
        reqKey();
        reqD("AQI");
    }

    /**
     * 获取关键字列表 aqi pm2.5值等数据
     */
    private void reqKey() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        okHttpAirRemark();
    }

    /**
     * 關鍵字在列表中的位置
     */
    private int keyPosition = 0;

    /**
     * 处理aqi字段
     */
    private void dealWidthKeyData() {
        dataeaum.clear();
        for (int i = 0; i < packKey.dicList.size(); i++) {
            dataeaum.add(packKey.dicList.get(i));
        }
        if (dataeaum.size() > 0) {
            // 如果第一个不是aqi的话，则从新请求数据
            if (dataeaum.get(0).rankType.toLowerCase().equals("aqi")) {
            } else {
                changeValueKey(0);
                reqD(dataeaum.get(0).rankType);
            }
        }
    }

    private void changeValueKey(int position) {
        try {
            keyPosition = position;
            if (packKey.dicList == null || packKey.dicList.size() == 0) {

            } else {
                whatMessage = packKey.dicList.get(position).rankType;
                whatAQI.setText(Html.fromHtml("<u>" + packKey.dicList.get(position).questions + "</u>"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PackAirRankNewUp packDetialup;

    private Toast toast;

    public void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), str,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }
    private void reqD(String reqcode) {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        // 如果是aqi则有背景
        adapter.isAQI = reqcode.trim().equals("aqi") || reqcode.trim().toLowerCase().equals("aqi");
        okHttpAirRankNew(reqcode);
    }

    public void showProgressDialog() {
        showProgressDialog(getActivity().getResources().getString(R.string.please_wait));
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }
    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    };

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    /* 判断是否是有网络*/
    public boolean isOpenNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return true;
        } else {
            return false;
        }
    }

    private void initEvent() {
        whatAQI.setOnClickListener(this);
        tv_ph_down.setOnClickListener(this);
        tv_ph_up.setOnClickListener(this);
        pm_province.setOnClickListener(this);
        pm_city.setOnClickListener(this);
        pm_rank_name.setOnClickListener(this);
        cityList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                intentNextActivity(airListData.get(arg2).areaId, airListData.get(arg2).city);
            }
        });

    }

    private void intentNextActivity(String AreaID, String area_name) {
        ActivityAirQualityQuery.setCity(AreaID, area_name);
        Intent intent=null;
        if (!area_name.equals("天津")){
            intent = new Intent(getActivity(), ActivityAirQualityQuery.class);
        }else{
            intent = new Intent(getActivity(), ActivityAirQualitySH.class);
            intent.putExtra("id",AreaID);
            intent.putExtra("name",area_name);
        }
        startActivity(intent);
    }

    /* 初始化列表标题 */
    private void initListTitle() {
        pm_province = (Button) getView().findViewById(R.id.pm_province_t);
        pm_city = (Button) getView().findViewById(R.id.pm_city_t);
        pm_rank_name = (Button) getView().findViewById(R.id.pm_rank_name_t);
        tv_ph_down = getView().findViewById(R.id.tv_ph_down_t);
        tv_ph_up = getView().findViewById(R.id.tv_ph_up_t);
    }

    /* 初始化城市列表 */
    private void initListView() {
        whatAQI = ((TextView) getView().findViewById(R.id.citiao_t));// 设置文字下划线
        cityList = (ListView) getView().findViewById(R.id.paihang_t);
    }

    private String whatMessage = "AQI";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.isDown = true;
            tv_ph_down.setVisibility(View.VISIBLE);
            tv_ph_up.setVisibility(View.INVISIBLE);
            changeValueKey(msg.what);
            reqD(whatMessage);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pm_province_t:
                showProvincePopup();
                break;
            case R.id.pm_city_t:
                showCityPopup();
                break;
            case R.id.pm_rank_name_t:
                createAQIPopup(pm_rank_name, dataeaum, 0, new DrowListClick() {
                    @Override
                    public void itemClick(int floag, int item) {
                        handler.sendEmptyMessage(item);
                    }
                }).showAsDropDown(pm_rank_name);
                break;
            case R.id.tv_ph_down_t:
            case R.id.tv_ph_up_t:
                showProgressDialog();
                adapter.isDown = !adapter.isDown;
                if (adapter.isDown) {
                    tv_ph_down.setVisibility(View.VISIBLE);
                    tv_ph_up.setVisibility(View.INVISIBLE);
                } else {
                    tv_ph_down.setVisibility(View.INVISIBLE);
                    tv_ph_up.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
//                chagesequ(adapter.isDown);
                dismissProgressDialog();
                break;
            case R.id.btn_right:
                break;
        }
    }

    /**
     * true 是从小到大 false 从大到小
     */
    private void chagesequ(boolean floag) {
        if (floag) {
            //城市排序
            Collections.sort(airListData, new Comparator<AirRankNew>() {
                public int compare(AirRankNew arg0, AirRankNew arg1) {
                    if (TextUtils.isEmpty(arg0.num) || TextUtils.isEmpty(arg1.num)) {
                        return 0;
                    }
                    float a = Float.parseFloat(arg0.num);
                    float b = Float.parseFloat(arg1.num);
                    return Float.compare(a, b);
                }
            });
        } else {
            //城市排序
            Collections.sort(airListData, new Comparator<AirRankNew>() {
                public int compare(AirRankNew arg0, AirRankNew arg1) {
                    if (TextUtils.isEmpty(arg0.num) || TextUtils.isEmpty(arg1.num)) {
                        return 0;
                    }
                    float a = Float.parseFloat(arg0.num);
                    float b = Float.parseFloat(arg1.num);
                    return Float.compare(b, a);
                }
            });
        }

        adapter.setData(airListData);
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化下拉列表
     */
    private void initPopList() {
        //下拉列表
        listCityPop.clear();
        listCityPop.addAll(airListData);
        listProvincePop.clear();
        listProvincePop.addAll(airListDataParent);
        //省份拼音
        for (int i = 0; i < listProvincePop.size(); i++) {
            AirRankNew bean = listProvincePop.get(i);
            PackLocalCity packLocalCity = ZtqCityDB.getInstance().getProvinceByName(bean.province);
            if (packLocalCity != null) {
                bean.pinyin = packLocalCity.PINGYIN;
            }
        }
        //省份排序
        Collections.sort(listProvincePop, new Comparator<AirRankNew>() {
            public int compare(AirRankNew arg0, AirRankNew arg1) {
                if (arg0.province.equals("北京")) {
                    return -1;
                } else if (arg1.province.equals("北京")) {
                    return 1;
                }
                if (arg0.province.equals("天津")) {
                    return -2;
                } else if (arg1.province.equals("天津")) {
                    return 2;
                }
                return arg0.pinyin.compareTo(arg1.pinyin);
            }
        });
        //城市排序
        Collections.sort(listCityPop, new Comparator<AirRankNew>() {
            public int compare(AirRankNew arg0, AirRankNew arg1) {
//                if (arg0.city.equals("北京")) {
//                    return -1;
//                } else if (arg1.city.equals("北京")) {
//                    return 1;
//                }
//                if (arg0.city.equals("天津")) {
//                    return -2;
//                } else if (arg1.city.equals("天津")) {
//                    return 2;
//                }
                return arg0.pinyin.compareTo(arg1.pinyin);
            }
        });
    }

    /**
     * 显示城市下拉框
     */
    private void showCityPopup() {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < listCityPop.size(); i++) {
            listString.add(listCityPop.get(i).city);
        }
        AdapterAirChoiceCity adapter = new AdapterAirChoiceCity(getActivity(), listString);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.mylistviw);
        listView.setAdapter(adapter);

        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(view);
        pop.setOutsideTouchable(false);
        pop.setWidth(getResources().getDimensionPixelOffset(R.dimen.dimen140));
        if (listCityPop.size() < 12) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen300));
        }
        pop.setFocusable(true);

        pop.showAsDropDown(pm_city);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                try {
                    intentNextActivity(airListData.get(position).areaId, listCityPop.get(position).city);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示省份下拉框
     */
    private void showProvincePopup() {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < listProvincePop.size(); i++) {
            listString.add(listProvincePop.get(i).province);
        }
        AdapterAirChoiceCity adapter = new AdapterAirChoiceCity(getActivity(), listString);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.mylistviw);
        listView.setAdapter(adapter);

        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(view);
        pop.setOutsideTouchable(false);
        pop.setWidth(getResources().getDimensionPixelOffset(R.dimen.dimen140));
        if (listProvincePop.size() < 12) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen300));
        }
        pop.setFocusable(true);

        pop.showAsDropDown(pm_province);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    pop.dismiss();
                    Intent intent = new Intent(getActivity(), ActivityAirQualityProvinceRranking.class);
                    ActivityAirQualityProvinceRranking.province = listProvincePop.get(position).province;
                    ActivityAirQualityProvinceRranking.reqCode = packDetialup.rank_type;
                    ActivityAirQualityProvinceRranking.keyPosition = keyPosition;

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 创建AQI等下拉列表
     */
    private PopupWindow createAQIPopup(final Button dropDownView, final List<DicListBean> dataeaum, final int floag, final DrowListClick listener) {
        AdapterAirChoiceType dataAdapter = new AdapterAirChoiceType(getActivity(), dataeaum);
        View popcontent = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(getResources().getDimensionPixelOffset(R.dimen.dimen140));
        if (dataeaum.size() < 12) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen300));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showKey = dataeaum.get(position).rankType;
                dropDownView.setText(showKey);
                listener.itemClick(floag, position);
            }
        });
        return pop;
    }

    private void okHttpAirRankNew(final String airType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("airType", airType);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("air_rank_new", json);
                    final String url = CONST.BASE_URL+"air_rank_new";
                    Log.e("air_rank_new", url);
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
                            Log.e("air_rank_new", result);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("air_rank_new")) {
                                                JSONObject air_rank = bobj.getJSONObject("air_rank_new");
                                                PackAirRankNewDown pack = new PackAirRankNewDown();
                                                pack.fillData(air_rank.toString());
                                                try {
                                                    adapter.isDown = true;
                                                    airListDataParent.clear();
                                                    airListData.clear();
                                                    List<AirRankNew> airListData_exm = new ArrayList<>();
//            airListData.addAll(pack.rank_list);
                                                    for (int i=0;i<pack.rank_list.size();i++){
                                                        AirRankNew airRankNew=new AirRankNew();
                                                        if (pack.rank_list.get(i).city.equals("天津")){
                                                            airRankNew=pack.rank_list.get(i);
                                                            airListData.add(airRankNew);
                                                        }else{
                                                            airListData_exm.add(pack.rank_list.get(i));
                                                        }
                                                    }
                                                    airListData.addAll(airListData_exm);
                                                    Iterator it = pack.allProvince.entrySet().iterator();
                                                    while (it.hasNext()) {
                                                        Map.Entry entry = (Map.Entry) it.next();
                                                        airListDataParent.add((AirRankNew) entry.getValue());
                                                    }

                                                    adapter.setData(airListData);
                                                    adapter.notifyDataSetChanged();
                                                    //初始化下拉列表
                                                    initPopList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

    private void okHttpAirRemark() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"air_remark";
                    Log.e("air_remark", url);
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
                            Log.e("air_remark", result);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("air_remark")) {
                                                JSONObject air_remark = bobj.getJSONObject("air_remark");
                                                packKey.fillData(air_remark.toString());
                                                dealWidthKeyData();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
