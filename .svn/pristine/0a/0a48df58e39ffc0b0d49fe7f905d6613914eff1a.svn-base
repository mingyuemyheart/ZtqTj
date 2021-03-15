package com.pcs.ztqtj.control.adapter.adapter_set;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.ztqtj.R;

import java.util.List;
import java.util.Map;

/**
 * Created by tyaathome on 2016/8/26.
 */
public class AdapterColumnManager extends AdapterFragmentSetManager {

    private String strText = "";
    private int rightTextColor = -1;

    public AdapterColumnManager(Context context, List<Map<String, String>> listdata, int rebootIndex) {
        super(context, listdata, rebootIndex);
    }

    public AdapterColumnManager(Context context, List<Map<String, String>> listdata, int rebootIndex, int
            versionIndex) {
        super(context, listdata, rebootIndex, versionIndex);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_fragmentset_2, null);
            holder.layout = (RelativeLayout) view
                    .findViewById(R.id.item_fragmentset_id);
            holder.choosebutton= (CheckBox) view.findViewById(R.id.open_icon);
            holder.itemTest = (TextView) view.findViewById(R.id.explain_text);
            holder.icon = (ImageView) view.findViewById(R.id.setimage_icon);
            holder.subText = (TextView) view.findViewById(R.id.sub_text);
            holder.rightText = (TextView) view.findViewById(R.id.tv_right);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

//        if(position%2 == 1) {
//            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.adapter_column_manager_odd));
//        } else {
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.bg_white));
//        }
        holder.choosebutton.setVisibility(View.GONE);
//        if (position == 0) {
//            holder.choosebutton.setVisibility(View.VISIBLE);
//            holder.choosebutton
//                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView,
//                                                     boolean isChecked) {
//                            // 开机启动监听事件
//                            if (isChecked) {
//                                Util.setPreferencesBooleanValue(context,
//                                        "root", "start", true);
//                            } else {
//                                Util.setPreferencesBooleanValue(context,
//                                        "root", "start", false);
//                            }
//
//                        }
//                    });
//        } else {
//            holder.choosebutton.setVisibility(View.GONE);
//        }

        if(position == 3) {
            holder.rightText.setVisibility(View.VISIBLE);
            String versionName = "";
            try {
                versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                        .versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            versionName = !TextUtils.isEmpty(versionName) ? "当前" + versionName : versionName;
            holder.rightText.setText(versionName);

            holder.rightText.setText(strText);
            if(rightTextColor != -1) {
                holder.rightText.setTextColor(rightTextColor);
            }
        } else {
            holder.rightText.setVisibility(View.GONE);
        }

        try {
            holder.choosebutton.setChecked(Util.getPreferencesBooleanValue(
                    context, "root", "start"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemTest.setText(listdata.get(position).get("t"));
        if (position == getCount()) {
            holder.icon.setVisibility(View.GONE);
        } else {
            try {
                holder.icon.setImageResource(Integer.parseInt((listdata
                        .get(position).get("i"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public void setRightText(String text, int color) {
        strText = text;
        rightTextColor = color;
    }
}
