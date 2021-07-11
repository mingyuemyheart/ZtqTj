package com.pcs.ztqtj.view.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
 * 小部件预警列表adapter
 */
public class WarnGridService extends RemoteViewsService {

    private Handler mUIHandler = new Handler();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WarnGridServiceFactory(getApplicationContext(), intent);
    }

    private class WarnGridServiceFactory implements RemoteViewsFactory {

        private Context context;
        private List<YjxxInfo> dataList = new ArrayList<>();
        private int mAppWidgetId;

        public WarnGridServiceFactory(Context context, Intent intent) {
            this.context = context;
            if(intent.hasExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS)) {
                dataList = intent.getParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);
            }
            if(intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
        }

        @Override
        public void onCreate() {
            okHttpWarningImages();
        }

        /**
         * 获取预警，首页预警图标
         */
        private void okHttpWarningImages() {
            final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
            if(city == null) {
                return;
            }
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
                        Log.e("yjxx_index_fb_list", json);
                        final String url = CONST.BASE_URL+"yjxx_index_fb_list";
                        Log.e("yjxx_index_fb_list", url);
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
                                mUIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Log.e("yjxx_index_fb_list", result);
                                        if (!TextUtil.isEmpty(result)) {
                                            try {
                                                JSONObject obj = new JSONObject(result);
                                                if (!obj.isNull("b")) {
                                                    JSONObject bobj = obj.getJSONObject("b");
                                                    if (!bobj.isNull("yjxx_index_fb_list")) {
                                                        JSONObject listobj = bobj.getJSONObject("yjxx_index_fb_list");
                                                        if (!TextUtil.isEmpty(listobj.toString())) {
                                                            PackYjxxIndexFbDown warnbean = new PackYjxxIndexFbDown();
                                                            warnbean.fillData(listobj.toString());
                                                            if (warnbean != null) {
                                                                List<YjxxInfo> warnList = new ArrayList<>();
                                                                if(warnbean.list.size() == 2) {
                                                                    String first = warnbean.list.get(0);
                                                                    if(first.equals("省")) {
                                                                        warnList = warnbean.list_3;
                                                                    } else if(first.equals("市")) {
                                                                        warnList = warnbean.list_3;
                                                                    }
                                                                } else if(warnbean.list.size() == 1) {
                                                                    warnList = warnbean.list_2;
                                                                }
                                                                dataList = warnList;
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

        private Bitmap getBitmap(String path) {
            Bitmap bitmap = null;
            InputStream input = null;
            AssetManager asset = PcsInit.getInstance().getContext().getAssets();
            try {
                input = asset.open(path);
                if (input != null) {
                    bitmap = BitmapFactory.decodeStream(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
            }
            return bitmap;
        }


        @Override
        public void onDataSetChanged() {
            okHttpWarningImages();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(dataList.size() > position) {
                YjxxInfo bean = dataList.get(position);
                String path = "img_warn/" + bean.ico + ".png";
                Bitmap bitmap = getBitmap(path);
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_widget_warn);
                if (bitmap != null) {
                    rv.setImageViewBitmap(R.id.iv, bitmap);
                }
                Bundle bundle = new Bundle();
                bundle.putString("t", "气象预警");
                bundle.putString("i", bean.ico);
                bundle.putString("id", bean.id);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                rv.setOnClickFillInIntent(R.id.iv, intent);
                return rv;
            } else {
                return new RemoteViews(context.getPackageName(), R.layout.item_widget_warn);
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
