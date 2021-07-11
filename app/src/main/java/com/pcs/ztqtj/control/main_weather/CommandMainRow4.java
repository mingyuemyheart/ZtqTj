package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.life_number.AdapterLifeNumberGridView;
import com.pcs.ztqtj.control.tool.ChineseDateUtil;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.calendar.ActivityCalendarSecond;
import com.pcs.ztqtj.view.activity.lifenumber.ActivityLifeNumberEdit;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-生活指数
 */
public class CommandMainRow4 extends CommandMainBase {

    private Activity mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private AdapterLifeNumberGridView mLifeNumberAdapter = null;
    private List<PackLifeNumberDown.LifeNumber> dataList = new ArrayList<>();

    public CommandMainRow4(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher) {
        mActivity = activity;
        mRootLayout = rootLayout;
        mImageFetcher = imageFetcher;
    }

    @Override
    protected void init() {
        View rowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_4, null);
        rowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(rowView);
        //初始化适配器
        initAdapterEtc();
        setStatus(Status.SUCC);
//        okHttpAqi();
    }

    @Override
    protected void refresh() {
        if (mLifeNumberAdapter!=null){
            mLifeNumberAdapter.notifyDataSetChanged();
        }
        setStatus(Status.SUCC);
        okHttpAqi();
    }
   private View rowView;
    /**
     * 初始化适配器等
     */
    private void initAdapterEtc() {
        rowView = mRootLayout.findViewById(R.id.layout_home_weather_4);
        mLifeNumberAdapter = new AdapterLifeNumberGridView(mActivity,mImageFetcher, dataList);
        GridView gridView = (GridView) rowView.findViewById(R.id.gridView);
        gridView.setAdapter(mLifeNumberAdapter);
        gridView.setOnItemClickListener(onItemClickRow4);
        // 按钮
        Button btnMore = (Button) rowView.findViewById(R.id.btn_more);
//        btnMore.setOnClickListener(onClickRow4);
        TextView tv_calendar_content = rowView.findViewById(R.id.tv_calendar_content);

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
            dialogAqi(pack);
        }
    };

    private void dialogAqi(PackLifeNumberDown.LifeNumber pack) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_aqi, null);
        ImageView image = view.findViewById(R.id.image);
        TextView text_title = view.findViewById(R.id.text_title);
        TextView text_content = view.findViewById(R.id.text_content);

        final Dialog dialog = new Dialog(mActivity, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        try {
            mImageFetcher.loadImage(mActivity.getString(R.string.shzs)+ pack.ico_path, image, ImageConstant.ImageShowType.SRC);
            text_title.setText(pack.index_name);
            text_content.setText(pack.simple_des);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取空气指数数据
     */
    private void okHttpAqi() {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if (city == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("shzs", json);
                    final String url = CONST.BASE_URL+"shzs";
                    Log.e("shzs", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("shzs", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("shzs")) {
                                                    JSONObject listobj = bobj.getJSONObject("shzs");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        PackLifeNumberDown mPackLifeNumberDown = new PackLifeNumberDown();
                                                        mPackLifeNumberDown.fillData(listobj.toString());
                                                        if (mPackLifeNumberDown == null || mPackLifeNumberDown.dataList.size() == 0) {
                                                            rowView.setVisibility(View.GONE);
                                                        } else {
                                                            rowView.setVisibility(View.VISIBLE);
                                                            dataList.clear();
                                                            dataList.addAll(mPackLifeNumberDown.dataList);
                                                            if (mLifeNumberAdapter != null) {
                                                                mLifeNumberAdapter.notifyDataSetChanged();
                                                            }
                                                        }
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
