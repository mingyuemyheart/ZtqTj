package com.pcs.ztqtj.control.inter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;

import java.util.List;

/**
 * Created by tyaathome on 2016/9/12.
 */
public abstract class UserFragmentCallBack extends Fragment {
    // 自定义点击提交按钮行为
    public abstract void onClickSubmitButton();
    // 点击提交时所填写的内容是否正确
    public abstract boolean check();

    private PopupWindow popupWindow;

    /**
     * 显示下来选择列表
     */
    public void showPopupWindow(Context context, final TextView tvView, final List<String> listData, final ItemClick listener) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        // 设置按钮的点击事件
        ListView chose_listview = (ListView) contentView.findViewById(R.id.mylistviw);
        AdapterData adapter = new AdapterData(context, listData);
        chose_listview.setAdapter(adapter);
        chose_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dimissPop();
                tvView.setText(listData.get(position));
                if (listener != null) {
                    listener.itemClick(position, listData.get(position));
                }
            }
        });
        int height = 0;
        if(adapter.getCount() > 0) {
            View childView = adapter.getView(0, null, chose_listview);
            childView.measure(0, View.MeasureSpec.UNSPECIFIED);
            height = childView.getMeasuredHeight();
        }
        if(height != 0) {
            popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, height*5, true);
        } else {
            popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar
                    .LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.alpha100));
        int width = tvView.getWidth();
        // 设置好参数之后再show
        int off = 0;
//        int off = tvView.getWidth() / 4;
        popupWindow.setWidth(width);
        popupWindow.showAsDropDown(tvView, -off, 0);
    }

    /**
     * 关闭popup
     */
    private void dimissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();

        }
    }
}
