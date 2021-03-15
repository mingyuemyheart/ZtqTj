package com.pcs.ztqtj.control.adapter.adapter_warn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterWeaWarnList extends BaseAdapter {
    private Context context;
    private List<WarnCenterYJXXGridBean> listData;

    public AdapterWeaWarnList(Context context, List<WarnCenterYJXXGridBean> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
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
        ListViewChildHolder lvchildholder;
        if (null == convertView) {
            lvchildholder = new ListViewChildHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_weather_warn_child, null);
            lvchildholder.cTitle = (TextView) convertView.findViewById(R.id.warn_title);
            lvchildholder.cInformation = (TextView) convertView.findViewById(R.id.warn_info);
            lvchildholder.classImage = (ImageView) convertView.findViewById(R.id.warn_image);
            lvchildholder.tv_warn_info_flag = (TextView) convertView.findViewById(R.id.tv_warn_info_flag);
            convertView.setTag(lvchildholder);
        } else {
            lvchildholder = (ListViewChildHolder) convertView.getTag();
        }
        lvchildholder.cTitle.setText(listData.get(position).level);
        lvchildholder.cInformation.setText(listData.get(position).put_str);
        String id = SharedPreferencesUtil.getData(listData.get(position).id, "");

        String date2 = listData.get(position).put_time + ":00";
        date2 = date2.replace("年", "-");
        date2 = date2.replace("月", "-");
        date2 = date2.replace("日", " ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int days = 0;
        try {
            Date date3 = format.parse(date2);
            String date = format.format(new Date());
            Date date4 = format.parse(date);
            days = differentDaysByMillisecond(date3, date4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (days < 1) {
            if (!TextUtils.isEmpty(id)) {
                lvchildholder.tv_warn_info_flag.setText("");
            } else {
                lvchildholder.tv_warn_info_flag.setText("未读");
            }
        } else {
            lvchildholder.tv_warn_info_flag.setText("");
            SharedPreferencesUtil.putData(listData.get(position).id,listData.get(position).id);
        }
        if (listData.get(position).ico.equals("")) {
            lvchildholder.classImage.setVisibility(View.GONE);
        } else {
            InputStream is = null;
            try {
                is = context.getResources().getAssets().open("img_warn/" + listData.get(position).ico + ".png");
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();

//				bm = BitmapUtil.scaleBitmip(bm, 0.6f, 0.6f);

                lvchildholder.classImage.setImageBitmap(bm);
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
        return convertView;
    }

    public class ListViewChildHolder {
        public TextView cTitle;
        public TextView cInformation, tv_warn_info_flag;
        public ImageView classImage;
    }


    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return Math.abs(days);
    }
}