package com.pcs.ztqtj.view.fragment.warning;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterPopWarn;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdapterWeaWarnList;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;

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
 * 预警中心-气象预警-气象预警
 */
public class FragmentWeatherWarningNotFj extends Fragment implements WarnFragmentUpdateImpl {

    private RadioGroup radioGroup = null;
    private ListView listView = null;
    private LinearLayout llWarningDetailContent = null;
    private TextView tvShare;
    /**
     * 市级对话框
     */
    private DialogOneButton mDialogStation = null;

    private List<String> titleData = new ArrayList<>();
    private List<List<WarnCenterYJXXGridBean>> warnList = new ArrayList<>();
    private List<WarnCenterYJXXGridBean> currentWarnList = new ArrayList<>();

    // 上传下载包
    private MyPackWarnWeatherDown packDown = new MyPackWarnWeatherDown();

    private AdapterWeaWarnList adapter = null;

    private List<PackLocalCity> contentList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weather_warning_notfj, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            refresh();
        }
    }

    private void initView() {
        tvShare = (TextView) getView().findViewById(R.id.tv_share);
        radioGroup = (RadioGroup) getView().findViewById(R.id.radiogroup);
        listView = (ListView) getView().findViewById(R.id.warnlistview);
        llWarningDetailContent = (LinearLayout) getView().findViewById(R.id.fragment);
    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferencesUtil.putData(currentWarnList.get(position).id,currentWarnList.get(position).id);
                okHttpWarningDetail(currentWarnList.get(position).id);
            }
        });
    }

    private void initData() {
        adapter = new AdapterWeaWarnList(getActivity(), currentWarnList);
        listView.setAdapter(adapter);
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if (city == null) return;
        okHttpWarningList(city.ID);
    }

    /**
     * 初始化预警详情fragment
     * @param bean
     */
    private void initDetailFragment(final WarnBean bean) {
        if (bean != null) {
            // 显示detail，隐藏列表
            llWarningDetailContent.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            FragmentWarningCenterDetail fragment = new FragmentWarningCenterDetail();
            Bundle bundle = new Bundle();
            bundle.putSerializable("warninfo", bean);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, fragment);
            fragmentTransaction.commit();
            tvShare.setText("预警分享");
            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareContent =  bean.msg + "(" + bean.put_str + ")" + CONST.SHARE_URL;
                    View content = getView().findViewById(R.id.fragment);
                    Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(content);
                    bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
                    //ShareUtil.share(getActivity(), shareContent, bitmap);
                    ShareTools.getInstance(getActivity()).setShareContent(bean.level,shareContent, bitmap, "0").showWindow(content);
                }
            });
        }
    }

    public void notifyadapter(int item) {
        if(item < 0) {
            return;
        }
        currentWarnList.clear();
        for (int i = 0; i < warnList.get(item).size(); i++) {
            currentWarnList.add(warnList.get(item).get(i));
        }
        adapter.notifyDataSetChanged();
        // 隐藏detail，显示列表
        llWarningDetailContent.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        // 显示预警查询
        tvShare.setText(getActivity().getResources().getString(R.string.warning_query));
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogStation();
            }
        });
    }

    private void radioButtonClick(int id) {
        int count = radioGroup.getChildCount();
        int index = 0;
        for (; index < count; index++) {
            View view = radioGroup.getChildAt(index);
            if (view.getId() == id) {
                break;
            }
        }
        notifyadapter(count - index - 1);
    }

    /**
     * 显示市级对话框
     */
    private void showDialogStation() {
        if (packDown == null) {
            return;
        }
        contentList.clear();
        contentList.addAll(packDown.cityidex);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.setfragmetnt_dialog_layout, null);
        AdapterPopWarn adapterPopWarn = new AdapterPopWarn(getActivity(), contentList);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapterPopWarn);
        listView.setOnItemClickListener(mOnItemClickStation);
        mDialogStation = new DialogOneButton(getActivity(), view, "取消", mDialogListener);
        mDialogStation.setTitle("预警查询");
        mDialogStation.show();
        setListViewHeightBasedOnChildren(listView);
    }

    /**
     * 设置ListView一屏显示的条数
     * @param listView
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int showItmeCount = 0;
        if (listView.getCount() < 0) {
            showItmeCount = 0;
        } else {
            if (listView.getCount() >= 9) {
                showItmeCount = 9;
            } else {
                showItmeCount = listView.getCount();
            }
        }
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < showItmeCount; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(0, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    /**
     * 市级列表点击事件
     */
    private final AdapterView.OnItemClickListener mOnItemClickStation = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            okHttpWarningList(contentList.get(position).ID);
            // 关闭对话框
            mDialogStation.hide();
        }
    };


    /**
     * 市级对话框按钮点击事件
     */
    private final DialogFactory.DialogListener mDialogListener = new DialogFactory.DialogListener() {
        @Override
        public void click(String str) {
            // 对话框取消按钮
            mDialogStation.dismiss();
        }
    };

    private void refresh() {
        if(radioGroup != null && radioGroup.getChildCount() > 0) {
            radioButtonClick(radioGroup.getChildAt(0).getId());
        }
    }

    @Override
    public void update(int position) {
        refresh();
    }

    /**
     * 获取预警列表信息
     */
    private void okHttpWarningList(final String stationId) {
        ((ActivityWarningCenterNotFjCity) getActivity()).showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"warningcenterqx_fb";
                    Log.e("warningcenterqx_fb", url);
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
//                                    Log.e("warningcenterqx_fb", result);
                                    ((ActivityWarningCenterNotFjCity) getActivity()).dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("warningcenterqx_fb")) {
                                                    JSONObject fbObj = bobj.getJSONObject("warningcenterqx_fb");
                                                    packDown.fillData(fbObj.toString());
                                                    dealWidthData();
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

    /**
     * 下载完成后处理数据
     */
    private void dealWidthData() {
        if (packDown.county.size() == 0 && packDown.province.size() == 0 && packDown.city.size() == 0) {
            return;
        }
        titleData.clear();
        warnList.clear();
        // 取出对应城市的预警列表
        if (packDown.province.size() != 0) {
            titleData.add(packDown.provincesName);
            warnList.add(packDown.province);
        }
        if (packDown.city.size() != 0) {
            titleData.add(packDown.cityname);
            warnList.add(packDown.city);
        }
        if (packDown.county.size() != 0) {
            titleData.add(packDown.countyname);
            warnList.add(packDown.county);
        }

        Bundle bundle = getArguments();
        boolean isDisWaring = false;
        String warningId = null;
        if(bundle != null) {
            warningId = bundle.getString("warningId");
            isDisWaring = bundle.getBoolean("isDisWaring");
            okHttpWarningDetail(warningId);
        }
        addRadioButton(isDisWaring);
    }

    /**
     * 添加标题的单选按钮
     */
    private void addRadioButton(boolean isDisWaring) {
        radioGroup.removeAllViews();
        int width = Util.getScreenWidth(getActivity());
        int _10dp = Util.dip2px(getActivity(), 10);
        int _35dp = Util.dip2px(getActivity(), 35);
        int radioWidth = (width - _10dp*(titleData.size()-1)) / titleData.size();
        // 这里修改过，省市县的显示顺序有变化
        for (int i = titleData.size(); i > 0; i--) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextColor(getResources().getColor(R.color.text_black));
            radioButton.setBackgroundResource(R.drawable.btn_disaster_reporting);
            radioButton.setPadding(0, 0, 0, 0);
            radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
            radioButton.setText(titleData.get(i - 1));
            radioButton.setSingleLine(true);
            radioButton.setChecked(false);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioButtonClick(v.getId());
                }
            });
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(radioWidth, _35dp, 1.0f);
            lp.rightMargin = _10dp;
            radioGroup.addView(radioButton, lp);
        }

        if (titleData.size() > 0) {
            // 默认选中的栏目序号
            int index = isDisWaring ? 0 : 1;
            RadioButton btn = (RadioButton) radioGroup.getChildAt(index);
            if(btn==null){
                return;
            }
            // 点击按钮
            btn.performClick();
        }
    }

    /**
     * 预警详情
     */
    private void okHttpWarningDetail(final String id) {
        ((ActivityWarningCenterNotFjCity) getActivity()).showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", id);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("yjxx_info_query", json);
                    final String url = CONST.BASE_URL+"yjxx_info_query";
                    Log.e("yjxx_info_query", url);
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
                                    ((ActivityWarningCenterNotFjCity) getActivity()).dismissProgressDialog();
                                    Log.e("yjxx_info_query", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yjxx_info_query")) {
                                                    JSONObject yjxx_info_query = bobj.getJSONObject("yjxx_info_query");
                                                    if (!TextUtil.isEmpty(yjxx_info_query.toString())) {
                                                        ((ActivityWarningCenterNotFjCity) getActivity()).dismissProgressDialog();
                                                        //预警详情
                                                        PackWarnPubDetailDown down = new PackWarnPubDetailDown();
                                                        down.fillData(yjxx_info_query.toString());
                                                        //数据
                                                        WarnBean bean = new WarnBean();
                                                        bean.level = down.desc;
                                                        bean.ico = down.ico;
                                                        bean.msg = down.content;
                                                        bean.pt = down.pt;
                                                        bean.defend = down.defend;
                                                        bean.put_str = down.put_str;
                                                        initDetailFragment(bean);
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
