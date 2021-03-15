package com.pcs.ztqtj.control.livequery;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pcs.ztqtj.control.adapter.livequery.AdapterDetailSearchResult;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;

import java.util.ArrayList;
import java.util.List;

import static com.pcs.ztqtj.R.id.list_station;

/**
 * 风雨查询自动站控制器
 * Created by tyaathome on 2017/4/26.
 */

public class ControlLiveQueryAutoSite {

    private Activity mActivity;
    private AdapterDetailSearchResult adapter;
    private List<PackLocalStation> stationList = new ArrayList<>();
    private EditText mSiteEditText;
    private ListView listView;
    private AutoSiteItemListener mListener;

    public ControlLiveQueryAutoSite(Activity activity, EditText editText, AutoSiteItemListener listener) {
        this.mActivity = activity;
        mSiteEditText = editText;
        mListener = listener;
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        listView = (ListView) mActivity.findViewById(list_station);
    }

    private void initEvent() {
        mSiteEditText.addTextChangedListener(mTextWatcher);
        listView.setOnItemClickListener(listViewListener);
    }

    private void initData() {
        adapter = new AdapterDetailSearchResult(stationList);
        listView.setAdapter(adapter);
    }

    /**
     * 清除搜索数据
     */
    private void clearData() {
        mSiteEditText.setText("");
        stationList.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * 搜索站点
     * @param str
     */
    private void searchStation(String str){
        stationList.clear();
        List<PackLocalStation> searchList=new ArrayList<>();
        ZtqCityDB.getInstance().searchStation(searchList,str);
        stationList.addAll(searchList);
        adapter.notifyDataSetChanged();
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchStation(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private AdapterView.OnItemClickListener listViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mListener != null) {
                mListener.onItemClick(position, stationList.get(position));
            }
            clearData();
        }
    };

    public interface AutoSiteItemListener {
        void onItemClick(int position, PackLocalStation bean);
    }

}
