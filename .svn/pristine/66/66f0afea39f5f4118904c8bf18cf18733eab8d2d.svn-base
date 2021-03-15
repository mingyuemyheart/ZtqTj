package com.pcs.ztqtj.view.fragment.warning.emergency_responsibility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjUserInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/8/16 0016.
 * chen_jx
 */

public class AdatperRespon extends BaseAdapter {

    private Context mcontext;
    private List<YjUserInfo> mlist;
    private boolean isVisibility = true;

    public AdatperRespon(Context context, List<YjUserInfo> list) {
        super();
        this.mcontext = context;
        this.mlist = list;
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
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_respon, null);
            handler.company = (TextView) view.findViewById(R.id.tv_respon_company);
            handler.name = (TextView) view.findViewById(R.id.tv_respon_name);
            handler.phone = (TextView) view.findViewById(R.id.tv_respon_phone);
            handler.tv_loading = (TextView) view.findViewById(R.id.tv_loading);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.company.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
            handler.name.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
            handler.phone.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
        }

        if (mlist.size() == 1) {
            handler.company.setText(mlist.get(position).depart_name);
            handler.name.setText(mlist.get(position).fullname);
            handler.phone.setText(mlist.get(position).mobile);
            handler.tv_loading.setVisibility(View.GONE);
        } else if (position == mlist.size() - 1) {
            if (isVisibility) {
                handler.tv_loading.setVisibility(View.VISIBLE);
            } else {
                handler.tv_loading.setVisibility(View.GONE);
            }
        } else if (position < mlist.size()) {
            handler.company.setText(mlist.get(position).depart_name);
            handler.name.setText(mlist.get(position).fullname);
            handler.phone.setText(mlist.get(position).mobile);
            handler.tv_loading.setVisibility(View.GONE);
        }

        handler.phone.setOnClickListener(new BtnClicklistener(position, handler.phone));

        return view;
    }

    class BtnClicklistener implements View.OnClickListener {
        private int position;
        private View view;

        public BtnClicklistener(int pos, View view) {
            this.position = pos;
            this.view = view;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_respon_phone:
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                            + mlist.get(position).mobile));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(intent);
                    break;
            }
        }
    }


    public void setLoadingVisibility(boolean isVisibility) {
        this.isVisibility = isVisibility;
    }

    private class Handler {
        public TextView company;
        public TextView name;
        public TextView phone;
        public TextView tv_loading;
        public LinearLayout lay_disaster_detail;
    }
}
