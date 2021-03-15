package com.pcs.ztqtj.view.activity.set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.receiver.AlarmReceiver;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;


import java.util.Calendar;
import java.util.Map;

public class ActivityAutoShare extends FragmentActivityZtqBase implements
        OnClickListener {

    private TextView textsharecontent;// share content

    private CheckBox autoshare_share_check;// auto share switch

    private Button sharecity;// share city button

    private Button timebutton;// set time button
    private Button bundlesina;// bundle sina
    private Button bundletencent;// bundle tencent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        setContentView(R.layout.activity_autoshare);
        init();
        initdata();
        initListener();
    }

    private void init() {
        textsharecontent = (TextView) findViewById(R.id.textsharecontent);
        autoshare_share_check = (CheckBox) findViewById(R.id.autoshare_share_check);
        sharecity = (Button) findViewById(R.id.sharecity);
        timebutton = (Button) findViewById(R.id.timebutton);
        bundlesina = (Button) findViewById(R.id.bundlesina);
        bundletencent = (Button) findViewById(R.id.bundletencent);
    }

    private void initdata() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        sharecity.setText(cityMain.NAME);
        String time = Util.getPreferencesValue(ActivityAutoShare.this,
                AUTOSHARE, "time");
        if (TextUtils.isEmpty(time)) {
            timebutton.setText("00:00");
        } else {
            timebutton.setText(time);
        }

        // 是否已经打开自动分享
        boolean openShare = Util.getPreferencesBooleanValue(
                ActivityAutoShare.this, AUTOSHARE, "open");
        // 是否已经授权
        boolean authorization = Util.getPreferencesBooleanValue(
                ActivityAutoShare.this, AUTOSHARE, "promission");

        if (!openShare || !authorization) {
            autoshare_share_check.setChecked(false);
        } else {
            autoshare_share_check.setChecked(openShare);
        }
        if (authorization) {
            bundlesina.setText("已绑定");
        } else {
            bundlesina.setText("请绑定");
        }
        shareContent();
    }

    String shareC = "";

    private void shareContent() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        PackMainWeekWeatherDown pcsDownPack = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack(PackMainWeekWeatherUp.NAME + "#" + cityMain.PARENT_ID + cityMain.ID);
        if (pcsDownPack == null) {
            pcsDownPack = new PackMainWeekWeatherDown();
        }
        shareC = pcsDownPack.getShareStr(cityMain.NAME);
        textsharecontent.setText(shareC);

    }

    private TimePickerDialog dialogTime;

    private void initListener() {
        timebutton.setOnClickListener(this);
        bundlesina.setOnClickListener(this);
        autoshare_share_check
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            Util.setPreferencesBooleanValue(
                                    ActivityAutoShare.this, AUTOSHARE, "open",
                                    true);
                            // 是否已经授权
                            boolean authorization = Util
                                    .getPreferencesBooleanValue(
                                            ActivityAutoShare.this, AUTOSHARE,
                                            "promission");
                            if (authorization) {
                                setClock(
                                        timebutton.getText().toString().trim(),
                                        11100);
                            } else {
                                setCancelClock(11100);
                            }
                        } else {
                            Util.setPreferencesBooleanValue(
                                    ActivityAutoShare.this, AUTOSHARE, "open",
                                    isChecked);
                            setCancelClock(11100);
                        }
                    }
                });
    }

    private void showTimeDialog() {
        if (dialogTime == null) {
            dialogTime = new TimePickerDialog(ActivityAutoShare.this,
                    new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String time = "";
                            if (hourOfDay < 10) {
                                time += "0" + hourOfDay;
                            } else {
                                time += hourOfDay;
                            }
                            if (minute < 10) {
                                time += ":0" + minute;
                            } else {
                                time += ":" + minute;
                            }
                            timebutton.setText(time);

                            Util.setPreferencesValue(ActivityAutoShare.this,
                                    AUTOSHARE, "time", time);
                            // 是否已经打开自动分享
                            boolean openShare = Util
                                    .getPreferencesBooleanValue(
                                            ActivityAutoShare.this, AUTOSHARE,
                                            "open");
                            // 是否已经授权
                            boolean authorization = Util
                                    .getPreferencesBooleanValue(
                                            ActivityAutoShare.this, AUTOSHARE,
                                            "promission");
                            if (openShare && authorization) {
                                setClock(time, 11100);
                            } else {
                                setCancelClock(11100);
                            }
                        }
                    }, 0, 0, true);
        }
        dialogTime.show();
    }

    private final String AUTOSHARE = "autoshare";
//	private static UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    private void shareFunction(String str) {
        UMShareAPI mShareAPI = UMShareAPI.get(this);
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.SINA, umAuthListener);
//        OauthHelper.isAuthenticated(ActivityAutoShare.this, SHARE_MEDIA.SINA);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            Toast.makeText(ActivityAutoShare.this, "授权完成",
                    Toast.LENGTH_SHORT).show();
            // 获取相关授权信息或者跳转到自定义的分享编辑页面
            Util.setPreferencesBooleanValue(ActivityAutoShare.this, AUTOSHARE, "promission", true);
            bundlesina.setText("已绑定");

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bundlesina:
                // 设置新浪SSO handler
                boolean hasAuto = Util.getPreferencesBooleanValue(ActivityAutoShare.this, AUTOSHARE, "promission");
                if (!hasAuto) {
                    shareFunction(shareC);
                } else {
//                    OauthHelper.isAuthenticated(ActivityAutoShare.this, SHARE_MEDIA.SINA);
                    Util.setPreferencesBooleanValue(ActivityAutoShare.this, AUTOSHARE, "promission", false);
                    bundlesina.setText("请绑定");
                    setCancelClock(11100);
                }
                break;
            case R.id.timebutton:
                showTimeDialog();
                break;
        }
    }

    /**
     * 绑定闹钟
     *
     * @param time  12:00格式
     * @param flags 标记闹钟
     */
    public void setClock(String time, int flags) {
        // 重置前先取消在设置循环
        setCancelClock(flags);
        String[] h_s = time.split(":");
        Calendar c = Calendar.getInstance();
        long nowTime = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h_s[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(h_s[1]));
        long shareTime = c.getTimeInMillis();
        long ONEDAY = 86400000;
        // 如果设置的时间小于当前时间这增加一天
        if (nowTime < shareTime) {
            shareTime = shareTime + ONEDAY;
        }
        Intent intent = new Intent(ActivityAutoShare.this, AlarmReceiver.class);
        intent.putExtra("key", "share");
        PendingIntent pendtime = PendingIntent.getBroadcast(
                ActivityAutoShare.this, 0, intent, flags);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, shareTime, ONEDAY,
                pendtime);
    }

    /**
     * 取消闹钟绑定
     *
     * @param flags
     */
    public void setCancelClock(int flags) {
        Intent intent = new Intent(ActivityAutoShare.this, AlarmReceiver.class);
        intent.putExtra("key", "share");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ActivityAutoShare.this, 0, intent, flags);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
