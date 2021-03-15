package com.pcs.ztqtj.view.activity.set;

import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;
import com.tencent.android.tpush.XGPushConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Z 气象服务推送
 */
public class ActivityServicePushSet extends FragmentActivityZtqBase implements OnClickListener{
    private String[] itemNames = new String[]{"消息栏模式", "弹窗模式"};
    private Boolean[] itemBooleans = new Boolean[]{false, false};
    private List<Map<String, Object>> dialoglistData;
    private boolean sign_jcbg;
    private boolean sign_hyqx;
    private boolean sign_ljyb;

    private Button setmodel;
    private CheckBox cb_jcbg;
    private CheckBox cb_hyqx;
    private CheckBox cb_ljyb;
    private MyReceiver receiver = new MyReceiver();
    private Map<String, String> params = new HashMap<String, String>();

    private String token = "";
    private DialogOneButton dialog;
    private MyListView dialogListview;
    private AdapterSetDialogList dialogadapter;
    private TextView current_model;
    /**
     * 显示模式：0对话框，1通知栏
     **/
    private int displayModel = 0;
    private int currdisplayModel = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        setContentView(R.layout.activity_server_push_set);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        current_model= (TextView) findViewById(R.id.current_model);
        cb_jcbg = (CheckBox) findViewById(R.id.cb_jcbg);
        cb_hyqx = (CheckBox) findViewById(R.id.cb_hyqx);
        cb_ljyb = (CheckBox) findViewById(R.id.cb_ljyb);
        setmodel = (Button) findViewById(R.id.setmodel);
    }

    private void initEvent() {
        setmodel.setOnClickListener(this);
        cb_jcbg.setOnClickListener(clickListener);
        cb_hyqx.setOnClickListener(clickListener);
        cb_ljyb.setOnClickListener(clickListener);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cb_jcbg:
                    if (!NotificationManagerCompat.from(ActivityServicePushSet.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityServicePushSet.this);
                    }else{
                        setPushTag(PushTag.getInstance().PUSHTAG_QXFW_JC, cb_jcbg.isChecked() == true ? "1" : "0");
                    }
                    break;
                case R.id.cb_hyqx:
                    if (!NotificationManagerCompat.from(ActivityServicePushSet.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityServicePushSet.this);
                    }else{
                        setPushTag(PushTag.getInstance().PUSHTAG_QXFW_HY, cb_hyqx.isChecked() == true ? "1" : "0");
                    }
                    break;
                case R.id.cb_ljyb:
                    if (!NotificationManagerCompat.from(ActivityServicePushSet.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityServicePushSet.this);
                    }else{
                        setPushTag(PushTag.getInstance().PUSHTAG_QXFW_LJ, cb_ljyb.isChecked() == true ? "1" : "0");
                    }
                    break;
            }
        }
    };

    /**
     * 初始化数据
     **/
    private void initData() {
        // 设备token
//		token = (String) LocalDataHelper.getPushTag(getApplicationContext(), "token", String.class);
        token = XGPushConfig.getToken(this);

        displayModel = (Integer) LocalDataHelper.getPushTag(ActivityServicePushSet.this, PushTag.getInstance()
                .PUSHTAG_TIPS_MODEL_SERVICE, Integer.class);
        initDispalyModel(displayModel);

        dialoglistData = new ArrayList<Map<String, Object>>();

        switch (displayModel) {
            case 0:
                itemBooleans[0] = true;
                itemBooleans[1] = false;
                break;
            case 1:
                itemBooleans[0] = false;
                itemBooleans[1] = true;
                break;
        }
        for (int i = 0; i < itemNames.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("c", itemNames[i]);
            map.put("r", itemBooleans[i]);
            dialoglistData.add(map);
        }

        dialogadapter = new AdapterSetDialogList(this, dialoglistData, radioClick);

        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(ActivityServicePushSet.this, receiver);
        checkDataServer(token);
        initCheckBox();
    }

    /**
     * 列表中的单选按钮被选中的时候更改其他的按钮并更新适配器
     */
    private AdapterSetDialogList.RadioClick radioClick = new AdapterSetDialogList.RadioClick() {
        @Override
        public void positionclick(int position) {
            for (int i = 0; i < dialoglistData.size(); i++) {
                if (i == position) {
                    dialoglistData.get(i).put("r", true);
                } else {
                    dialoglistData.get(i).put("r", false);
                }
            }
            if (position == 0) {
                currdisplayModel = 0;
                // checkStr = "当前模式：消息栏模式";
            } else {
                currdisplayModel = 1;
                // checkStr = "当前模式：弹窗模式";
            }
            dialogadapter.notifyDataSetChanged();
        }
    };

    private PackQueryPushTagDown serverDataDown;

    /**
     * 获取服务器的值
     *
     * @param token
     */
    private void checkDataServer(String token) {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        serverDataDown = new PackQueryPushTagDown();
        PackQueryPushTagUp up = new PackQueryPushTagUp();
        up.token = token;
        up.pushType = "5";
        showProgressDialog();
        PcsDataDownload.addDownload(up);
    }

    private void initCheckBox() {

        sign_jcbg = (Boolean) LocalDataHelper.getPushTag(ActivityServicePushSet.this, PushTag.getInstance()
                .PUSHTAG_QXFW_JC, Boolean.class);
        sign_hyqx = (Boolean) LocalDataHelper.getPushTag(ActivityServicePushSet.this, PushTag.getInstance()
                .PUSHTAG_QXFW_HY, Boolean.class);
        sign_ljyb = (Boolean) LocalDataHelper.getPushTag(ActivityServicePushSet.this, PushTag.getInstance()
                .PUSHTAG_QXFW_LJ, Boolean.class);

        cb_jcbg.setChecked(sign_jcbg);
        cb_hyqx.setChecked(sign_hyqx);
        cb_ljyb.setChecked(sign_ljyb);
    }

    /**
     * 设置信鸽推送标签
     *
     * @param key
     * @param value
     */
    private void setPushTag(String key, String value) {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        setPushTagUp = new SetPushTagUp();
        params.clear();
        params.put(key, value);
        params.put("warning_city",cityMain.ID);
        params.put("yjxx_city",cityMain.ID);
        params.put("weatherForecast_city",cityMain.ID);
        setPushTagUp.token = token;
        setPushTagUp.params = params;
        setPushTagUp.key = key;
        PcsDataDownload.addDownload(setPushTagUp);
    }

    private SetPushTagUp setPushTagUp;

    private void showMyDialog() {

        if (dialog == null) {
            View view = LayoutInflater.from(ActivityServicePushSet.this).inflate(R.layout.setfragmetnt_dialog_layout2, null);
            dialogListview = (MyListView) view.findViewById(R.id.listview);
            dialogListview.setAdapter(dialogadapter);

            dialog = new DialogOneButton(ActivityServicePushSet.this, view, "确定", new DialogFactory.DialogListener() {
                @Override
                public void click(String str) {
                    dialog.dismiss();
                    if (str.endsWith(getString(R.string.sure))) {
                        if (currdisplayModel != displayModel) {
                            saveDispalyModel(currdisplayModel);
                        }
                    }
                }
            });
            dialog.setTitle("天津气象提示");
            // dialog.showCloseBtn();
        }

        dialog.show();
    }

    /**
     * 点击确定按钮的时候，保存选择的模式
     *
     * @param position
     */
    private void saveDispalyModel(int position) {
        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_TIPS_MODEL_SERVICE, position);
        displayModel = position;
        initDispalyModel(position);
    }

    private String displayModelTexr = "";

    /**
     * 展示当前选择的模式
     *
     * @param postion
     */
    private void initDispalyModel(int postion) {
        switch (postion) {
            case 0:
                displayModelTexr = "当前模式：消息栏模式";
                break;
            case 1:
                displayModelTexr = "当前模式：弹窗模式";
                break;
            default:
                displayModelTexr = "当前模式：消息栏模式";
                break;
        }
        current_model.setText(displayModelTexr);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setmodel:
                showMyDialog();
                break;
        }
    }

    /**
     * 数据更新广播接收
     *
     * @author JiangZy
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {

            if (PackQueryPushTagUp.NAME.equals(name)) {
//				查询服务器的值
                dismissProgressDialog();
                checkNotificationPermission(ActivityServicePushSet.this);
                serverDataDown = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (serverDataDown == null) {
                    return;
                } else {

                    String jc = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXFW_JC);
                    String hy = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXFW_HY);
                    String lj = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXFW_LJ);

                    LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_JC,
                            "1".equals(jc));
                    LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_HY,
                            "1".equals(hy));
                    LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_LJ,
                            "1".equals(lj));
                    initCheckBox();
                }
            } else if (setPushTagUp != null && setPushTagUp.getName().equals(name)) {
                dismissProgressDialog();
//				值改变返回
                SetPushTagDown setPushTagDown = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (setPushTagDown == null) {
                    return;
                }

                if ("1".equals(setPushTagDown.result) && TextUtils.isEmpty(error)) {
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_QXFW_JC) != -1) {// 节日
                        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance()
                                .PUSHTAG_QXFW_JC, cb_jcbg.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_QXFW_HY) != -1) {// 节气
                        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance()
                                .PUSHTAG_QXFW_HY, cb_hyqx.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_QXFW_LJ) != -1) {// 公告
                        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_LJ, cb_ljyb.isChecked());
                    }
                } else {
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_QXFW_JC) != -1) {// 节日
                        cb_jcbg.setChecked(!cb_jcbg.isChecked());
                        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_JC, cb_jcbg.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_QXFW_HY) != -1) {// 节气
                        cb_hyqx.setChecked(!cb_hyqx.isChecked());
                        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_HY, cb_hyqx.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_QXFW_LJ) != -1) {// 公告
                        cb_ljyb.setChecked(!cb_ljyb.isChecked());
                        LocalDataHelper.savePushTag(ActivityServicePushSet.this, PushTag.getInstance().PUSHTAG_QXFW_LJ, cb_ljyb.isChecked());
                    }
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
