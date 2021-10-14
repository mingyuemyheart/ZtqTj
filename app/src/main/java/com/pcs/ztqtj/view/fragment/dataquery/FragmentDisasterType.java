package com.pcs.ztqtj.view.fragment.dataquery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.AreaConfig;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryAreaConfigDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryAreaConfigUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.ItemExpert;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertListUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.view.activity.product.dataquery.ActivityDataQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/11/20.
 */

public class FragmentDisasterType extends Fragment {

    private PackDataQueryAreaConfigUp areaUp = new PackDataQueryAreaConfigUp();
    private PackExpertListUp listUp = new PackExpertListUp();
    private PackExpertDetailUp detailUp = new PackExpertDetailUp();
    private MyReceiver receiver = new MyReceiver();
    private String mType = "", mCategory = "";
    private List<AreaConfig> areaList = new ArrayList<>();
    private List<ItemExpert> titleList = new ArrayList<>();
    private TextView tvType, tvTitle;
    private ActivityDataQuery mActivity;
    private int currentTypePosition = 0;
    private int currentTitlePosition = 0;
    private ImageView image;
    private TextView tvContent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActivityDataQuery) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_query_disaster_type, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        tvType = (TextView) getView().findViewById(R.id.tv_type);
        tvTitle = (TextView) getView().findViewById(R.id.tv_title);
        image = (ImageView) getView().findViewById(R.id.image);
        tvContent = (TextView) getView().findViewById(R.id.tv_content);
    }

    private void initEvent() {
        tvType.setOnClickListener(onClickListener);
        tvTitle.setOnClickListener(onClickListener);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategory = bundle.getString("category", "");
            mType = bundle.getString("column", "");
        }
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        requestArea();
    }

    private void updateArea(int position) {
        currentTypePosition = position;
        if (areaList.size() > currentTypePosition) {
            tvType.setText(areaList.get(currentTypePosition).name);
            requestInfoTitle();
        }
    }

    private void updateTitle(int position) {
        currentTitlePosition = position;
        if(titleList.size() > currentTitlePosition) {
            tvTitle.setText(titleList.get(currentTitlePosition).title);
            requestInfoDetail();
        }
    }

    /**
     * 请求地区数据
     */
    private void requestArea() {
        mActivity.showProgressDialog();
        areaUp = new PackDataQueryAreaConfigUp();
        areaUp.type = mType;
        areaUp.d_type = mCategory;
        PcsDataDownload.addDownload(areaUp);
    }

    /**
     * 请求文章标题
     */
    private void requestInfoTitle() {
        mActivity.showProgressDialog();
        if(areaList.size() > currentTypePosition) {
            listUp = new PackExpertListUp();
            listUp.channel_id = areaList.get(currentTypePosition).area_id;
            PcsDataDownload.addDownload(listUp);
        }
    }

    private void requestInfoDetail() {
        mActivity.showProgressDialog();
        if(titleList.size() > currentTitlePosition) {
            detailUp = new PackExpertDetailUp();
            detailUp.id = titleList.get(currentTitlePosition).id;
            PcsDataDownload.addDownload(detailUp);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_type:
                    if (areaList.size() == 0) {
                        return;
                    }
                    List<String> cityList = new ArrayList<>();
                    for (AreaConfig config : areaList) {
                        cityList.add(config.name);
                    }
                    mActivity.createPopupWindow((TextView) v, cityList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            updateArea(position);
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.tv_title:
                    if (titleList.size() == 0) {
                        return;
                    }
                    List<String> tList = new ArrayList<>();
                    for (ItemExpert item : titleList) {
                        tList.add(item.title);
                    }
                    mActivity.createPopupWindow((TextView) v, tList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            updateTitle(position);
                        }
                    }).showAsDropDown(v);
                    break;
            }
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (areaUp.getName().equals(nameStr)) {
                mActivity.dismissProgressDialog();
                PackDataQueryAreaConfigDown down = (PackDataQueryAreaConfigDown) PcsDataManager.getInstance()
                        .getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                areaList.clear();
                areaList.addAll(down.p_list);
                updateArea(0);
            } else if (listUp.getName().equals(nameStr)) {
                mActivity.dismissProgressDialog();
                PackExpertListDown down = (PackExpertListDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                titleList.clear();
                titleList.addAll(down.dataList);
                updateTitle(0);
            } else if (detailUp.getName().equals(nameStr)) {
                mActivity.dismissProgressDialog();
                PackExpertDetailDown down = (PackExpertDetailDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                if(!TextUtils.isEmpty(down.big_img)) {
                    image.setVisibility(View.VISIBLE);
                    String path = getString(R.string.file_download_url) + down.big_img;
                    Picasso.get().load(path).into(image);
                } else {
                    image.setVisibility(View.GONE);
                }
                tvContent.setText(down.desc);
            }
        }
    }
}
