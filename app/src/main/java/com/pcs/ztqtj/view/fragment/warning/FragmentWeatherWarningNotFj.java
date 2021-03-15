package com.pcs.ztqtj.view.fragment.warning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterPopWarn;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdapterWeaWarnList;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.set.ActivityPushMain;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 气象预警fragment
 * Created by tyaathome on 2016/6/13.
 */
public class FragmentWeatherWarningNotFj extends Fragment implements WarnFragmentUpdateImpl {

    private RadioGroup radioGroup = null;
    private ListView listView = null;
    private LinearLayout llWarningDetailContent = null;
    private TextView tvShare, tvPush;
    /**
     * 市级对话框
     */
    private DialogOneButton mDialogStation = null;
    /**
     * 县级对话框
     */
    private DialogOneButton mmDialogStation = null;

    private List<String> titleData = new ArrayList<>();
    private List<List<WarnCenterYJXXGridBean>> warnList = new ArrayList<>();
    private List<WarnCenterYJXXGridBean> currentWarnList = new ArrayList<>();

    // 上传下载包
    private PackWarnWeatherUp packUp = new PackWarnWeatherUp();
    private PackWarnWeatherDown packDown = new PackWarnWeatherDown();

    // 广播对象
    private MyReceiver receiver = new MyReceiver();

    private AdapterWeaWarnList adapter = null;

    private List<PackLocalCity> contentList=new ArrayList<>();

    // 市级城市ID
    private String parentId;

