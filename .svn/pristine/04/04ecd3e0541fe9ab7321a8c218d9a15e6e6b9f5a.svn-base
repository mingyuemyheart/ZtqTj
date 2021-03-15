package com.pcs.ztqtj.control.adapter.disaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjMyReport;

import java.util.List;

/**
 * Created by Administrator on 2017/8/4 0004.
 * chen_jx
 */

public class AdatperReporting extends BaseAdapter{

    private Context mcontext;
    private List<YjMyReport> mlist;

    public AdatperReporting(Context context,List<YjMyReport> list){
        super();
        this.mcontext=context;
        this.mlist=list;
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        Handler handler = null;

        if (view == null) {
            handler = new Handler();
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_disaster_report, null);
            handler.time = (TextView) view.findViewById(R.id.disaster_time);
            handler.address = (TextView) view.findViewById(R.id.disaster_address);
            handler.status = (TextView) view.findViewById(R.id.disaster_type);
            handler.lay_disaster_detail= (LinearLayout) view.findViewById(R.id.lay_disaster_detail);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.time.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
            handler.address.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
            handler.status.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
        }
        handler.time.setText(mlist.get(position).pub_time);
        handler.address.setText(mlist.get(position).zq_addr);
        if(mlist.get(position).status.equals("0")){
            handler.status.setText("待审核");
        }else if(mlist.get(position).status.equals("1")){
            handler.status.setText("已通过");
        }else {
            handler.status.setText("被驳回");
        }

        return view;
    }

    private class Handler {
        public TextView time;
        public TextView address;
        public TextView status;
        public LinearLayout lay_disaster_detail;
    }

}
