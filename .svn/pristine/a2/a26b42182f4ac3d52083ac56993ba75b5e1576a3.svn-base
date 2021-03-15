package com.pcs.ztqtj.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.test_location.AdapterTestLocation;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTestLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * JiangZy on 2016/11/30.
 */

public class ControlTestLocation {

    private List<PackLocalTestLocation> mList = new ArrayList<PackLocalTestLocation>();

    private Dialog mDialog;
    private AlertDialog.Builder mDialogBuilder;

    public ControlTestLocation(Context context, View button) {
        initData();
        initDialog(context);
        button.setOnClickListener(mOnClick);
    }

    private void initData() {
        PackLocalTestLocation pack1 = new PackLocalTestLocation();
        pack1.show_text = "真实定位";
        mList.add(pack1);

        PackLocalTestLocation pack2 = new PackLocalTestLocation();
        pack2.show_text = "模拟 天津";
        pack2.longitude = 117.19937d;
        pack2.latitude = 39.08510d;
        pack2.province = "福建省";
        pack2.city = "福州";
        pack2.district = "晋安区";
        mList.add(pack2);

        PackLocalTestLocation pack3 = new PackLocalTestLocation();
        pack3.show_text = "模拟 福州-台江区";
        pack3.longitude = 119.319898d;
        pack3.latitude = 26.060845d;
        pack3.province = "福建省";
        pack3.city = "福州";
        pack3.district = "台江区";
        mList.add(pack3);


        PackLocalTestLocation pack4 = new PackLocalTestLocation();
        pack4.show_text = "模拟 北京-东城区";
        pack4.longitude = 116.420453d;
        pack4.latitude = 39.935803d;
        pack4.province = "北京";
        pack4.city = "北京";
        pack4.district = "东城区";
        mList.add(pack4);


        PackLocalTestLocation pack5 = new PackLocalTestLocation();
        pack5.show_text = "华大街道";
        pack5.longitude = 119.2926400000;
        pack5.latitude =  26.0975300000;
        pack5.province = "福建省";
        pack5.city = "福建市";
        pack5.district = "鼓楼区";
        mList.add(pack5);

        PackLocalTestLocation pack6 = new PackLocalTestLocation();
        pack6.show_text = "华大街道";
        pack6.longitude =  118.6318200000;
        pack6.latitude = 24.9384200000;
        pack6.province = "福建省";
        pack6.city = "泉州市";
        pack6.district = "丰泽区";
        mList.add(pack6);

        PackLocalTestLocation pack7 = new PackLocalTestLocation();
        pack7.show_text = "安泰街道";
        pack7.longitude =  119.3000000000;
        pack7.latitude = 26.0800000000;
        pack7.province = "福建省";
        pack7.city = "福州市";
        pack7.district = "鼓楼区";
        mList.add(pack7);



        PackLocalTestLocation pack8 = new PackLocalTestLocation();
        pack8.show_text = "打铁街";
        pack8.longitude = 117.5285700000;
        pack8.latitude = 23.7392100000;
        pack8.province = "福建省";
        pack8.city = "漳州市";
        pack8.district = "东山县";
        mList.add(pack8);

        PackLocalTestLocation pack9 = new PackLocalTestLocation();
        pack9.show_text = "新店镇";
        pack9.longitude = 119.3118600000;
        pack9.latitude = 26.1274100000;
        pack9.province = "福建省";
        pack9.city = "福州市";
        pack9.district = "晋安区";
        mList.add(pack9);

        PackLocalTestLocation pack10 = new PackLocalTestLocation();
        pack10.show_text = "建新中路";
        pack10.longitude = 119.2569040000;
        pack10.latitude = 26.0419390000;
        pack10.province = "福建省";
        pack10.city = "福州市";
        pack10.district = "仓山区";
        mList.add(pack10);

        PackLocalTestLocation pack11= new PackLocalTestLocation();
        pack11.show_text = "闽侯县";
        pack11.longitude = 119.2502900000;
        pack11.latitude = 26.1028000000;
        pack11.province = "福建";
        pack11.city = "福州";
        pack11.district = "闽侯";
        mList.add(pack11);

        PackLocalTestLocation pack12= new PackLocalTestLocation();
        pack12.show_text = "仓山区";
        pack12.longitude = 119.2629300000;
        pack12.latitude = 26.0477700000;
        pack12.province = "福建省";
        pack12.city = "福州";
        pack12.district = "仓山区";
        mList.add(pack12);

        PackLocalTestLocation pack13= new PackLocalTestLocation();
        pack13.show_text = "彬城镇";
        pack13.longitude = 117.1758900000;
        pack13.latitude = 26.8968600000;
        pack13.province = "福建省";
        pack13.city = "三明";
        pack13.district = "泰宁";
        mList.add(pack13);

        PackLocalTestLocation pack14= new PackLocalTestLocation();
        pack14.show_text = "岚城乡";
        pack14.longitude = 119.7801000000;
        pack14.latitude = 25.4913700000;
        pack14.province = "福建省";
        pack14.city = "平潭";
        pack14.district = "平潭";
        mList.add(pack14);

        PackLocalTestLocation pack15= new PackLocalTestLocation();
        pack15.show_text = "庐山南大道";
        pack15.longitude = 115.868087;
        pack15.latitude = 28.699103;
        pack15.province = "江西省";
        pack15.city = "南昌市";
        pack15.district = "南昌青山湖区";
        mList.add(pack15);

        PackLocalTestLocation pack16= new PackLocalTestLocation();
        pack16.show_text = "吉安市人民政府";
        pack16.longitude = 114.966826;
        pack16.latitude = 27.091258;
        pack16.province = "江西省";
        pack16.city = "吉安市";
        pack16.district = "吉安吉州区";
        mList.add(pack16);

        PackLocalTestLocation pack17 = new PackLocalTestLocation();
        pack17.show_text = "吉诺车屋";
        pack17.longitude = 119.67545;
        pack17.latitude = 25.926746;
        pack17.province = "";
        pack17.city = "";
        pack17.district = "";
        mList.add(pack17);

        PackLocalTestLocation pack18 = new PackLocalTestLocation();
        pack18.show_text = "长乐区";
        pack18.longitude = 119.523266;
        pack18.latitude = 25.962888;
        pack18.province = "";
        pack18.city = "";
        pack18.district = "";
        mList.add(pack18);
    }

    private void initDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_test_location, null);
        mDialogBuilder = new AlertDialog.Builder(context);
        mDialogBuilder.setCancelable(true);
        mDialogBuilder.setView(view);

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(mOnItemClick);

        AdapterTestLocation adapter = new AdapterTestLocation(context);
        adapter.setData(mList);
        listView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                //删除数据
                PcsDataManager.getInstance().removeLocalData(PackLocalTestLocation.KEY);
            } else {
                //设置数据
                PackLocalTestLocation pack = mList.get(position);
                PcsDataManager.getInstance().saveLocalData(PackLocalTestLocation.KEY, pack);
            }
            //刷新
            ZtqLocationTool.getInstance().refreshSearch();
            ZtqLocationTool.getInstance().refreshLocationCity(false);
            mDialog.dismiss();
        }
    };

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDialog == null) {
                mDialog = mDialogBuilder.show();
            } else {
                mDialog.show();
            }
        }
    };
}
