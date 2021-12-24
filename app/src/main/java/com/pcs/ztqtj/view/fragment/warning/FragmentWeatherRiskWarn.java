package com.pcs.ztqtj.view.fragment.warning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjColumnGradeDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdatperWeaRiskWarn;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.fragment.FragmentWebView;

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
 * 预警中心-气象预警-风险预警
 */
public class FragmentWeatherRiskWarn extends Fragment {

    private ListView listview;
    private RadioGroup radioGroup;
    private AdatperWeaRiskWarn adatper;
    private List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist;
    private TextView textnull;
    private LinearLayout llFragmentContent = null;
    private ArrayList<YjColumnGradeDown> list_column;
    private PackWarningCenterTfggsjDown packdown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weather_risk_warn, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        listview = (ListView) getView().findViewById(R.id.warnlist);
        radioGroup = (RadioGroup) getView().findViewById(R.id.radiogroup);
        textnull = (TextView) getView().findViewById(R.id.textnull);
        llFragmentContent = (LinearLayout) getView().findViewById(R.id.fragment);
    }

    private void initEvent() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String htmlpath;
                htmlpath = packdown.list.get(position).html_path;
                SharedPreferencesUtil.putData(htmlpath,htmlpath);

                // 隐藏列表和暂无数据，显示网页fragment
                listview.setVisibility(View.GONE);
                textnull.setVisibility(View.GONE);
                llFragmentContent.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.file_download_url) + htmlpath);
                FragmentWebView fragmentWebView = new FragmentWebView();
                fragmentWebView.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, fragmentWebView);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    /**
     * 添加标题的单选按钮
     */
    private void addRadioButton() {
        radioGroup.removeAllViews();
        int width = Util.getScreenWidth(getActivity());
        int _10dp = Util.dip2px(getActivity(), 10);
        int _35dp = Util.dip2px(getActivity(), 35);
        int radioWidth = (width /3);
        int pad = Util.dip2px(getActivity(), 5);
        // 这里修改过，省市县的显示顺序有变化
        for (int i = 0; i <list_column.size(); i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(i);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            radioButton.setTextColor(getResources().getColor(R.color.text_black));
            radioButton.setBackgroundResource(R.drawable.btn_disaster_reporting);
            radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
            radioButton.setText(list_column.get(i).name);
            radioButton.setChecked(false);
            radioButton.setPadding(pad, 0, pad, 0);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reqWarn(v.getId());
                }
            });
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(radioWidth, _35dp, 1.0f);
            if (list_column.size() > 3) {
                lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, _35dp, 1.0f);
            }
            lp.rightMargin = _10dp;
            radioGroup.addView(radioButton, lp);
        }

        if (list_column.size() > 0) {
            // 默认选中的栏目序号
            int index = 0;
            if (index > list_column.size() - 1) {
                index = 0;
            }
            if (index==-1){
                index = 0;
            }
            RadioButton btn = (RadioButton) radioGroup.getChildAt(index);
            if(btn==null){
                return;
            }
            // 点击按钮
            btn.performClick();
        }
    }

    private void initData() {
        Bundle bundle = getArguments();
        list_column = bundle.getParcelableArrayList("list");
        addRadioButton();

        datalist = new ArrayList<>();
        adatper = new AdatperWeaRiskWarn(getActivity(), datalist);
        listview.setAdapter(adatper);
        reqWarn(0);
    }

    private void reqWarn(int position) {
        String type = list_column.get(position).type;
        okHttpAcciWarning(type);
    }

    /**
     * 处理数据
     */
    private void dealWithData() {
        if (packdown == null) {
            return;
        }
        datalist.clear();
        for (PackWarningCenterTfggsjDown.WarnTFGGSJ bean : packdown.list) {
            datalist.add(bean);
        }
        llFragmentContent.setVisibility(View.GONE);
        if (datalist.size() == 0) {
            listview.setVisibility(View.GONE);
            textnull.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            textnull.setVisibility(View.GONE);
        }
        adatper.notifyDataSetChanged();
    }

    /**
     * 获取风险预警数据
     * @param warningType FW_DZZH(地质灾害),FW_NLDZ(内涝),FW_SLHX(森林火险)，FW_SH(山洪)，FW_ZXHL(中小河流)，FW_NCZ(脑卒中预警)
     */
    private void okHttpAcciWarning(final String warningType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("extra", warningType);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("pub_warn_acci_health", json);
                    final String url = CONST.BASE_URL + "pub_warn_acci_health";
                    Log.e("pub_warn_acci_health", url);
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
                            Log.e("pub_warn_acci_health", result);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        datalist.clear();
                                        adatper.notifyDataSetChanged();
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("pub_warn_acci_health")) {
                                                JSONObject itemObj = bobj.getJSONObject("pub_warn_acci_health");
                                                if (!TextUtils.isEmpty(itemObj.toString())) {
                                                    packdown = new PackWarningCenterTfggsjDown();
                                                    packdown.fillData(itemObj.toString());
                                                    dealWithData();
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
