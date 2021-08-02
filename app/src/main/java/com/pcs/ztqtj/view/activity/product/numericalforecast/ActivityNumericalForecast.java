package com.pcs.ztqtj.view.activity.product.numericalforecast;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackAppaisalDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackAppaisalUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.product_numerical.AdapterColumn;
import com.pcs.ztqtj.control.adapter.product_numerical.AdapterNumericalForecast;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

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
 * 监测预报-模式预报
 */
public class ActivityNumericalForecast extends FragmentActivitySZYBBase {

    private GridView gridView;
    private AdapterColumn gridAdapter;
    private ExpandableListView expandablelistview;
    private AdapterNumericalForecast adapter;
    private List<List<ForList>> listLeve2 = new ArrayList<>();
    private List<ForList> listLeve1 = new ArrayList<>();
    private List<ForList> forList1 = new ArrayList<>();//一级目录
    private List<ForList> forList2 = new ArrayList<>();//二级目录
    private List<ForList> forList22 = new ArrayList<>();//二级目录
    private PackNumericalForecastColumnDown packNumericalForecastColumnDown;
    private MyReceiver receiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numerical_forecast);
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        setTitleText("模式预报");
        initView();
        okHttpModelList();
        initEvent();
    }

    private Toast toast;

    private void initEvent() {
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                appaisalDown = (PackAppaisalDown) PcsDataManager.getInstance().getNetPack(PackAppaisalUp.NAME);
                cliciPosition = position;
//                if (appaisalDown == null || !appaisalDown.result.equals("1")) {
//                    showAppraisalDailog();
//                } else {
                    gridViewClick(position);
//                }
            }
        });
        expandablelistview.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(ActivityNumericalForecast.this, ActivityDetailNumericalForecast.class);
                intent.putExtra("type", "1");
                intent.putExtra("t", listLeve1.get(groupPosition).name);
                intent.putExtra("subt", listLeve2.get(groupPosition).get(childPosition).name);
                intent.putExtra("req", listLeve2.get(groupPosition).get(childPosition).id);
                if (listLeve1.get(groupPosition).name.trim().equals("福建省气象台指导预报") || listLeve1.get(groupPosition)
                        .name.trim().equals("中央气象台指导预报")) {
                    intent.putExtra("show", true);
                } else {
                    intent.putExtra("show", false);
                }
                if (listLeve2.get(groupPosition).get(childPosition).name.trim().equals("国内天气公报")) {
                    if (toast == null) {
                        toast = Toast.makeText(ActivityNumericalForecast.this, "无数据", Toast.LENGTH_SHORT);
                    } else {
                        toast.setText("无数据");
                    }
                    toast.show();
                } else {
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private int cliciPosition = -1;

    private void gridViewClick(int position) {
        Intent it = new Intent();
        it.setClass(ActivityNumericalForecast.this, ActivityDetailNumericalForecast.class);
        it.putExtra("t", listLeve1.get(position).name);
        it.putExtra("c", listLeve1.get(position).id);
        startActivity(it);
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        expandablelistview = (ExpandableListView) findViewById(R.id.expandablelistview);
        expandablelistview.setVisibility(View.GONE);
        adapter = new AdapterNumericalForecast(ActivityNumericalForecast.this, listLeve1, listLeve2);
        expandablelistview.setAdapter(adapter);
    }

    private DialogTwoButton dialog;
    private TextView tv_pwd;

    private void showAppraisalDailog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_appraisel, null);
        tv_pwd = (TextView) view.findViewById(R.id.tv_pwd);
        dialog = new DialogTwoButton(ActivityNumericalForecast.this, view, "取消", "提交", new DialogFactory
                .DialogListener() {
            @Override
            public void click(String str) {
                if (str.equals("提交")) {
                    if (!isOpenNet()) {
                        showToast("网络连接失败，请检查网络连接");
                    } else {
                        String strPwd = tv_pwd.getText().toString().trim();
                        if (TextUtils.isEmpty(strPwd)) {
                            showToast("请输入口令");
                        } else {
                            showProgressDialog();
                            commidPwd();
                        }
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.setTitle("口令输入框");
        dialog.show();
    }

    private PackAppaisalUp appaisalUp;
    private PackAppaisalDown appaisalDown;

    private void commidPwd() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        String str = tv_pwd.getText().toString().trim();
        appaisalUp = new PackAppaisalUp();
        appaisalUp.pwd = PcsMD5.Md5(str);
        PcsDataDownload.addDownload(appaisalUp);
    }

    /**
     * 解析数据
     **/
    private void analysis() {
        if (packNumericalForecastColumnDown != null) {
            for (int i = 0; i < packNumericalForecastColumnDown.forlist.size(); i++) {
                if (packNumericalForecastColumnDown.forlist.get(i).parent_id.equals("")) {
                    forList1.add(packNumericalForecastColumnDown.forlist.get(i));
                } else {
                    forList2.add(packNumericalForecastColumnDown.forlist.get(i));
                }
            }
            listLeve1 = forList1;
            for (int i = 0; i < listLeve1.size(); i++) {
                for (int j = 0; j < forList2.size(); j++) {
                    if (forList2.get(j).parent_id.equals(listLeve1.get(i).id)) {
                        forList22.add(forList2.get(j));
                    }
                }
                listLeve2.add(forList22);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (appaisalUp != null && nameStr.equals(appaisalUp.getName())) {
                dismissProgressDialog();
                if (!TextUtils.isEmpty(errorStr)) {
                    showToast("提交失败，请稍后再试");
                } else {
                    appaisalDown = (PackAppaisalDown) PcsDataManager.getInstance().getNetPack(PackAppaisalUp.NAME);
                    if (appaisalDown == null) {
                        showToast("提交失败，请稍后再试");
                    } else if (appaisalDown.result.equals("1")) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        showToast("提交成功");
                        gridViewClick(cliciPosition);
                    } else {
                        showToast("口令错误");
                    }
                }
            }
        }
    }

    /**
     * 获取模式预报数据
     */
    private void okHttpModelList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"modelprediction_list";
                    Log.e("modelprediction_list", url);
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
                            Log.e("modelprediction_list", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("forecast_column_n2")) {
                                                JSONObject forecast_column_n2 = bobj.getJSONObject("forecast_column_n2");
                                                if (!TextUtils.isEmpty(forecast_column_n2.toString())) {
                                                    dismissProgressDialog();
                                                    listLeve1.clear();
                                                    listLeve2.clear();
                                                    packNumericalForecastColumnDown = new PackNumericalForecastColumnDown();
                                                    packNumericalForecastColumnDown.fillData(forecast_column_n2.toString());
                                                    analysis();
                                                    adapter.setData(listLeve1, listLeve2);
                                                    gridAdapter = new AdapterColumn(ActivityNumericalForecast.this, listLeve1, getImageFetcher());
                                                    gridView.setAdapter(gridAdapter);
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

}
