package com.pcs.ztqtj.view.fragment.warning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;

import java.io.IOException;
import java.io.InputStream;

/**
 * 预警中心-气象预警-气象预警-预警详情
 */
public class FragmentWarningCenterDetail extends Fragment {

    private ImageView contentInfo;
    private TextView content;
    private TextView title_content;
    private TextView title_date;
    private TextView defense_guidelines;
    private ImageView icon_title;

    private String titlecontent = "";
    private String titledata = "";

    private String contentText = "";
    private String shareContent = "";
    private String defend = "";
    private String icon = "";
    private String contentImageview = "";
    private TextView tvZRZHTitle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_warn_details, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        contentInfo = (ImageView) getView().findViewById(R.id.image_info);
        icon_title = (ImageView) getView().findViewById(R.id.c_icon);
        title_content = (TextView) getView().findViewById(R.id.title_content);
        title_date = (TextView) getView().findViewById(R.id.title_data);
        content = (TextView) getView().findViewById(R.id.warn_content);
        defense_guidelines = (TextView) getView().findViewById(R.id.defense_guidelines);
        tvZRZHTitle = (TextView) getView().findViewById(R.id.weatherwarn_parent);
    }

    private void initEvent() {
        ActivityWarningCenterNotFjCity activity = (ActivityWarningCenterNotFjCity) getActivity();
        TextView tv = (TextView) activity.getShareButton();
        tv.setText(getActivity().getResources().getString(R.string.warning_share));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout content = (RelativeLayout) getView().findViewById(R.id.content);
                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(content);
                bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
                ShareTools.getInstance(getActivity()).setShareContent(titlecontent,shareContent, bitmap, "0").showWindow(content);
            }
        });
//        }

        getView().findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout content = (RelativeLayout) getView().findViewById(R.id.content);
                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(content);
                bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
                //ShareUtil.share(getActivity(), shareContent, bitmap);
                ShareTools.getInstance(getActivity()).setShareContent(titlecontent,shareContent, bitmap, "0").showWindow(content);
            }
        });
    }

    private void initData() {
        PackLocalCityMain city = ZtqCityDB.getInstance().getCityMain();
//        if(city != null && !city.isFjCity) {
            getView().findViewById(R.id.ll_bottom_button).setVisibility(View.GONE);
//        } else {
           // getView().findViewById(R.id.ll_bottom_button).setVisibility(View.VISIBLE);
//        }
        Bundle bundle = getArguments();
        String zrzhTitle = bundle.getString("zrzh_title");
        if(!TextUtils.isEmpty(zrzhTitle)) {
            tvZRZHTitle.setVisibility(View.VISIBLE);
            tvZRZHTitle.setText(zrzhTitle);
            tvZRZHTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityWarningCenterNotFjCity activity = (ActivityWarningCenterNotFjCity) getActivity();
                    if(activity != null) {
                        String type = getArguments().getString("type", "1");
                        if(type.equals("1")) {
                            activity.showFragment(1);
                        } else {
                            activity.showFragment(2);
                        }
                    }
                }
            });
        } else {
            tvZRZHTitle.setVisibility(View.GONE);
        }
        WarnBean bean = (WarnBean) bundle.getSerializable("warninfo");
        if (bean != null) {
            PackShareAboutDown shareDown= (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
            String shareAdd="";
            if (shareDown!=null) {
                shareAdd=shareDown.share_content;
            }
            titlecontent = bean.level;
            contentText = bean.msg;
            icon = bean.ico;
            contentImageview = "";
            titledata = bean.put_str;
            shareContent =  contentText + "(" + titledata + ")" + shareAdd;
            defend = bean.defend;
        }
        if (TextUtils.isEmpty(icon)) {
            icon_title.setVisibility(View.GONE);
        } else {
            InputStream is = null;
            try {
                is = getResources().getAssets().open("img_warn/" + icon + ".png");
                Bitmap bm = BitmapFactory.decodeStream(is);
                bm = BitmapUtil.scaleBitmip(bm, 0.8f, 0.8f);
                is.close();
                icon_title.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e1) {
                }
            }
        }
        title_date.setText(titledata);
        title_content.setText(titlecontent);
        content.setText(contentText);
        if (contentImageview.equals("")) {
            contentInfo.setVisibility(View.GONE);
        } else {
            contentInfo.setVisibility(View.VISIBLE);
        }
        // 预警指南
        if (titlecontent.contains("解除")) {

        } else {
            defense_guidelines.setText(defend);
//				String warn = getResources().getString(getDefenseMsg(icon));
//				defense_guidelines.setText(warn);
        }
    }
}
