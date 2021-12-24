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
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterCustomPushCity;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList.RadioClick;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.citylist.ActivityCityListCountry;
import com.pcs.ztqtj.view.activity.push.ActivityWarningTypeDialog;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.myview.MyListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author c 气象预警推送
 *
 */
public class ActivityWeatherBasedWarning extends FragmentActivityZtqBase {
	private String[] itemNames = new String[] { "消息栏模式", "弹窗模式" };
	private Boolean[] itemBooleans = new Boolean[] { false, false };
	private List<Map<String, Object>> dialoglistData;
	private CheckBox redpush;
	private CheckBox bluepush;
	private CheckBox orangepush;
	private CheckBox yellowpush;
    private CheckBox cbRemoveWeather, cbNatural, cbAccident, cbPublic, cbSociety, cbEmergency, cbSuspension;
	private PackLocalCity ssta;
	private TextView setCityTextView;
	private Button setmodel;
	private TextView current_model;
	private DialogOneButton dialog;
	private MyListView dialogListview;
	private AdapterSetDialogList dialogadapter;
    private MyListView cityListView;
    private AdapterCustomPushCity customPushAdapter;
    private List<PackLocalCity> customPushCityList = new ArrayList<>();
    private Button btnOrangePush, btnYellowPush, btnBluePush, btnNatural, btnAccident, btnPublic, btnSociety;
    private Map<String, String> pushTagMap = new HashMap<String, String>();
    // 是否已修改预警设置内容
    private boolean isModified = false;
    private boolean isExit = false;
	private MyReceiver receiver = new MyReceiver();

	// private SetPushTagDown setPushTagDown = new SetPushTagDown();

	// private boolean redChecked; // 红色预警//不能关。一定是开着的
	private boolean orangeChecked; // 橙色预警
	private boolean yellowChecked; // 黄色预警
	private boolean blueChecked; // 蓝色预警

	/** 显示模式：0通知栏，1对话框 **/
	private int displayModel = 0;
	private int currdisplayModel = 0;
	private String displayModelTexr = "";
	private String token = "";