    // 从首页传入的首页单条预警信息
    private WarnBean mainSingleWarnBean = null;


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
        tvPush = (TextView) getView().findViewById(R.id.tv_push_settings);
        radioGroup = (RadioGroup) getView().findViewById(R.id.radiogroup);
        listView = (ListView) getView().findViewById(R.id.warnlistview);
        llWarningDetailContent = (LinearLayout) getView().findViewById(R.id.fragment);
    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferencesUtil.putData(currentWarnList.get(position).id,currentWarnList.get(position).id);
                reqDetail(currentWarnList.get(position));
            }
        });
        tvPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityPushMain.class);
                intent.putExtra("city", ZtqCityDB.getInstance().getCityMain());
                intent.putExtra("title", "推送设置");
                startActivity(intent);
            }
        });
    }

    private void initData() {
        //PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        adapter = new AdapterWeaWarnList(getActivity(), currentWarnList);
        listView.setAdapter(adapter);
        String cityid = "";
        Bundle bundle = getArguments();
        if(bundle != null) {
            mainSingleWarnBean = (WarnBean) bundle.getSerializable("warninfo");
            cityid = bundle.getString("cityid");
        } else {
            PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
            cityid = cityMain.ID;
//            if (cityMain != null&&cityMain.isFjCity) {
//                cityid = cityMain.PARENT_ID;
//            }else{
//                cityid = cityMain.ID;
//            }
        }
        if(!TextUtils.isEmpty(cityid)) {
            reqNet(cityid);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (receiver != null) {
//            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
//        }
    }

    /**
     * 查询详情
     *
     * @param bean
     */
    private void reqDetail(WarnCenterYJXXGridBean bean) {
        PcsDataBrocastReceiver.registerReceiver(getContext(), receiver);
        //等待框
        ((ActivityWarningCenterNotFjCity) getActivity()).showProgressDialog();
        //请求
        PackWarnPubDetailUp packDetailUp = new PackWarnPubDetailUp();
        packDetailUp.id = bean.id;
        //下载
        PcsDataDownload.addDownload(packDetailUp);
    }

    /**
     * 初始化预警详情fragment
     *
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
                    PackShareAboutDown shareDown= (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
                    String shareAdd="";
                    if (shareDown!=null) {
                        shareAdd=shareDown.share_content;
                    }
                    String shareContent =  bean.msg + "(" + bean.put_str + ")" + shareAdd;
                    View content = getView().findViewById(R.id.fragment);
                    Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(content);
                    bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
                    //ShareUtil.share(getActivity(), shareContent, bitmap);
                    ShareTools.getInstance(getActivity()).setShareContent(bean.level,shareContent, bitmap, "0").showWindow(content);
                }
            });
        }
    }

    /**
     * 网络请求数据
     *
     * @param cityid
     */
    private void reqNet(String cityid) {
        PcsDataBrocastReceiver.registerReceiver(getContext(), receiver);
        packUp = new PackWarnWeatherUp();
        packUp.areaid = cityid;
//        // 请求网络
//        packDown = (PackWarnWeatherDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
//        if (packDown != null) {
//            dealWidthData(packDown);
//        }else{
//
//        }
        PcsDataDownload.addDownload(packUp);
    }

    /**
     * 下载完成后处理数据
     *
     * @param pack
     */
    private void dealWidthData(PackWarnWeatherDown pack) {
        ActivityWarningCenterNotFjCity activity = (ActivityWarningCenterNotFjCity) getActivity();
//        if(pack.city.size() == 0) {
//            activity.showNoData(true);
//        } else {
//            activity.showNoData(false);
//        }
        if (pack.county.size() == 0 && pack.province.size() == 0
                && pack.city.size() == 0) {
            return;
        }
        titleData.clear();
        warnList.clear();
        // 取出对应城市的预警列表
        if (pack.province.size() != 0) {
            titleData.add(pack.provincesName);
            warnList.add(pack.province);
        }
        if (pack.city.size() != 0) {
            titleData.add(pack.cityname);
            warnList.add(pack.city);
        }
        if (pack.county.size() != 0) {
            titleData.add(pack.countyname);
            warnList.add(pack.county);
        }
        addRadioButton();
    }

    /**
     * 添加标题的单选按钮
     *
     */
    private void addRadioButton() {
        radioGroup.removeAllViews();
        int width = Util.getScreenWidth(getActivity());
        int _10dp = Util.dip2px(getActivity(), 10);
        int _35dp = Util.dip2px(getActivity(), 35);
        int radioWidth = (width - _10dp*(titleData.size()-1))
                / titleData.size();
        int pad = Util.dip2px(getActivity(), 10);
        // 这里修改过，省市县的显示顺序有变化
        for (int i = titleData.size(); i > 0; i--) {
            RadioButton radioButton = new RadioButton(getActivity());
            //radioButton.setId(1000 + i);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextColor(getResources()
                    .getColor(R.color.text_black));
//            radioButton
//                    .setBackgroundResource(R.drawable.selector_blue_line);
            radioButton
                    .setBackgroundResource(R.drawable.btn_disaster_reporting);
            radioButton.setPadding(0, 0, 0, 0);
            radioButton.setButtonDrawable(getResources().getDrawable(
                    android.R.color.transparent));
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
//            if(i != 1) {
                lp.rightMargin = _10dp;
//            }
            radioGroup.addView(radioButton, lp);
        }

        if (titleData.size() > 0) {
            // 默认选中的栏目序号
            int index = 0;
            if (mainSingleWarnBean != null) {
                String put_str = mainSingleWarnBean.put_str;
                if (put_str.contains("市")) {
                    index = titleData.size() - 1 - 1;
                } else if (put_str.contains("省")) {
                    index = titleData.size() - 1;
                }
            }
            if (index > titleData.size() - 1) {
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

    public void notifyadapter(int item) {
        if(item < 0) {
            return;
        }
        currentWarnList.clear();
        for (int i = 0; i < warnList.get(item).size(); i++) {
            currentWarnList.add(warnList.get(item).get(i));
        }
        if (mainSingleWarnBean != null) {
            initDetailFragment(mainSingleWarnBean);
            mainSingleWarnBean = null;
        } else {
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
     *
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
            parentId = contentList.get(position).ID;
            reqNet(contentList.get(position).ID);
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

    /**
     * 市级对话框按钮点击事件
     */
    private final DialogFactory.DialogListener mmDialogListener = new DialogFactory.DialogListener() {
        @Override
        public void click(String str) {
            // 对话框取消按钮
            mmDialogStation.dismiss();
        }
    };


    /**
     * 获取详情失败
     */
    private void showDetilError() {
        Toast.makeText(getActivity(), R.string.get_detail_error, Toast.LENGTH_SHORT).show();
    }

    private void refresh() {
        if(radioGroup != null && radioGroup.getChildCount() > 0) {
            radioButtonClick(radioGroup.getChildAt(0).getId());
        }
    }

    @Override
    public void update(int position) {
        refresh();
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (TextUtils.isEmpty(name)) {
                return;
            }
            if (packUp != null && name.equals(packUp.getName())) {
                if(getContext() != null && receiver != null) {
                    PcsDataBrocastReceiver.unregisterReceiver(getContext(), receiver);
                }
                packDown = (PackWarnWeatherDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
                if (packDown == null) {
                    return;
                }
                dealWidthData(packDown);
            } else if (name.startsWith(PackWarnPubDetailUp.NAME)) {
                if(getContext() != null && receiver != null) {
                    PcsDataBrocastReceiver.unregisterReceiver(getContext(), receiver);
                }
                //等待框
                ((ActivityWarningCenterNotFjCity) getActivity()).dismissProgressDialog();
                //预警详情
                if (!TextUtils.isEmpty(errorStr)) {
                    //获取详情失败
                    showDetilError();
                    return;
                }
                PackWarnPubDetailDown packDown = (PackWarnPubDetailDown) PcsDataManager.getInstance().getNetPack(name);
                if (packDown == null) {
                    //获取详情失败
                    showDetilError();
                    return;
                }
                //数据
                WarnBean bean = new WarnBean();
                bean.level = packDown.desc;
                bean.ico = packDown.ico;
                bean.msg = packDown.content;
                bean.pt = packDown.pt;
                bean.defend = packDown.defend;
                bean.put_str = packDown.put_str;
                initDetailFragment(bean);
            }
        }
    }
}