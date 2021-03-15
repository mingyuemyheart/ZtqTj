package com.pcs.ztqtj.view.activity.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterPopWindow;

import java.util.List;

/**
 * Created by tyaathome on 2017/8/16.
 */

public class LiveQueryPopupWindowTool {
    private static LiveQueryPopupWindowTool instance;
    private Context mContext;
    private OnPopupWindowItemClickListener listener;
    private View dropDownView;
    private PopupWindow pop;

    private LiveQueryPopupWindowTool(Context context) {
        this.mContext = context;
    }

    public static LiveQueryPopupWindowTool getInstance(Context context) {
        if(instance == null) {
            instance = new LiveQueryPopupWindowTool(context);
        }
        return instance;
    }

    public LiveQueryPopupWindowTool setListener(OnPopupWindowItemClickListener listener) {
        this.listener = listener;
        return instance;
    }

    /**
     *
     * @param dropDownView
     * @param dataeaum
     */
    public LiveQueryPopupWindowTool createPopupWindow(final View dropDownView,
                                   final List<String> dataeaum) {
        this.dropDownView = dropDownView;
        AdapterPopWindow dataAdapter = new AdapterPopWindow(mContext, dataeaum);
        View popcontent = LayoutInflater.from(mContext).inflate(
                R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        pop = new PopupWindow(mContext);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
        // 调整下拉框长度
        View mView = dataAdapter.getView(0, null, lv);
        mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int height = mView.getMeasuredHeight();
        height += lv.getDividerHeight();
        height *= 8; // item的高度*个数
        if (dataeaum.size() < 9) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(height);
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pop.dismiss();
                if(listener != null) {
                    listener.onClick(position);
                }

            }
        });
        return instance;
    }

    public void show() {
        if(pop != null) {
            pop.showAsDropDown(dropDownView);
        }
    }

    public interface OnPopupWindowItemClickListener {
        void onClick(int position);
    }
}
