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

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList.RadioClick;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.myview.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Z 温馨提示推送
 */
public class ActivityReminder extends FragmentActivityZtqBase implements OnClickListener {
    private String[] itemNames = new String[]{"消息栏模式", "弹窗模式"};
    private Boolean[] itemBooleans = new Boolean[]{false, false};
    private List<Map<String, Object>> dialoglistData;

    private boolean holidayChecked; // 节日推送
    private boolean jieqiChecked; // 节气推送
    private boolean noticeChecked; // 公告推送

    private Button setmodel;
    private TextView current_model;
    private CheckBox holidaypush;
    private CheckBox jieqipush;
    private CheckBox noticepush;

    private MyReceiver receiver = new MyReceiver();

    private Map<String, String> params = new HashMap<String, String>();

    /**
     * 显示模式：0对话框，1通知栏
     **/
    private int displayModel = 0;
    private int currdisplayModel = 0;
    private String displayModelTexr = "";
    private String token = "";
    private DialogOneButton dialog;
    private MyListView dialogListview;
    private AdapterSetDialogList dialogadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        // PackLocalCity ssta = (PackLocalCity) bundle.getSerializable("city");
        setTitleText(bundle.getString("title"));
        setContentView(R.layout.activity_reminder);
        initView();
        initData();
        initEvent();// 放在初始化数据之后,因为先设置本地缓存的开关会触发监听事件，向服务端发送请求
    }

    private void initView() {
        current_model = (TextView) findViewById(R.id.current_model);
        setmodel = (Button) findViewById(R.id.setmodel);
        holidaypush = (CheckBox) findViewById(R.id.cb_jcbg);
        jieqipush = (CheckBox) findViewById(R.id.cb_hyqx);
        noticepush = (CheckBox) findViewById(R.id.cb_ljyb);
    }

    private void initEvent() {
        setmodel.setOnClickListener(this);
        holidaypush.setOnClickListener(clickListener);
        jieqipush.setOnClickListener(clickListener);
        noticepush.setOnClickListener(clickListener);
//		holidaypush.setOnCheckedChangeListener(myOnCheckedChangeListener);
//		jieqipush.setOnCheckedChangeListener(myOnCheckedChangeListener);
//		noticepush.setOnCheckedChangeListener(myOnCheckedChangeListener);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cb_jcbg:
                    if (!NotificationManagerCompat.from(ActivityReminder.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityReminder.this);
                    }else{
                        setPushTag(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY, holidaypush.isChecked() == true ? "1" : "0");
                    }
                    break;
                case R.id.cb_hyqx:
                    if (!NotificationManagerCompat.from(ActivityReminder.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityReminder.this);
                    }else{
                        setPushTag(PushTag.getInstance().PUSHTAG_TIPS_JIEQI, jieqipush.isChecked() == true ? "1" : "0");
                    }
                    break;
                case R.id.cb_ljyb:
                    if (!NotificationManagerCompat.from(ActivityReminder.this).areNotificationsEnabled()){
                        checkNotificationPermission(ActivityReminder.this);
                    }else{
                        setPushTag(PushTag.getInstance().PUSHTAG_TIPS_NOTICE, noticepush.isChecked() == true ? "1" : "0");
                    }
                    break;
            }
        }
    };


    /**
     * 初始化数据
     **/
    private void initData() {

        dialoglistData = new ArrayList<Map<String, Object>>();
        // 设备token
//        token = XGPushConfig.getToken(this);
//		token = (String) LocalDataHelper.getPushTag(getApplicationContext(), "token", String.class);
        displayModel = (Integer) LocalDataHelper.getPushTag(ActivityReminder.this, PushTag.getInstance()
                .PUSHTAG_TIPS_MODEL, Integer.class);
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
        PcsDataBrocastReceiver.registerReceiver(ActivityReminder.this, receiver);
        checkDataServer(token);
        initCheckBox();
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
        up.pushType = "4";
        showProgressDialog();
        PcsDataDownload.addDownload(up);
    }

    private void initCheckBox() {
        holidayChecked = (Boolean) LocalDataHelper.getPushTag(ActivityReminder.this, PushTag.getInstance()
                .PUSHTAG_TIPS_HOLIDAY, Boolean.class);
        jieqiChecked = (Boolean) LocalDataHelper.getPushTag(ActivityReminder.this, PushTag.getInstance()
                .PUSHTAG_TIPS_JIEQI, Boolean.class);
        noticeChecked = (Boolean) LocalDataHelper.getPushTag(ActivityReminder.this, PushTag.getInstance()
                .PUSHTAG_TIPS_NOTICE, Boolean.class);
        holidaypush.setChecked(holidayChecked);
        jieqipush.setChecked(jieqiChecked);
        noticepush.setChecked(noticeChecked);
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
        SetPushTagUp setPushTagUp = new SetPushTagUp();
        params.clear();
        params.put(key, value);
        params.put("warning_city",cityMain.ID);
        params.put("yjxx_city",cityMain.ID);
        params.put("weatherForecast_city",cityMain.ID);
        setPushTagUp.token = token;
        setPushTagUp.params = params;
        setPushTagUp.key = key;
        setPushTagUp.pushType = "4";

        PcsDataDownload.addDownload(setPushTagUp);
    }

    /**
     * 数据更新广播接收
     *
     * @author JiangZy
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            SetPushTagDown setPushTagDown = new SetPushTagDown();
            if (PackQueryPushTagUp.NAME.equals(name)) {
                dismissProgressDialog();
                checkNotificationPermission(ActivityReminder.this);
                serverDataDown = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (serverDataDown == null) {
                    return;
                } else {

                    String holiday = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY);
                    String jieqi = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_TIPS_JIEQI);
                    String notice = serverDataDown.hashMap.get(PushTag.getInstance().PUSHTAG_TIPS_NOTICE);
                    LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY,
                            "1".equals(holiday));
                    LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_JIEQI, "1"
                            .equals(jieqi));
                    LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_NOTICE, "1"
                            .equals(notice));
                    initCheckBox();
                }
            } else if (!TextUtils.isEmpty(name) && name.contains(SetPushTagUp.NAME)) {
                dismissProgressDialog();
                setPushTagDown = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (setPushTagDown == null) {
                    return;
                }

                if ("1".equals(setPushTagDown.result) && TextUtils.isEmpty(error)) {
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY) != -1) {// 节日
                        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance()
                                .PUSHTAG_TIPS_HOLIDAY, holidaypush.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_TIPS_JIEQI) != -1) {// 节气
                        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_JIEQI,
                                jieqipush.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_TIPS_NOTICE) != -1) {// 公告
                        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_NOTICE,
                                noticepush.isChecked());
                    }
                } else {
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY) != -1) {// 节日
                        holidaypush.setChecked(!holidaypush.isChecked());
                        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance()
                                .PUSHTAG_TIPS_HOLIDAY, holidaypush.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_TIPS_JIEQI) != -1) {// 节气
                        jieqipush.setChecked(!jieqipush.isChecked());
                        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_JIEQI,
                                jieqipush.isChecked());
                    }
                    if (name.indexOf(PushTag.getInstance().PUSHTAG_TIPS_NOTICE) != -1) {// 公告
                        noticepush.setChecked(!noticepush.isChecked());
                        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_NOTICE,
                                noticepush.isChecked());
                    }
                }
            }
        }
    }

//	private OnCheckedChangeListener myOnCheckedChangeListener = new OnCheckedChangeListener() {
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//			switch (buttonView.getId()) {
//			case R.id.holiday_choose:
//				holidaypush.setChecked(isChecked);
//				setPushTag(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY, isChecked == true ? "1" : "0");
//				break;
//			case R.id.jieqi_choose:
//				jieqipush.setChecked(isChecked);
//				setPushTag(PushTag.getInstance().PUSHTAG_TIPS_JIEQI, isChecked == true ? "1" : "0");
//				break;
//			case R.id.notice_choose:
//				noticepush.setChecked(isChecked);
//				setPushTag(PushTag.getInstance().PUSHTAG_TIPS_NOTICE, isChecked == true ? "1" : "0");
//				break;
//			default:
//				// Intent intent = new Intent(ActivityReminder.this,
//				// ActivityHolidayDialog.class);
//				// intent.putExtra("TITLE", "节日");// 标题
//				// intent.putExtra("TYPE", "节日");// 推送类型
//				// intent.putExtra("HOLIDAY_NAME", "元旦");// 节日或者节气的名称
//				// intent.putExtra("ICO", "yd");
//				// intent.putExtra("CONTENT",
//				// "元旦，中国节日，即世界多数国家通称的“新年”，是公历新一年开始的第一天。“元旦”一词最早出现于《晋书》。中国古代曾以腊月、十月等的月首为元旦，汉武帝始为农历1月1日，并延用。中华民国始为公历1月1
// 日，1949年中华人民共和国成立时得以明确，同时确定农历1月1日为“春节”，因此元旦在中国也被称为“新历年”、“阳历年”（相对应地，春节称为“旧历年”、“阴历年”等）。 “元旦”一词系中国“土产” 已经沿用4000多年。");//
//				// 节日、节气的描述
//				// intent.putExtra("DAYS", "8");// 距离天数
//				// startActivity(intent);
//				// Map<String,String> dataMap = new HashMap<String,
//				// String>();
//				// dataMap.put("TYPE", "节日");// 标题
//				// dataMap.put("TITLE", "节日");// 推送类型
//				// dataMap.put("HOLIDAY_NAME", "元旦");// 节日或者节气的名称
//				// dataMap.put("ICO", "yd");// 图片名称
//				// dataMap.put("CONTENT",
//				// "元旦，中国节日，即世界多数国家通称的“新年”，是公历新一年开始的第一天。“元旦”一词最早出现于《晋书》。中国古代曾以腊月、十月等的月首为元旦，汉武帝始为农历1月1日，并延用。中华民国始为公历1月1
// 日，1949年中华人民共和国成立时得以明确，同时确定农历1月1日为“春节”，因此元旦在中国也被称为“新历年”、“阳历年”（相对应地，春节称为“旧历年”、“阴历年”等）。 “元旦”一词系中国“土产” 已经沿用4000多年。");//
//				// 节日、节气的描述
//				// dataMap.put("DAYS", "8");// 图片名称
//				//
//				// MNotification mn = new
//				// MNotification(ActivityReminder.this,
//				// dataMap);
//				// mn.showHolidayNotify();
//				break;
//			}
//		}
//	};

    private void showMyDialog() {

        if (dialog == null) {
            View view = LayoutInflater.from(ActivityReminder.this).inflate(R.layout.setfragmetnt_dialog_layout2, null);
            dialogListview = (MyListView) view.findViewById(R.id.listview);
            dialogListview.setAdapter(dialogadapter);

            dialog = new DialogOneButton(ActivityReminder.this, view, "确定", new DialogListener() {
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

    /**
     * 点击确定按钮的时候，保存选择的模式
     *
     * @param position
     */
    private void saveDispalyModel(int position) {
        LocalDataHelper.savePushTag(ActivityReminder.this, PushTag.getInstance().PUSHTAG_TIPS_MODEL, position);
        displayModel = position;
        initDispalyModel(position);
    }

    /**
     * 列表中的单选按钮被选中的时候更改其他的按钮并更新适配器
     */
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setmodel:
                showMyDialog();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
