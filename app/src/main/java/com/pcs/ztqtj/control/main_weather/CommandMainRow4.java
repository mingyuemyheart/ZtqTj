package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.life_number.AdapterLifeNumberGridView;
import com.pcs.ztqtj.control.tool.ChineseDateUtil;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.calendar.ActivityCalendarSecond;
import com.pcs.ztqtj.view.activity.lifenumber.ActivityLifeNumberDetail;
import com.pcs.ztqtj.view.activity.lifenumber.ActivityLifeNumberEdit;

import java.util.Calendar;

/**
 * JiangZy on 2016/6/3.
 */
public class CommandMainRow4 extends CommandMainBase {
    private Activity mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    // 生活指数的适配器
    private AdapterLifeNumberGridView mLifeNumberAdapter = null;

    private TextView tv_calendar_content;

    public CommandMainRow4(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher) {
        mActivity = activity;
        mRootLayout = rootLayout;
        mImageFetcher = imageFetcher;
    }

    @Override
    protected void init() {
        View rowView = LayoutInflater.from(mActivity).inflate(
                R.layout.item_home_weather_4, null);
        rowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(rowView);
        //初始化适配器
        initAdapterEtc();
        setStatus(Status.SUCC);
    }

    @Override
    protected void refresh() {
        if(mLifeNumberAdapter!=null){
            // 刷新
            mLifeNumberAdapter.notifyDataSetChanged();
        }
        setStatus(Status.SUCC);
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null) {
            return;
        }
        PackLifeNumberUp packLifeNumber = new PackLifeNumberUp();
        packLifeNumber.area = cityMain.ID;

        PackLifeNumberDown mPackLifeNumberDown = (PackLifeNumberDown) PcsDataManager.getInstance().getNetPack(packLifeNumber.getName());
        if(mPackLifeNumberDown==null||mPackLifeNumberDown.dataList.size()==0){
            rowView.setVisibility(View.GONE);
        }else{
            rowView.setVisibility(View.VISIBLE);
        }
    }
   private  View rowView;
    /**
     * 初始化适配器等
     */
    private void initAdapterEtc() {
        rowView = mRootLayout.findViewById(R.id.layout_home_weather_4);
        mLifeNumberAdapter = new AdapterLifeNumberGridView(mActivity,mImageFetcher);
        GridView gridView = (GridView) rowView.findViewById(R.id.gridView);
        gridView.setAdapter(mLifeNumberAdapter);
//        gridView.setOnItemClickListener(onItemClickRow4);
        // 按钮
        Button btnMore = (Button) rowView.findViewById(R.id.btn_more);
//        btnMore.setOnClickListener(onClickRow4);
        tv_calendar_content= (TextView) rowView.findViewById(R.id.tv_calendar_content);

        Calendar calStartDay = Calendar.getInstance();
        ChineseDateUtil c = new ChineseDateUtil(calStartDay);
        Calendar calToday = Calendar.getInstance();
        int week = calToday.get(Calendar.DAY_OF_WEEK) - 1;
        int m = calToday.get(Calendar.MONTH)+1;
        int d = calToday.get(Calendar.DAY_OF_MONTH);
        String weeks = "";
        if (week == 1) {
            weeks = "星期一";
        } else if (week == 2) {
            weeks = "星期二";
        } else if (week == 3) {
            weeks = "星期三";
        } else if (week == 4) {
            weeks = "星期四";
        } else if (week == 5) {
            weeks = "星期五";
        } else if (week == 6) {
            weeks = "星期六";
        } else if (week == 0) {
            weeks = "星期日";
        }
        tv_calendar_content.setText(m + "月" + d + "日  " + weeks + "  " + c.getChinaYear() + c.getChinaMonthString() + c.getChinaDayString());
        tv_calendar_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(mActivity, ActivityCalendarSecond.class);
                mActivity.startActivity(intent3);
            }
        });
    }

    /**
     * 按钮监听第4行
     */
    private View.OnClickListener onClickRow4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 跳转
            Intent it = new Intent();
            it.setClass(mActivity, ActivityLifeNumberEdit.class);
            mActivity.startActivityForResult(it, MyConfigure.RESULT_LIFENUMBER);
        }
    };

    /**
     * 第四行GridView监听
     */
    private AdapterView.OnItemClickListener onItemClickRow4 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            PackLifeNumberDown.LifeNumber pack = mLifeNumberAdapter.getItemPosition(position);
            if (pack == null) {
                Toast.makeText(mActivity, "获取生活指数失败", Toast.LENGTH_SHORT).show();
                return;
            }
            // 跳转
            Intent it = new Intent();
            it.putExtra("key", pack.id);
            it.setClass(mActivity, ActivityLifeNumberDetail.class);
            mActivity.startActivity(it);
        }
    };
}
