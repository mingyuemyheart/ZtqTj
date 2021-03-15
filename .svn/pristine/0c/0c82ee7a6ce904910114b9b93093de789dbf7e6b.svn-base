package com.pcs.ztqtj.control.adapter.life_number;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalLife;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown.LifeNumber;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器：生活指数GridView
 *
 * @author JiangZy
 */
public class AdapterLifeNumberGridView extends BaseAdapter {
    private Context mContext;
    // 网络指数列表
    PackLifeNumberDown mPackLifeNumberDown = null;
    private ImageFetcher mImageFetcher;
    private List<LifeNumber> localNum = new ArrayList<>();
    private MyReceiver mReceiver;

    public AdapterLifeNumberGridView(Context context, ImageFetcher mImageFetcher) {
        mContext = context;
        this.mImageFetcher = mImageFetcher;
    }

    @Override
    public void notifyDataSetChanged() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null) {
            return;
        }
        PackLifeNumberUp packLifeNumber = new PackLifeNumberUp();
        packLifeNumber.area = cityMain.ID;

        mPackLifeNumberDown = (PackLifeNumberDown) PcsDataManager.getInstance().getNetPack(packLifeNumber.getName());
        if (mPackLifeNumberDown == null && mReceiver == null) {
            mReceiver = new MyReceiver();
            PcsDataBrocastReceiver.registerReceiver(mContext, mReceiver);
        }
        localNum = PackLocalLife.getInstance().getLifeNumber(mContext);
        if (!PackLocalLife.getInstance().getDefault(mContext)) {
            if (localNum != null) {
                localNum.clear();
                if (mPackLifeNumberDown != null && mPackLifeNumberDown.dataList.size() > 0) {
                    for (int i = 0; i <  mPackLifeNumberDown.dataList.size(); i++) {
                        localNum.add(mPackLifeNumberDown.dataList.get(i));
                    }
                }
                PackLocalLife.getInstance().setLifeNumber(mContext, localNum);
            }
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (localNum == null) {
            return 0;
        }
        return localNum.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_home_life_number, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.text_title);
            holder.content = (TextView) convertView
                    .findViewById(R.id.text_content);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        try {
            LifeNumber packLocal = mPackLifeNumberDown.dataMap
                    .get(localNum.get(position).id);
            String url = mContext.getString(R.string.file_download_url)
                    + packLocal.ico_path;
            // 名称
            mImageFetcher.loadImage(
                    mContext.getString(R.string.file_download_url)
                            + packLocal.ico_path, holder.icon,
                    ImageConstant.ImageShowType.SRC);
            holder.title.setText(packLocal.index_name + "指数");
            holder.content.setText(packLocal.simple_des);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public LifeNumber getItemPosition(int position) {
        LifeNumber packLocal = mPackLifeNumberDown.dataMap.get(localNum.get(position).id);
        return packLocal;
    }

    class Holder {
        public ImageView icon;
        public TextView title;
        public TextView content;
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (!TextUtils.isEmpty(errorStr)) {
                return;
            }

            if (mPackLifeNumberDown != null) {
                PcsDataBrocastReceiver.unregisterReceiver(mContext, MyReceiver.this);
                return;
            }

            PcsDataBrocastReceiver.unregisterReceiver(mContext, MyReceiver.this);
            notifyDataSetChanged();
        }
    }
}
