package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjHourDown.RainFall;

import java.util.List;

/**
 * @author Z 自动降雨量统计
 */
public class AdatperAutoRainFall extends BaseAdapter {
    private Context context;
    private List<RainFall> rainfalllist;
    private int clickposition = 1;
    private RainFallIn listerner;

    public AdatperAutoRainFall(Context context, List<RainFall> rainfalllist, RainFallIn listerner) {
        this.context = context;
        this.rainfalllist = rainfalllist;
        this.listerner = listerner;
    }

    ;

    @Override
    public int getCount() {
        return rainfalllist.size();
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
    public View getView(final int position, View view, ViewGroup parent) {

        Handler handler = null;

        if (view == null) {
            handler = new Handler();
            view = LayoutInflater.from(context).inflate(R.layout.item_livequery_autorainfall, null);
            handler.countryText = (TextView) view.findViewById(R.id.livequerycounty);
            handler.hourOneText = (TextView) view.findViewById(R.id.livequery_temper);
            handler.hourSixText = (TextView) view.findViewById(R.id.livequery_temper_time);
            handler.hourThreeText = (TextView) view.findViewById(R.id.livequeryhoursix);
            handler.hourTwelve = (TextView) view.findViewById(R.id.livequeryhourtwelve);
            handler.hourTownty_four = (TextView) view.findViewById(R.id.livequerytwenty_four);

            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            switch (clickposition) {
                case 1:
                    handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitleclick));
//                    handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
                    break;
                case 2:
                    handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitleclick));
//                    handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
                    break;
                case 3:
                    handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitleclick));
//                    handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));

                    break;
                case 4:
                    handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitleclick));
//                    handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
                    break;
                case 5:
                    handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitleclick));
//                    handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
                    break;
                default:
                    handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitleclick));
//                    handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
//                    handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.livequery_listtitle));
                    break;
            }
        } else {
            handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourOneText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourSixText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourThreeText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourTwelve.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.hourTownty_four.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
        }

        handler.countryText.setText(rainfalllist.get(position).county);
        handler.hourOneText.setText(rainfalllist.get(position).hour1);
        handler.hourSixText.setText(rainfalllist.get(position).hour3);
        handler.hourThreeText.setText(rainfalllist.get(position).hour6);
        handler.hourTwelve.setText(rainfalllist.get(position).hour12);
        handler.hourTownty_four.setText(rainfalllist.get(position).hour24);

        // handler.hourTownty_four.setBackgroundResource(R.drawable.bg_livequery_item);
        handler.countryText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position != 0) {
                    clickposition = 1;
                    listerner.itemClick(clickposition, position);
                }
            }
        });
        handler.hourOneText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickposition = 1;
                listerner.itemClick(clickposition, position);
            }
        });
        handler.hourSixText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickposition = 2;
                listerner.itemClick(clickposition, position);
            }
        });
        handler.hourThreeText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickposition = 3;
                listerner.itemClick(clickposition, position);
            }
        });
        handler.hourTwelve.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickposition = 4;
                listerner.itemClick(clickposition, position);
            }
        });
        handler.hourTownty_four.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickposition = 5;
                listerner.itemClick(clickposition, position);
            }
        });

        return view;
    }

    private class Handler {
        public TextView countryText;
        public TextView hourOneText;
        public TextView hourSixText;
        public TextView hourThreeText;
        public TextView hourTwelve;
        public TextView hourTownty_four;
    }

    /**
     * @author Z
     *         表头点击事件监听
     */
    public interface RainFallIn {
        /**
         * 表头点击事件
         * 1、第二列表头点击
         * 2、第三列表头点击
         * 3、第四列表头点击
         * 4、第五列表头点击
         * 5、第六列表头点击
         */
        public void itemClick(int clickC, int positon);
    }

    public void setClickposition(int clickposition) {
        this.clickposition = clickposition;
    }
}
