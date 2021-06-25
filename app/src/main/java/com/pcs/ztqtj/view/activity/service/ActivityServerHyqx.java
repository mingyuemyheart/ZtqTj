package com.pcs.ztqtj.view.activity.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.product.agriculture.ActivityAgricultureWeatherColumn;
import com.pcs.ztqtj.view.activity.product.traffic.ActivityTraffic;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
 * 专项服务-行业气象
 */
public class ActivityServerHyqx extends FragmentActivityZtqBase {

    private MyGridViewAdapter mGridViewAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_server_hyqx);
        setTitleText("行业气象");
        createImageFetcher();
        initGridView();
        okHttpColumnList();
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        GridView mGridView = findViewById(R.id.gridview);
        mGridViewAdapter = new MyGridViewAdapter(this);
        mGridView.setAdapter(mGridViewAdapter);
    }

    /**
     * 刷新GridView
     */
    private void refreshGridView() {
        mGridViewAdapter.notifyDataSetChanged();
    }

    /**
     * GridView适配器
     * @author JiangZy
     */
    private class MyGridViewAdapter extends BaseAdapter {

        private Context mContext;

        public MyGridViewAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            if (arrcolumnInfo == null) {
                return 0;
            }
            return arrcolumnInfo.size();
        }

        @Override
        public Object getItem(int position) {
            if (arrcolumnInfo == null) {
                return null;
            }
            return position;
        }

        @Override
        public long getItemId(int position) {
            if (arrcolumnInfo == null) {
                return 0;
            }
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_server_second, null);
                holder.itemImageView = (ImageView) view.findViewById(R.id.itemImageView);
                holder.itemimageview_top = (ImageView) view.findViewById(R.id.itemimageview_top);
                holder.itemName = (TextView) view.findViewById(R.id.itemName);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }

            holder.itemimageview_top.setVisibility(View.GONE);
            final ColumnInfo info = arrcolumnInfo.get(position);
            if (!TextUtils.isEmpty(info.ioc)) {
                String url = mContext.getString(R.string.file_download_url) + info.ioc;
                getImageFetcher().loadImage(url, holder.itemImageView, ImageConstant.ImageShowType.SRC);
            }

            holder.itemImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = null;
                    if (info.type.equals("10103030201")) {
                        intent = new Intent(mContext, ActivityTraffic.class);
                    } else if (info.type.equals("10103030202")) {
                        intent = new Intent(mContext, ActivityAgricultureWeatherColumn.class);
                    }
                    if (intent != null) {
                        intent.putExtra("type", info.type);
                        startActivity(intent);
                    }
                }
            });
            holder.itemName.setText(info.name);
            return view;
        }

        private class Holder {
            public ImageView itemImageView;
            public ImageView itemimageview_top;
            public TextView itemName;
        }
    }

    public List<ColumnInfo> arrcolumnInfo = new ArrayList<>();

    /**
     * 获取行业气象列表
     */
    private void okHttpColumnList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"column_list";
                    Log.e("column_list", url);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("column_list", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("column")) {
                                                    JSONObject yjxx_info_query = bobj.getJSONObject("column");
                                                    if (!TextUtil.isEmpty(yjxx_info_query.toString())) {
                                                        dismissProgressDialog();
                                                        PackColumnDown down = new PackColumnDown();
                                                        down.fillData(yjxx_info_query.toString());
                                                        arrcolumnInfo.clear();
                                                        arrcolumnInfo.addAll(down.arrcolumnInfo);
                                                        refreshGridView();
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
