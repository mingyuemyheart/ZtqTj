package com.pcs.ztqtj.view.fragment.warning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdapterWeaWarnExplenList;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.set.ActivityPushMain;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterPublicNaturalDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterPublicNaturalUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 突发公共事件预警页面中的自然灾害fragment
 * Created by tyaathome on 2016/6/14.
 */
public class FragmentWarningCommonZRZH extends Fragment implements WarnFragmentUpdateImpl {

    private ExpandableListView expandableListView = null;
    private TextView tvNull = null;
    // 列表内容部分
    private RelativeLayout rlListContent = null;
    // 预警细节内容部分
    private LinearLayout llDetailContent = null;
    private TextView tvShare, tvPush;

    private String currentBaseTitle = "";

    private List<String> parentData = new ArrayList<>();
    private List<List<WarnCenterYJXXGridBean>> chilData = new ArrayList<>();

    private PackWarningCenterPublicNaturalUp packUp = new PackWarningCenterPublicNaturalUp();
    private PackWarningCenterPublicNaturalDown packDown = new PackWarningCenterPublicNaturalDown();

    private AdapterWeaWarnExplenList adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_warning_common_zrzh, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        tvShare = (TextView) getView().findViewById(R.id.tv_share);
        tvPush = (TextView) getView().findViewById(R.id.tv_push_settings);
        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandlisview);
        tvNull = (TextView) getView().findViewById(R.id.textnull);
        rlListContent = (RelativeLayout) getView().findViewById(R.id.list_content);
        llDetailContent = (LinearLayout) getView().findViewById(R.id.ll_detail_content);
        tvShare.setVisibility(View.GONE);
        rlListContent.setVisibility(View.VISIBLE);
        llDetailContent.setVisibility(View.GONE);
    }

    private void initEvent() {
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                refresh();
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long
                    id) {
                currentBaseTitle = parentData.get(groupPosition);
                //存储用来判断已读跟未读的id
                SharedPreferencesUtil.putData(chilData.get(groupPosition).get(childPosition).id,chilData.get(groupPosition).get(childPosition).id);
                //等待框
                ((ActivityWarningCenterNotFjCity) getActivity()).showProgressDialog();
                //请求
                PackWarnPubDetailUp packDetailUp = new PackWarnPubDetailUp();
                packDetailUp.id = chilData.get(groupPosition).get(childPosition).id;
                packDetailUp.type = getType();
                //广播接收
                PcsDataBrocastReceiver.registerReceiver(getActivity(), mReceiver);
                //下载
                PcsDataDownload.addDownload(packDetailUp);
                return false;
            }
        });
        getView().findViewById(R.id.tv_push_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityPushMain.class);
                intent.putExtra("city", ZtqCityDB.getInstance().getCityMain());
                intent.putExtra("title", "推送设置");
                startActivity(intent);
            }
        });

        tvShare.setOnClickListener(onClickListener);
        tvPush.setOnClickListener(onClickListener);
    }

    private void initData() {
        adapter = new AdapterWeaWarnExplenList(getActivity(), parentData, chilData);
        expandableListView.setAdapter(adapter);

        // 数据请求
        reqNet();
    }

    protected String getType() {
        return "1";
    }

    /**
     * 数据请求
     */
    private void reqNet() {
        PcsDataBrocastReceiver.registerReceiver(getContext(), mReceiver);
        PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city != null) {
            packUp = new PackWarningCenterPublicNaturalUp();
            packUp.areaid = city.ID;
            packUp.type = getType();
            packDown = (PackWarningCenterPublicNaturalDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
            if(packDown != null) {
                dealWithData(packDown);
            }
            PcsDataDownload.addDownload(packUp);
        }
    }

    private void dealWithData(PackWarningCenterPublicNaturalDown down) {
        if(down == null) {
            return ;
        }

        parentData.clear();
        chilData.clear();

        if (!TextUtils.isEmpty(down.provincesName)) {
            parentData.add(down.provincesName);
            chilData.add(down.province);
        }
        if (!TextUtils.isEmpty(down.cityname)) {
            parentData.add(down.cityname);
            chilData.add(down.city);
        }
        if (!TextUtils.isEmpty(down.countyname)) {
            parentData.add(down.countyname);
            chilData.add(down.county);
        }
        adapter.notifyDataSetChanged();
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
        if (parentData.size() > 0) {
            expandableListView.setVisibility(View.VISIBLE);
            tvNull.setVisibility(View.GONE);
        } else {
            expandableListView.setVisibility(View.GONE);
            tvNull.setVisibility(View.VISIBLE);
        }
    }

    private void refresh() {
        if(rlListContent != null && llDetailContent != null) {
            rlListContent.setVisibility(View.VISIBLE);
            llDetailContent.setVisibility(View.GONE);
            // 数据请求
            reqNet();
        }
    }

    @Override
    public void update(int position) {
        refresh();
    }

    /**
     * 获取详情失败
     */
    private void showDetilError() {
        Toast.makeText(getActivity(), R.string.get_detail_error, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_share:

                    break;
                case R.id.tv_push_settings:
                    Intent intent = new Intent(getActivity(), ActivityPushMain.class);
                    intent.putExtra("city", ZtqCityDB.getInstance().getCityMain());
                    intent.putExtra("title", "推送设置");
                    startActivity(intent);
                    break;
            }
        }
    };

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if(nameStr.equals(packUp.getName())) {
                packDown = (PackWarningCenterPublicNaturalDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(packDown == null) {
                    return ;
                }
                dealWithData(packDown);
                PcsDataBrocastReceiver.unregisterReceiver(getContext(), mReceiver);
            } else if (nameStr.startsWith(PackWarnPubDetailUp.NAME)) {
                //等待框
                ((ActivityWarningCenterNotFjCity) getActivity()).dismissProgressDialog();
                //预警详情
                PcsDataBrocastReceiver.unregisterReceiver(getActivity(), mReceiver);
                if (!TextUtils.isEmpty(errorStr)) {
                    //获取详情失败
                    showDetilError();
                    return;
                }
                PackWarnPubDetailDown packDown = (PackWarnPubDetailDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    //获取详情失败
                    showDetilError();
                    return;
                }

                //数据
                final WarnBean bean = new WarnBean();
                bean.level = packDown.desc;
                bean.ico = packDown.ico;
                bean.msg = packDown.content;
                bean.pt = packDown.pt;
                bean.defend = packDown.defend;
                bean.put_str = packDown.put_str;

                Bundle bundle = new Bundle();
                bundle.putSerializable("warninfo", bean);
                bundle.putString("zrzh_title", currentBaseTitle);
                bundle.putString("type", getType());
                FragmentWarningCenterDetail fragment = new FragmentWarningCenterDetail();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.ll_detail_content, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                tvShare.setVisibility(View.VISIBLE);
                tvShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getView().findViewById(R.id.ll_detail_content);
                        PackShareAboutDown shareDown= (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
                        String shareAdd="";
                        if (shareDown!=null) {
                            shareAdd=shareDown.share_content;
                        }
                        String shareContent =  bean.msg + "(" + bean.put_str + ")" + shareAdd;
                        Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(view);
                        bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
                        //ShareUtil.share(getActivity(), shareContent, bitmap);
                        ShareTools.getInstance(getActivity()).setShareContent(bean.level,shareContent, bitmap, "0").showWindow(view);
                    }
                });
                rlListContent.setVisibility(View.GONE);
                llDetailContent.setVisibility(View.VISIBLE);
            }
        }
    };
}
