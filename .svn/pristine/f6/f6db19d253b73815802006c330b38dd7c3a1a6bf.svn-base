package com.pcs.ztqtj.view.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 小部件预警列表adapter
 * Created by tyaathome on 2018/1/9.
 */

public class WarnGridService extends RemoteViewsService {

    public static final String EXTRA_ITEM = "com.pcs.ztq.extra_item";

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
                mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
            }
        }

        @Override
        public void onCreate() {
            updateData();
        }

        private void updateData() {
            // 首页预警
            PackLocalCity warncityinfo = ZtqCityDB.getInstance().getCityMain();
            if (warncityinfo != null) {
                PackYjxxIndexFbUp packUp = new PackYjxxIndexFbUp();
                packUp.setCity(warncityinfo);
                PackYjxxIndexFbDown warnbean = (PackYjxxIndexFbDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
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
//                    dataList.clear();
//                    for(int i = 0; i < 4; i++) {
//                        dataList.addAll(warnList);
//                    }
                    dataList = warnList;
                }
            }
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
            updateData();
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