	private PackLocalCity city;
	private String cityId = "";
	private String cityName = "";
    private Button btnCustom;
    private Button btnSubmit;
    private DialogTwoButton modifieDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		ssta = (PackLocalCity) bundle.getSerializable("city");
		setTitleText(bundle.getString("title"));
		setContentView(R.layout.activity_weatherwarnpush);
		initView();
		initData();
		initEvent();// 放在初始化数据之后,因为先设置本地缓存的开关会触发监听事件，向服务端发送请求
	}

	private void initView() {
		setCityTextView = (TextView) findViewById(R.id.setcity);
		redpush = (CheckBox) findViewById(R.id.redwarn_choose);
		redpush.setClickable(false);
		bluepush = (CheckBox) findViewById(R.id.bluewarn_choose);
		orangepush = (CheckBox) findViewById(R.id.orangewarn_choose);
		yellowpush = (CheckBox) findViewById(R.id.yellowwarn_choose);
        cbRemoveWeather = (CheckBox) findViewById(R.id.remove_weather_warn_choose);
        cbNatural = (CheckBox) findViewById(R.id.natural_disaster_warn_choose);
        cbAccident = (CheckBox) findViewById(R.id.accident_disaster_warn_choose);
        cbPublic = (CheckBox) findViewById(R.id.public_health_choose);
        cbSociety = (CheckBox) findViewById(R.id.society_choose);
        cbEmergency = (CheckBox) findViewById(R.id.emergency_public_warn_choose);
        cbSuspension = (CheckBox) findViewById(R.id.cb_suspension);
		current_model = (TextView) findViewById(R.id.current_model);
		setmodel = (Button) findViewById(R.id.setmodel);
        cityListView = (MyListView) findViewById(R.id.custom_push_city_list);
        btnCustom = (Button) findViewById(R.id.btn_customize);
		setCityTextView.setText(getResources().getString(R.string.current_default_push_city) + ssta.NAME);
        btnOrangePush = (Button) findViewById(R.id.orangewarn_type);
        btnYellowPush = (Button) findViewById(R.id.yellowwarn_type);
        btnBluePush = (Button) findViewById(R.id.bluewarn_type);
        btnNatural = (Button) findViewById(R.id.natural_disaster_warn_type);
        btnAccident = (Button) findViewById(R.id.accident_disaster_warn_type);
        btnPublic = (Button) findViewById(R.id.public_health_type);
        btnSociety = (Button) findViewById(R.id.society_type);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        initModifieDialog();
	}

	private void initEvent() {

		setmodel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMyDialog();
			}
		});
		orangepush.setOnClickListener(clickListener);
		yellowpush.setOnClickListener(clickListener);
		bluepush.setOnClickListener(clickListener);
        cbRemoveWeather.setOnClickListener(clickListener);
        cbNatural.setOnClickListener(clickListener);
        cbAccident.setOnClickListener(clickListener);
        cbPublic.setOnClickListener(clickListener);
        cbSociety.setOnClickListener(clickListener);
        cbEmergency.setOnClickListener(clickListener);
        cbSuspension.setOnClickListener(clickListener);
        btnCustom.setOnClickListener(clickListener);
        btnOrangePush .setOnClickListener(clickListener);
        btnYellowPush.setOnClickListener(clickListener);
        btnBluePush.setOnClickListener(clickListener);
        btnNatural.setOnClickListener(clickListener);
        btnAccident.setOnClickListener(clickListener);
        btnPublic.setOnClickListener(clickListener);
        btnSociety.setOnClickListener(clickListener);
        btnSubmit.setOnClickListener(clickListener);
        setBackListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifieDialog();
            }
        });
	}

	private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bluewarn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_B, bluepush.isChecked() ? "1" : "0");
                    break;
                case R.id.orangewarn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_O, orangepush.isChecked() ? "1" : "0");
                    break;
                case R.id.yellowwarn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_Y, yellowpush.isChecked() ? "1" : "0");
                    break;
                case R.id.remove_weather_warn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_REMOVE, cbRemoveWeather.isChecked() ? "1" : "0");
                    break;
                case R.id.natural_disaster_warn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_NATURAL, cbNatural.isChecked() ? "1" : "0");
                    break;
                case R.id.accident_disaster_warn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT, cbAccident.isChecked() ? "1" : "0");
                    break;
                case R.id.public_health_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC, cbPublic.isChecked() ? "1" : "0");
                    break;
                case R.id.society_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_SOCIETY, cbSociety.isChecked() ? "1" : "0");
                    break;
                case R.id.emergency_public_warn_choose:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY, cbEmergency.isChecked() ? "1" : "0");
                    break;
                case R.id.cb_suspension:
                    isModified = true;
                    setPushTag(PushTag.getInstance().PUSHTAG_TKL, cbSuspension.isChecked() ? "1" : "0");
                    break;
                case R.id.btn_customize:
                    toCityListActivity();
                    break;
                case R.id.orangewarn_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_O, "1");
                    break;
                case R.id.yellowwarn_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_Y, "1");
                    break;
                case R.id.bluewarn_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_B, "1");
                    break;
                case R.id.natural_disaster_warn_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_NATURAL, "2");
                    break;
                case R.id.accident_disaster_warn_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT, "2");
                    break;
                case R.id.public_health_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC, "2");
                    break;
                case R.id.society_type:
                    gotoTagTypeActivity(PushTag.getInstance().PUSHTAG_QXYJ_SOCIETY, "2");
                    break;
                case R.id.btn_submit:
                    if (!NotificationManagerCompat.from(ActivityWeatherBasedWarning.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityWeatherBasedWarning.this);
                    }else{
                        reqSetPushTag();
                    }
                    break;
            }
        }
	};

	/** 初始化数据 **/
	private void initData() {
//		token = (String) LocalDataHelper.getPushTag(getApplicationContext(), "token", String.class);
//		token = XGPushConfig.getToken(this);
		displayModel = (Integer) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_MODEL, Integer.class);
		initDispalyModel(displayModel);
		cityName = (String) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_CITY_NAME, String.class);
		if (!TextUtils.isEmpty(cityName)) {
			setCityTextView.setText(getResources().getString(R.string.current_default_push_city) + cityName);
		}
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
		PcsDataBrocastReceiver.registerReceiver(ActivityWeatherBasedWarning.this, receiver);
		checkDataServer(token);
		initCheckBox();
        customPushAdapter = new AdapterCustomPushCity(customPushCityList, new ItemClickListener() {
            @Override
            public void itemClick(int position, Object... objects) {
                deleteCity(position);
            }
        });
        cityListView.setAdapter(customPushAdapter);
	}

	private PackQueryPushTagDown serverDataDown;
	/**
	 * 获取服务器的值
	 *
	 * @param token
	 */
	private void checkDataServer(String token) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		serverDataDown = new PackQueryPushTagDown();
		PackQueryPushTagUp up = new PackQueryPushTagUp();
		up.token = token;
		up.pushType="1";
		showProgressDialog();
		PcsDataDownload.addDownload(up);
	}

	private void initCheckBox() {
		orangeChecked = (Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_O, Boolean.class);
		yellowChecked = (Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_Y, Boolean.class);
		blueChecked = (Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_B, Boolean.class);
		orangepush.setChecked(orangeChecked);
		yellowpush.setChecked(yellowChecked);
		bluepush.setChecked(blueChecked);
        cbRemoveWeather.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_REMOVE, Boolean.class));
        cbNatural.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_NATURAL, Boolean.class));
        cbAccident.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT, Boolean.class));
        cbPublic.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC, Boolean.class));
        cbSociety.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_SOCIETY, Boolean.class));
        cbEmergency.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY, Boolean.class));
        cbSuspension.setChecked((Boolean) LocalDataHelper.getPushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_TKL, Boolean.class));
	}

    /**
     * 初始化推送城市列表
     */
	private void initAreaList(List<PackQueryPushTagDown.PushTagAreaBean> areas) {
        customPushCityList.clear();
        // 添加定位城市
//        if(ssta != null) {
//            customPushCityList.add(ssta);
//        }
        PackLocalCity packLocalCity;
        for(PackQueryPushTagDown.PushTagAreaBean bean : areas) {
            // 如果是福建城市则匹配当前定位城市的二级城市ID，如果是外省城市则匹配当前定位城市ID
            if(bean.areaid.equals(ssta.ID)) {
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
        if(customPushCityList.size() >= 9) {
            showToast("城市数量定制已达上限：10个");
        } else {
            Intent it = new Intent(this, ActivityCityListCountry.class);
            // 设置不添加城市至左侧边栏
            it.putExtra("isSingleCityList", true);
            it.putExtra("add_city", false);
            // 设置二级城市列表
            //it.putExtra("isSingleCityList", true);
            //it.putParcelableArrayListExtra("citylist", (ArrayList<? extends Parcelable>) customPushCityList);
            List<PackLocalCity> cityList = new ArrayList<>(customPushCityList);
            cityList.add(ssta);
            it.putExtra("citylist", (Serializable) cityList);
            startActivityForResult(it, MyConfigure.RESULT_CITY_LIST);
        }
    }

    /**
     * 跳转预警标签类型
     * @param tag
     * @param type
     */
    private void gotoTagTypeActivity(String tag, String type) {
        Intent intent = new Intent(this, ActivityWarningTypeDialog.class);
        intent.putExtra("tag", tag);
        intent.putExtra("type", type);
        startActivity(intent);
    }

	private void showMyDialog() {
		if (dialog == null) {
			View view = LayoutInflater.from(ActivityWeatherBasedWarning.this).inflate(R.layout.setfragmetnt_dialog_layout2, null);
			dialogListview = (MyListView) view.findViewById(R.id.listview);
			dialogListview.setAdapter(dialogadapter);
			dialog = new DialogOneButton(ActivityWeatherBasedWarning.this, view, "确定", new DialogListener() {
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
		String string = getResources().getString(R.string.pushmodel)+displayModelTexr;
		current_model.setText(string);
	}

	/**
	 * 点击确定按钮的时候，保存选择的模式
	 *
	 * @param position
	 */
	private void saveDispalyModel(int position) {
		LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_MODEL, position);
		displayModel = position;
		initDispalyModel(position);
	}

    /**
     *  删除单个城市
     * @param position
     */
    private void deleteCity(int position) {
        customPushCityList.remove(position);
        customPushAdapter.notifyDataSetChanged();
        isModified = true;
    }

	/** 列表中的单选按钮被选中的时候更改其他的按钮并更新适配器 */
	private RadioClick radioClick = new RadioClick() {
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

	/**
	 * 设置信鸽推送标签
	 *
	 * @param key
	 * @param value
	 */
	private void setPushTag(String key, String value) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
        pushTagMap.put(key, value);

	}

	private void initModifieDialog() {
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_message, null);
        ((TextView) view.findViewById(R.id.dialogmessage))
                .setText("设置信息还未提交，要提交吗？");
        modifieDialog = new DialogTwoButton(this,
                view, "提交", "放弃", new DialogListener() {
            @Override
            public void click(String str) {
                modifieDialog.dismiss();
                if (str.equals("放弃")) {
                    finish();
                } else if(str.equals("提交")) {
                    if (!NotificationManagerCompat.from(ActivityWeatherBasedWarning.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityWeatherBasedWarning.this);
                    }else{
                        reqSetPushTag();
                        isExit = true;
                    }
                }
            }
        });
        modifieDialog.setTitle("天津气象");
    }

	private void showModifieDialog() {
        if(isModified && modifieDialog != null) {
            modifieDialog.show();
        } else {
            finish();
        }
    }

    /**
     * 检查城市是否已经添加
     * @return
     */
    private boolean checkCityAlreadyAdded(PackLocalCity city) {
        if(ssta.ID.equals(city.ID)) {
            return true;
        }
        for(PackLocalCity bean : customPushCityList) {
            if(bean.ID.equals(city.ID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求设置推送标签
     */
	private void reqSetPushTag() {
        List<String> areaList = new ArrayList<>();
        for(PackLocalCity city : customPushCityList) {
            areaList.add(city.ID);
        }
        // 添加定位城市
//        if(ssta != null) {
//            areaList.add(ssta.PARENT_ID);
//        }
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        SetPushTagUp setPushTagUp = new SetPushTagUp();
        setPushTagUp.token = token;
        pushTagMap.put("warning_city",cityMain.ID);
        pushTagMap.put("yjxx_city",cityMain.ID);
        pushTagMap.put("weatherForecast_city",cityMain.ID);
        setPushTagUp.params = pushTagMap;
        setPushTagUp.areas = areaList;
        setPushTagUp.pushType="1";

        PcsDataDownload.addDownload(setPushTagUp);
    }

	/**
	 * 数据更新广播接收
	 *
	 * @author JiangZy
	 *
	 */
	private class MyReceiver extends PcsDataBrocastReceiver {
		@Override
		public void onReceive(String name, String error) {
			if (PackQueryPushTagUp.NAME.equals(name)) {
				dismissProgressDialog();
                checkNotificationPermission(ActivityWeatherBasedWarning.this);
				serverDataDown = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack(name);
				if (serverDataDown == null) {
					return;
				}
                pushTagMap = serverDataDown.hashMap;
				String orange = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_O);
				String yellow = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_Y);
				String blue = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_B);
                String remove = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_REMOVE);
                String natural = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_NATURAL);
                String accident = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT);
                String publish = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC);
                String society = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_SOCIETY);
                String emergency = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY);
                String suspension = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_TKL);
				LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_O, "1".equals(orange));
				LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_Y, "1".equals(yellow));
				LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_B, "1".equals(blue));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_REMOVE, "1".equals(remove));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_NATURAL, "1".equals(natural));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT, "1".equals(accident));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC, "1".equals(publish));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_SOCIETY, "1".equals(society));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY, "1".equals(emergency));
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_TKL, "1".equals(suspension));
				initCheckBox();
                initAreaList(serverDataDown.areas);

            } else if (name.contains(SetPushTagUp.NAME)) {
                dismissProgressDialog();
                SetPushTagDown setPushTagDown = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(name);
				if (setPushTagDown == null) {
					return;
				}
                isModified = false;
                if(setPushTagDown.result.equals("1")) {
                    showToast("提交成功");
                } else {
                    showToast("提交失败");
                }
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_O, orangepush.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_Y, yellowpush.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_B, bluepush.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_REMOVE, cbRemoveWeather.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_NATURAL, cbNatural.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT, cbAccident.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC, cbPublic.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_SOCIETY, cbSociety.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY, cbEmergency.isChecked());
                LocalDataHelper.savePushTag(ActivityWeatherBasedWarning.this, PushTag.getInstance().PUSHTAG_TKL, cbSuspension.isChecked());
                if(isExit) {
                    finish();
                }
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}

    @Override
    public void onBackPressed() {
        showModifieDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == MyConfigure.RESULT_CITY_LIST) {
            PackLocalCity cityInfo = (PackLocalCity) data.getSerializableExtra("city_info");
            PackLocalCity parent = (PackLocalCity) data.getSerializableExtra("parent");
            // 城市是否已经添加
            if(!checkCityAlreadyAdded(cityInfo)) {
                customPushCityList.add(cityInfo);
                customPushAdapter.notifyDataSetChanged();
                isModified = true;
            } else {
                showToast("该城市已经是推送城市");
            }

        }
    }
}
