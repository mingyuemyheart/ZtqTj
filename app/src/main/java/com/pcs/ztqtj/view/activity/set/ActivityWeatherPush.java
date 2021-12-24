package com.pcs.ztqtj.view.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackPushWeatherConfigDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackPushWeatherConfigUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushQueryTagTypeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushQueryTagTypeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushSetTagTypeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushSetTagTypeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterCustomPushCity;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.citylist.ActivityCityListCountry;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.myview.MyListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Z 天气预报推送
 */
public class ActivityWeatherPush extends FragmentActivityZtqBase {
    private String[] itemNames = new String[]{"消息栏模式", "弹窗模式"};
    private Boolean[] itemBooleans = new Boolean[]{false, false};
    private List<Map<String, Object>> dialoglistData;
    private TextView setCity;
    private TextView textPush;
    private CheckBox morningSwitchbutton;
    private CheckBox bw_switchbutton;
    private CheckBox tsqw_switchbutton;
    private CheckBox tstq_switchbutton;
    private CheckBox kqwr_switchbutton;
    private Button bt_push_confirm;

    private SetPushTagUp setPushTagUp = new SetPushTagUp();
    private Map<String, String> params = new HashMap<String, String>();
    private MyReceiver receiver = new MyReceiver();

    private String token = "";
    private String cityName = "";

    private Button setmodel_city;
    private TextView current_model_city;
    private MyListView dialogListview, cityListView;
    private AdapterSetDialogList dialogadapter;
    private AdapterCustomPushCity customPushAdapter;
    private DialogOneButton dialog;
    private PackPushWeatherConfigUp packPushWeatherConfigUp = new PackPushWeatherConfigUp();
    private PackPushQueryTagTypeUp packPushQueryTagTypeUp = new PackPushQueryTagTypeUp();
    private TextView tv_weather_push, tv_weather_bt, tv_weather_bt_tag, tv_weather_tsqw, tv_weather_tsqw_tag,
            tv_weather_tstq, tv_weather_tstq_tag, tv_weather_kqwr, tv_weather_kqwr_tag;
    private RadioButton radioButton_two, radioButton_four, radioButton_six;
    private PackPushSetTagTypeUp setTagTypeUp = new PackPushSetTagTypeUp();
    public List<String> q_list = new ArrayList<>();
    private List<PackLocalCity> customPushCityList = new ArrayList<>();
    private boolean isModified = false, isFinish = false;
    private PackLocalCity ssta;
    private Button btn_customize_weather;
    private DialogTwoButton modifieDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherpush);
        setTitleText("天气预报推送");
        Bundle bundle = getIntent().getExtras();
        ssta = (PackLocalCity) bundle.getSerializable("city");
        initView();
        initData();
