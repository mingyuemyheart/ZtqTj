package com.pcs.ztqtj.control.main_weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.pcs.ztqtj.view.myview.MainViewPager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
    private TextView tvLifeTime;

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
        okHttpAd();
    }

    @Override
    protected void refresh() {
        okHttpAqi();
    }

    private View rowView;
    /**
     * 初始化适配器等
     */
    private void initAdapterEtc() {
        rowView = mRootLayout.findViewById(R.id.layout_home_weather_4);
        tvLifeTime = rowView.findViewById(R.id.tvLifeTime);
        viewPager = rowView.findViewById(R.id.viewPager);
        mLifeNumberAdapter = new AdapterLifeNumberGridView(mActivity,mImageFetcher, dataList);
        GridView gridView = (GridView) rowView.findViewById(R.id.gridView);
        gridView.setAdapter(mLifeNumberAdapter);
        gridView.setOnItemClickListener(onItemClickRow4);
        // 按钮
//        Button btnMore = (Button) rowView.findViewById(R.id.btn_more);
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
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                                                            if (!TextUtil.isEmpty(mPackLifeNumberDown.dataList.get(0).create_time)) {
                                                                tvLifeTime.setText(mPackLifeNumberDown.dataList.get(0).create_time+"更新");
                                                            }
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

    /**
     * 获取广告
     */
    private void okHttpAd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("ad_type", "A002");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"ad_list";
                    Log.e("ad_list", url);
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
                                    if (!TextUtil.isEmpty(result)) {
                                        initViewPager(result);
                                    } else {
                                        viewPager.setVisibility(View.GONE);
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

    private MainViewPager viewPager = null;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private void initViewPager(String result) {
        fragments.clear();
        try {
            JSONObject obj = new JSONObject(result);
            if (!obj.isNull("b")) {
                JSONObject bObj = obj.getJSONObject("b");
                if (!bObj.isNull("ad")) {
                    JSONObject adObj = bObj.getJSONObject("ad");
                    if (!adObj.isNull("ad_list")) {
                        JSONArray array = adObj.getJSONArray("ad_list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject itemObj = array.getJSONObject(i);
                            String imgUrl = mActivity.getResources().getString(R.string.msyb) + itemObj.getString("img_path");
                            String name = itemObj.getString("title");
                            String dataUrl = itemObj.getString("url");
                            Fragment fragment = new FragmentAd(mImageFetcher);
                            Bundle bundle = new Bundle();
                            bundle.putString("imgUrl", imgUrl);
                            bundle.putString("name", name);
                            bundle.putString("dataUrl", dataUrl);
                            fragment.setArguments(bundle);
                            fragments.add(fragment);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (fragments.size() > 0) {
            viewPager.setVisibility(View.VISIBLE);
        } else {
            viewPager.setVisibility(View.GONE);
        }
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        mHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
    }

    private final int AUTO_PLUS = 1;
    private static final int PHOTO_CHANGE_TIME = 2000;//定时变量
    private int index_plus = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO_PLUS:
                    viewPager.setCurrentItem(index_plus++);//收到消息后设置当前要显示的图片
                    mHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
                    if (index_plus >= fragments.size()) {
                        index_plus = 0;
                    }
                    break;
                default:
                    break;
            }
        };
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            index_plus = arg0;
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            try {
                ((ViewPager) container).removeView(fragments.get(position).getView());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                mActivity.getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

}
