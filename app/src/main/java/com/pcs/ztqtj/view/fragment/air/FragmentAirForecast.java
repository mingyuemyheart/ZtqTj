package com.pcs.ztqtj.view.fragment.air;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirForecast;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAir;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirPollutionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirPollutionUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2017/1/16.
 */

public class FragmentAirForecast extends Fragment {

    private ActivityAir activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityAir) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_air_forecast, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PcsDataBrocastReceiver.registerReceiver(getActivity(), myReceiver);
        // 创建图片缓存
        initView();
        initData();
        initEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }

    private ImageFetcher mImageFetcher;
    ;

    private void initEvent() {
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_forecast.setImageResource(R.drawable.no_pic);
                checkImageItem(position);
                btn_play.setChecked(false);
            }
        });

        btn_play.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    playImg();
                } else {
                    handler.removeMessages(0);
                }
            }
        });
    }

    private void initData() {
        mImageFetcher = activity.getMImageFetcher();
        //mImageFetcher.addListener(imgListener);
        getForeCast();
    }


    private ListenerImageLoad imgListener=new ListenerImageLoad() {
        @Override
        public void done(String key, boolean isSucc) {
            if (!TextUtils.isEmpty(imgPath) && imgPath.equals(key) && isSucc) {
                BitmapDrawable bm = mImageFetcher.getImageCache().getBitmapFromMemCache(key);
                show_forecast.setImageDrawable(bm);
            }
        }
    };


    private ImageView show_forecast;

    private TextView title;
    private TextView desc;

    private GridView grid_view;
    private CheckBox btn_play;
    private AdapterAirForecast adapter;
    private List<PackAirPollutionDown.ForecustItem> dataList;

    private void initView() {
        show_forecast = (ImageView) getActivity().findViewById(R.id.show_forecast);
        btn_play = (CheckBox) getActivity().findViewById(R.id.btn_play);
        title = (TextView) getActivity().findViewById(R.id.title);
        desc = (TextView) getActivity().findViewById(R.id.desc);
        title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        grid_view = (GridView) getActivity().findViewById(R.id.grid_view);
        dataList = new ArrayList<>();
        adapter = new AdapterAirForecast(dataList);
        grid_view.setAdapter(adapter);
    }


    private void cleanForecastView() {
        dataList.clear();
        adapter.selectItem(0);
        adapter.notifyDataSetChanged();
        title.setText("");
        desc.setText("");
        show_forecast.setImageResource(R.drawable.no_pic);
    }

    private void playImg() {
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int item = adapter.getSelectItem();
            if (item < adapter.getCount() - 1 && adapter.getCount() != 0) {
                checkImageItem(item + 1);
            } else if (item != 0 && item == adapter.getCount() - 1) {
                checkImageItem(0);
            }
            playImg();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PcsDataBrocastReceiver.unregisterReceiver(getActivity(), myReceiver);
    }

    public void getForeCast() {
        activity.showProgressDialog();
        PackAirPollutionUp up = new PackAirPollutionUp();
        PcsDataDownload.addDownload(up);
    }


    private PcsDataBrocastReceiver myReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(PackAirPollutionUp.NAME)) {
                activity.dismissProgressDialog();
                PackAirPollutionDown down = (PackAirPollutionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down != null) {
                    reflushForecast(down);
                }
            }
        }
    };

    private void reflushForecast(PackAirPollutionDown down) {
        if (down != null) {
            dataList.clear();
            dataList.addAll(down.dataList);
            adapter.notifyDataSetChanged();
            title.setText(down.title);
            desc.setText(down.desc);
            if (dataList.size() > 0) {
                checkImageItem(0);
            }
        }
    }

    private String imgPath = "";

    private void checkImageItem(int position) {
        PackAirPollutionDown.ForecustItem down = (PackAirPollutionDown.ForecustItem) adapter.getItem(position);
        String path = getString(R.string.file_download_url) + down.img_url;
        imgPath = path;
        //mImageFetcher.loadImage(path, null, ImageConstant.ImageShowType.NONE);
        mImageFetcher.loadImage(path, show_forecast, ImageConstant.ImageShowType.SRC);
        adapter.selectItem(position);
        adapter.notifyDataSetChanged();
    }

}