//        ZtqCityDB.getInstance().getCityMain().ID
        initEvent();// 放在初始化数据之后,因为先设置本地缓存的开关会触发监听事件，向服务端发送请求
    }

    private void initView() {
        setCity = (TextView) findViewById(R.id.setcity);
        setCity.setText(getResources().getString(R.string.current_default_push_city) + ssta.NAME);
        current_model_city = (TextView) findViewById(R.id.current_model_city);
        morningSwitchbutton = (CheckBox) findViewById(R.id.morning_switchbutton);
        radioButton_two = (RadioButton) findViewById(R.id.radioButton_two);
        radioButton_four = (RadioButton) findViewById(R.id.radioButton_four);
        radioButton_six = (RadioButton) findViewById(R.id.radioButton_six);
        bw_switchbutton = (CheckBox) findViewById(R.id.bw_switchbutton);
        tsqw_switchbutton = (CheckBox) findViewById(R.id.tsqw_switchbutton);
        tstq_switchbutton = (CheckBox) findViewById(R.id.tstq_switchbutton);
        kqwr_switchbutton = (CheckBox) findViewById(R.id.kqwr_switchbutton);
        setmodel_city = (Button) findViewById(R.id.setmodel_city);
        bt_push_confirm = (Button) findViewById(R.id.bt_push_confirm);
        btn_customize_weather = (Button) findViewById(R.id.btn_customize_weather);
        tv_weather_push = (TextView) findViewById(R.id.tv_weather_push);
        tv_weather_tsqw = (TextView) findViewById(R.id.tv_weather_tsqw);
        tv_weather_tsqw_tag = (TextView) findViewById(R.id.tv_weather_tsqw_tag);
        tv_weather_bt = (TextView) findViewById(R.id.tv_weather_bt);
        tv_weather_bt_tag = (TextView) findViewById(R.id.tv_weather_bt_tag);
        tv_weather_tstq = (TextView) findViewById(R.id.tv_weather_tstq);
        tv_weather_tstq_tag = (TextView) findViewById(R.id.tv_weather_tstq_tag);
        tv_weather_kqwr = (TextView) findViewById(R.id.tv_weather_kqwr);
        tv_weather_kqwr_tag = (TextView) findViewById(R.id.tv_weather_kqwr_tag);
        cityListView = (MyListView) findViewById(R.id.custom_push_weather_city_list);
        initModifieDialog();
    }

    private void initEvent() {
        bt_push_confirm.setOnClickListener(onclickListener);
        btn_customize_weather.setOnClickListener(onclickListener);
        setmodel_city.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog();
            }
        });
        req();
        setBackListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifieDialog();
            }
        });
    }

    private boolean isCityChange = false;
    private boolean isCityRefresh = true;

    private void showModifieDialog() {
        if (isCityChange) {
            modifieDialog.show();
        } else if (isModified() && modifieDialog != null) {
            modifieDialog.show();
        } else {
            ActivityWeatherPush.this.finish();
        }
    }

    private void initModifieDialog() {
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_message, null);
        ((TextView) view.findViewById(R.id.dialogmessage))
                .setText("设置信息还未提交，要提交吗？");
        modifieDialog = new DialogTwoButton(this,
                view, "提交", "放弃", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                modifieDialog.dismiss();
                if (str.equals("放弃")) {
                    finish();
                } else if (str.equals("提交")) {
                    if (!NotificationManagerCompat.from(ActivityWeatherPush.this).areNotificationsEnabled()) {
                        checkNotificationPermission(ActivityWeatherPush.this);
                    } else {
                        isFinish = true;
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MORNING, morningSwitchbutton.isChecked
                                () == true ? "1" : "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_BW, bw_switchbutton.isChecked() == true ? "1" :
                                "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_TSQW, tsqw_switchbutton.isChecked() == true ?
                                "1" : "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_TSTQ, tstq_switchbutton.isChecked() == true ?
                                "1" : "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_KQWR, kqwr_switchbutton.isChecked() == true ?
                                "1" : "0");
                        reqPushTag();
                    }
                }
            }
        });
        modifieDialog.setTitle("天津气象");
    }

    @Override
    public void onBackPressed() {
        showModifieDialog();
    }

    /**
     * 显示模式：0通知栏，1对话框
     **/
    private int displayModel = 0;
    private int currdisplayModel = 0;
    private String displayModelTexr = "";

    private void req() {
        PcsDataDownload.addDownload(packPushWeatherConfigUp);
    }

    private void showMyDialog() {
        if (dialog == null) {
            View view = LayoutInflater.from(ActivityWeatherPush.this).inflate(R.layout.setfragmetnt_dialog_layout2,
                    null);
            dialogListview = (MyListView) view.findViewById(R.id.listview);
            dialogListview.setAdapter(dialogadapter);
            dialog = new DialogOneButton(ActivityWeatherPush.this, view, "确定", new DialogFactory.DialogListener() {
                @Override
                public void click(String str) {
                    dialog.dismiss();
                    if (str.endsWith(getString(R.string.sure))) {
                        saveDispalyModel(currdisplayModel);
                    }
                }
            });
            dialog.setTitle("天津气象提示");
        }
        dialog.show();
    }

    /**
     * 点击确定按钮的时候，保存选择的模式
     *
     * @param position
     */
    private void saveDispalyModel(int position) {
        LocalDataHelper.savePushTag(ActivityWeatherPush.this, PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MODEL,
                position);
        displayModel = position;
        initDispalyModel(position);
    }

    /**
     * 展示当前选择的模式
     *
     * @param postion
     */
    private void initDispalyModel(int postion) {
        switch (postion) {
            case 0:
                displayModelTexr = "消息栏模式";
                break;
            case 1:
                displayModelTexr = "弹窗模式";
                break;
            default:
                displayModelTexr = "消息栏模式";
                break;
        }
        String string = getResources().getString(R.string.pushmodel) + displayModelTexr;
        current_model_city.setText(string);
    }


    /**
     * 初始化数据
     **/
    private void initData() {

//        token = XGPushConfig.getToken(this);
        displayModel = (Integer) LocalDataHelper.getPushTag(ActivityWeatherPush.this, PushTag.getInstance()
                .PUSHTAG_WEATHER_FORECAST_MODEL, Integer.class);
        initDispalyModel(displayModel);
//        cityName = (String) LocalDataHelper.getPushTag(ActivityWeatherPush.this, PushTag.getInstance()
//                .PUSHTAG_WEATHER_FORECAST_CITY_NAME, String.class);
//        if (!TextUtils.isEmpty(cityName)) {
//            setCity.setText(cityName);
//        }

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
        PcsDataBrocastReceiver.registerReceiver(ActivityWeatherPush.this, receiver);
        checkDataServer(token);
        //initCheckBox();

        customPushAdapter = new AdapterCustomPushCity(customPushCityList, new ItemClickListener() {
            @Override
            public void itemClick(int position, Object... objects) {
                deleteCity(position);
            }
        });
        cityListView.setAdapter(customPushAdapter);
    }

    /**
     * 删除单个城市
     *
     * @param position
     */
    private void deleteCity(int position) {
        customPushCityList.remove(position);
        customPushAdapter.notifyDataSetChanged();
        isCityChange = true;
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

    private void initCheckBox(String flag, String flag2, String flag3, String flag4, String flag5) {
        if (!TextUtils.isEmpty(flag) && flag.equals("1")) {
            bw_switchbutton.setChecked(true);
        } else {
            bw_switchbutton.setChecked(false);
        }
        if (!TextUtils.isEmpty(flag2) && flag2.equals("1")) {
            tsqw_switchbutton.setChecked(true);
        } else {
            tsqw_switchbutton.setChecked(false);
        }
        if (!TextUtils.isEmpty(flag3) && flag3.equals("1")) {
            tstq_switchbutton.setChecked(true);
        } else {
            tstq_switchbutton.setChecked(false);
        }
        if (!TextUtils.isEmpty(flag4) && flag4.equals("1")) {
            kqwr_switchbutton.setChecked(true);
        } else {
            kqwr_switchbutton.setChecked(false);
        }
        if (!TextUtils.isEmpty(flag5) && flag5.equals("1")) {
            morningSwitchbutton.setChecked(true);
        } else {
            morningSwitchbutton.setChecked(false);
        }
    }

    /**
     * 初始化推送城市列表
     */
    private void initAreaList(List<PackQueryPushTagDown.PushTagAreaBean> areas) {
        customPushCityList.clear();
//        // 添加定位城市
//        if(ssta != null) {
//            customPushCityList.add(ssta);
//        }
        PackLocalCity packLocalCity;
        for (PackQueryPushTagDown.PushTagAreaBean bean : areas) {
            // 如果是福建城市则匹配当前定位城市的二级城市ID，如果是外省城市则匹配当前定位城市ID
            if (bean.areaid.equals(ssta.ID)) {
                continue;
            }
            packLocalCity = ZtqCityDB.getInstance().getAllCityByID(bean.areaid);
            if (packLocalCity != null) {
                customPushCityList.add(packLocalCity);
            }
        }
        customPushAdapter.notifyDataSetChanged();
    }

    /**
     * 跳转到城市列表
     */
    private void toCityListActivity() {
        if (customPushCityList.size() >= 9) {
            showToast("城市数量定制已达上限：10个");
        } else {
            Intent it = new Intent(this, ActivityCityListCountry.class);
            // 设置不添加城市至左侧边栏
            it.putExtra("isSingleCityList", true);
            it.putExtra("add_city", false);
            // 设置二级城市列表
            //it.putExtra("isThreeLevelCity", false);
            //it.putParcelableArrayListExtra("citylist", (ArrayList<? extends Parcelable>) customPushCityList);
            List<PackLocalCity> cityList = new ArrayList<>(customPushCityList);
            cityList.add(ssta);
            it.putExtra("citylist", (Serializable) cityList);
            startActivityForResult(it, MyConfigure.RESULT_CITY_LIST);
        }
    }

    /**
     * 检查城市是否已经添加
     *
     * @return
     */
    private boolean checkCityAlreadyAdded(PackLocalCity city) {
        if(ssta.ID.equals(city.ID)) {
            return true;
        }
        for (PackLocalCity bean : customPushCityList) {
            if (bean.ID.equals(city.ID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MyConfigure.RESULT_CITY_LIST) {
            PackLocalCity cityInfo = (PackLocalCity) data.getSerializableExtra("city_info");
            PackLocalCity parent = (PackLocalCity) data.getSerializableExtra("parent");
            // 城市是否已经添加
            if (!checkCityAlreadyAdded(cityInfo)) {
                customPushCityList.add(cityInfo);
                customPushAdapter.notifyDataSetChanged();
                isCityChange = true;
            } else {
                showToast("该城市已经是推送城市");
            }

        }
    }

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
        up.pushType = "3";
        showProgressDialog();
        PcsDataDownload.addDownload(up);

//        PackQueryPushTagUp up = new PackQueryPushTagUp();
//        up.token = token;
//        showProgressDialog();
//        PcsDataDownload.addDownload(up);
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
        if (setPushTagUp == null) {
            setPushTagUp = new SetPushTagUp();
        }
        //params.clear();
        params.put(key, value);
    }

    private void reqPushTag() {

        List<String> areaList = new ArrayList<>();
        for (PackLocalCity city : customPushCityList) {
            areaList.add(city.ID);
        }
        // 添加定位城市
//        if (ssta != null) {
//            areaList.add(ssta.PARENT_ID);
//        }
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        setPushTagUp.token = token;
        params.put("warning_city",cityMain.ID);
        params.put("yjxx_city",cityMain.ID);
        params.put("weatherForecast_city",cityMain.ID);
        setPushTagUp.params = params;
        setPushTagUp.pushType = "3";
        setPushTagUp.areas = areaList;
//        setPushTagUp.key = key;
        PcsDataDownload.addDownload(setPushTagUp);
//        setTagTypeUp.tag=list.get()
        if (morningSwitchbutton.isChecked() && info_list.size() >= 3) {
            q_list.clear();
            if (radioButton_two.isChecked()) {
                q_list.add(info_list.get(0).id);
            } else if (radioButton_four.isChecked()) {
                q_list.add(info_list.get(1).id);
            } else if (radioButton_six.isChecked()) {
                q_list.add(info_list.get(2).id);
            }
            setTagTypeUp.q_list = q_list;
            setTagTypeUp.type = "4";
            setTagTypeUp.tag = list.get(0).tag;
            PcsDataDownload.addDownload(setTagTypeUp);
        }
    }


    public List<PackPushWeatherConfigDown.PushWeatherBean> list = new ArrayList<>();
    public List<PackPushQueryTagTypeDown.PushTagTypeBean> info_list = new ArrayList<>();

    private boolean isModified() {
        String morning = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MORNING);
        String weather_bt = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_BW);
        String weather_tsqw = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_TSQW);
        String weather_tstq = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_TSTQ);
        String weather_kqwr = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_KQWR);

        if (!morning.equals(morningSwitchbutton.isChecked
                () == true ? "1" : "0")) {
            isModified = true;
        }

        if (!weather_bt.equals(bw_switchbutton.isChecked() == true ? "1" :
                "0")) {
            isModified = true;
        }

        if (!weather_tsqw.equals(tsqw_switchbutton.isChecked() == true ?
                "1" : "0")) {
            isModified = true;
        }

        if (!weather_tstq.equals(tstq_switchbutton.isChecked() == true ?
                "1" : "0")) {
            isModified = true;
        }

        if (!weather_kqwr.equals(kqwr_switchbutton.isChecked() == true ?
                "1" : "0")) {
            isModified = true;
        }

        if (radioButton_two.isChecked() && info_list.size() >= 1) {
            if (!info_list.get(0).ischeck.equals("1")) {
                isModified = true;
            }
        }

        if (radioButton_four.isChecked() && info_list.size() >= 2) {
            if (!info_list.get(1).ischeck.equals("1")) {
                isModified = true;
            }
        }

        if (radioButton_six.isChecked() && info_list.size() >= 3) {
            if (!info_list.get(2).ischeck.equals("1")) {
                isModified = true;
            }
        }
        return isModified;
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (PackQueryPushTagUp.NAME.equals(name)) {
                dismissProgressDialog();
                serverDataDown = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (serverDataDown == null) {
                    return;
                } else {

                    String morning = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MORNING);
                    String weather_bt = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_BW);
                    String weather_tsqw = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_TSQW);
                    String weather_tstq = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_TSTQ);
                    String weather_kqwr = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_WEATHER_KQWR);
                    initCheckBox(weather_bt, weather_tsqw, weather_tstq, weather_kqwr, morning);
                    if (isCityRefresh) {
                        initAreaList(serverDataDown.areas);
                        isCityRefresh=false;
                    }
                }
            } else if (!TextUtils.isEmpty(name) && setPushTagUp.getName().equals(name)) {
                SetPushTagDown setPushTagDown = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (setPushTagDown == null) {
                    return;
                }
                if ("1".equals(setPushTagDown.result) && TextUtils.isEmpty(error)) {
                    params.clear();
                    checkDataServer(token);
                    isCityChange = false;
                    PcsDataDownload.addDownload(packPushWeatherConfigUp);
                    Toast.makeText(ActivityWeatherPush.this, "提交成功", Toast.LENGTH_SHORT).show();
                    if (isFinish) {
                        finish();
                    }
                }
            } else if (packPushWeatherConfigUp.NAME.equals(name)) {
                PackPushWeatherConfigDown down = (PackPushWeatherConfigDown) PcsDataManager.getInstance().getNetPack
                        (name);
                dismissProgressDialog();
                checkNotificationPermission(ActivityWeatherPush.this);
                list.clear();
                if (down == null) {
                    return;
                }
                list.addAll(down.list);
                setData();
                showProgressDialog();
                packPushQueryTagTypeUp.tag = list.get(0).tag;
                packPushQueryTagTypeUp.type = "4";
                PcsDataDownload.addDownload(packPushQueryTagTypeUp);
            } else if (packPushQueryTagTypeUp.getName().equals(name)) {
                PackPushQueryTagTypeDown down = (PackPushQueryTagTypeDown) PcsDataManager.getInstance().getNetPack
                        (name);
                dismissProgressDialog();
                info_list.clear();
                if (down == null) {
                    return;
                }
                info_list.addAll(down.info_list);
                if (info_list.size() >= 3) {
                    if (info_list.get(0).ischeck.equals("0") && info_list.get(1).ischeck.equals("0") && info_list.get(2)
                            .ischeck.equals("0")) {
                        morningSwitchbutton.setChecked(false);
                    } else if (info_list.get(0).ischeck.equals("1")) {
                        radioButton_two.setChecked(true);
                    } else if (info_list.get(1).ischeck.equals("1")) {
                        radioButton_four.setChecked(true);
                    } else {
                        radioButton_six.setChecked(true);
                    }
                }
            } else if (setTagTypeUp.getName().equals(name)) {
                PackPushSetTagTypeDown down = (PackPushSetTagTypeDown) PcsDataManager.getInstance().getNetPack(name);
                if (down == null) {
                    return;
                }

            }
        }


    }

    private void setData() {
        tv_weather_push.setText(list.get(0).memo);
        tv_weather_bt.setText(list.get(1).memo);
        tv_weather_bt_tag.setText(list.get(1).s_memo);
        tv_weather_tsqw.setText(list.get(2).memo);
        tv_weather_tsqw_tag.setText(list.get(2).s_memo);
        tv_weather_tstq.setText(list.get(3).memo);
        tv_weather_tstq_tag.setText(list.get(3).s_memo);
        tv_weather_kqwr.setText(list.get(4).memo);
        tv_weather_kqwr_tag.setText(list.get(4).s_memo);
    }

    /**
     * checkbox监听
     **/
    private OnClickListener onclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_push_confirm:
                    if (!NotificationManagerCompat.from(ActivityWeatherPush.this).areNotificationsEnabled()) {
                        checkNotificationPermission(ActivityWeatherPush.this);
                    } else {
                        isFinish = false;
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MORNING, morningSwitchbutton.isChecked
                                () == true ? "1" : "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_BW, bw_switchbutton.isChecked() == true ? "1" :
                                "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_TSQW, tsqw_switchbutton.isChecked() == true ?
                                "1" : "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_TSTQ, tstq_switchbutton.isChecked() == true ?
                                "1" : "0");
                        setPushTag(PushTag.getInstance().PUSHTAG_WEATHER_KQWR, kqwr_switchbutton.isChecked() == true ?
                                "1" : "0");
                        reqPushTag();
                    }

//                    Map<String, String> dataMap = new HashMap<>();
//                    dataMap.put("TITLE", "TITLE");
//                    dataMap.put("CONTENT", "CONTENT");
//                    MNotification mn = new MNotification(ActivityWeatherPush.this, dataMap);
//                    mn.setNotification_ID_BASE(1);
//                    mn.showWeatherPreNotify();// 预警推送自定义通知栏
                    break;
                case R.id.btn_customize_weather:
                    toCityListActivity();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

}
