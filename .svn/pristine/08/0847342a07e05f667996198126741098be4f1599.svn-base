package com.pcs.ztqtj.view.activity.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.product.agriculture.ActivityAgricultureWeatherColumn;
import com.pcs.ztqtj.view.activity.product.traffic.ActivityTraffic;

import java.util.ArrayList;
import java.util.List;

/**
 * 气象生活第二页
 *
 * @author JiangZy
 */
public class ActivityServerHyqx extends FragmentActivityZtqBase {


    private GridView mGridView;
    private MyGridViewAdapter mGridViewAdapter;
    private MyReceiver receiver = new MyReceiver();
    private PackColumnUp columnUp = new PackColumnUp();


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_server_hyqx);
        setTitleText("行业气象");
        createImageFetcher();
        // 初始化GridView
        initGridView();
        intReq();
        PcsDataBrocastReceiver.registerReceiver(ActivityServerHyqx.this,
                receiver);
    }

    private void intReq() {
        showProgressDialog();
        columnUp.column_type = "26";
        PcsDataDownload.addDownload(columnUp);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridViewAdapter = new MyGridViewAdapter(this);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    /**
     * 刷新GridView
     */
    private void refreshGridView() {
        mGridViewAdapter.notifyDataSetChanged();
    }

    /**
     * GridView适配器
     *
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
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_server_second, null);
                holder.itemImageView = (ImageView) view
                        .findViewById(R.id.itemImageView);
                holder.itemimageview_top = (ImageView) view
                        .findViewById(R.id.itemimageview_top);

                holder.itemName = (TextView) view.findViewById(R.id.itemName);

                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            holder.itemimageview_top.setVisibility(View.GONE);
            ColumnInfo info = arrcolumnInfo.get(position);
            if (!TextUtils.isEmpty(info.ioc)) {
                String url = ActivityServerHyqx.this
                        .getString(R.string.file_download_url)
                        + "/"
                        + info.ioc;
                getImageFetcher().loadImage(url, holder.itemImageView,
                        ImageConstant.ImageShowType.SRC);
            }

            holder.itemImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent;
                    if (arrcolumnInfo.get(position).type.equals("1")) {
                        intent =new Intent(ActivityServerHyqx.this, ActivityTraffic.class);
                        startActivity(intent);
                    } else if (arrcolumnInfo.get(position).type.equals("2")) {
                         intent=new Intent(ActivityServerHyqx.this, ActivityAgricultureWeatherColumn.class);
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

    public List<ColumnInfo> arrcolumnInfo = new ArrayList<ColumnInfo>();

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            if (columnUp.getName().equals(name)) {//
                dismissProgressDialog();
                PackColumnDown down =
                        (PackColumnDown) PcsDataManager.getInstance().getNetPack(name);
                if (down == null) {
                    return;
                }
                arrcolumnInfo.clear();
                arrcolumnInfo.addAll(down.arrcolumnInfo);
                refreshGridView();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
