//package com.pcs.ztqtj.view.activity.set;
//
//import android.app.AlarmManager;
//import android.app.Dialog;
//import android.app.PendingIntent;
//import android.app.TimePickerDialog;
//import android.app.TimePickerDialog.OnTimeSetListener;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import com.pcs.ztqtj.R;
//import com.pcs.ztqtj.control.receiver.AlarmReceiver;
//import com.pcs.ztqtj.control.tool.SpeechManager;
//import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
//import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
//import com.pcs.ztqtj.view.dialog.DialogOneButton;
//import com.pcs.ztqtj.view.dialog.DialogTwoButton;
//import com.pcs.ztqtj.view.dialog.DialogWaiting;
//import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownload;
//import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownloadListener;
//import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
//import com.pcs.lib.lib_pcs_v3.control.tool.Util;
//import com.pcs.lib.lib_pcs_v3.control.tool.ZipUtil;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackYuyinDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackYuyinUp;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
///**
// * @author Z 天气闹钟
// */
//public class ActivityWeatherClock extends FragmentActivityZtqBase implements
//        OnClickListener {
//    private Button firsttime;
//    private CheckBox firstopenClosebtn;
//    private Button secondtime;
//    private CheckBox secondOpenClosebtn;
//    private Button thirdtime;
//    private CheckBox thirdopenClosebtn;
//    private Button setbtn;
//    private Dialog yuyincheck;
//    private Dialog yuyinDownload;
//    // private Dialog yuyinOutLine;
//
//    private TextView desc_download;
//    private ProgressBar progerssBar;
//    private Dialog checkDialogdownload;
//    private PackYuyinUp uppack;
//    private PackYuyinDown pack;
//
//    private String timeXML = "clock_xml";
//    private String first = "first_time";
//    private String second = "second_time";
//    private String third = "third_time";
//
//    private final long ONEDAY = 86400000;
//    private final int FIRSTTIME = 1;
//    private final int SECONDTIME = 2;
//    private final int THIRDTIME = 3;
//    private String first_check = "first_check";
//    private String second_check = "second_check";
//    private String third_check = "third_check";
//    private boolean first_checkbox = false;
//    private boolean second_checkbox = false;
//    private boolean third_checkbox = false;
//
//    private PackLocalCity ssta;
//    private Dialog dialogDownloadYuyin;
//
//    private Dialog dialogWaiting;
//    //文件下载
//    private PcsFileDownload mFileDownload = new PcsFileDownload();
//
//    private Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    // 请求语音包数据
//                    if (dialogWaiting == null) {
//                        dialogWaiting = new DialogWaiting(
//                                ActivityWeatherClock.this, "检测最新语音包。。。");
//                    }
//                    if (!dialogWaiting.isShowing()) {
//                        dialogWaiting.show();
//                        reqNet();
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };
//
//    private boolean isempty = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        PcsDataBrocastReceiver.registerReceiver(ActivityWeatherClock.this,
//                receiver);
//        Bundle bundle = getIntent().getExtras();
//        ssta = (PackLocalCity) bundle.getSerializable("city");
//        setTitleText(bundle.getString("title"));
//        setContentView(R.layout.activity_weatherclock);
//        init();
//        SpeechManager speech = SpeechManager
//                .getInstance();
//        if (speech.beExist()) {
//            // 语音包存在
//            isempty = false;
//            // 请求语音包数据
//            if (dialogWaiting == null) {
//                dialogWaiting = new DialogWaiting(ActivityWeatherClock.this,
//                        "检测最新语音包。。。");
//            }
//            if (!dialogWaiting.isShowing()) {
//                dialogWaiting.show();
//                reqNet();
//            }
//        } else {
//            // 语音包不存在
//            isempty = true;
//            View view = LayoutInflater.from(ActivityWeatherClock.this).inflate(
//                    R.layout.dialog_message, null);
//            TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
//            tv.setText("你尚未安装语音包，请下载！");
//            dialogDownloadYuyin = new DialogTwoButton(
//                    ActivityWeatherClock.this, view, "立即下载", "以后再说",
//                    new DialogListener() {
//                        @Override
//                        public void click(String str) {
//                            dialogDownloadYuyin.dismiss();
//                            if (str.equals("立即下载")) {
//                                handler.sendEmptyMessage(0);
//                            } else {
//                                finish();
//                            }
//                        }
//                    });
//            dialogDownloadYuyin.show();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        unregisterReceiver(receiver);
//        super.onDestroy();
//    }
//
//    private MyReceiver receiver = new MyReceiver();
//
//    private void reqNet() {
//
//        if(!isOpenNet()){
//            showToast(getString(R.string.net_err));
//            return ;
//        }
//        // 上传包
//        uppack = new PackYuyinUp();
//        pack = new PackYuyinDown();
//        // 请求网络
//        PcsDataDownload.addDownload(uppack);
//    }
//
//    private class MyReceiver extends PcsDataBrocastReceiver {
//        @Override
//        public void onReceive(String name, String errorStr) {
//            // 返回结果判断
//            if (uppack == null) {
//                return;
//            }
//            if (name.equals(uppack.getName())) {
//                pack = (PackYuyinDown) PcsDataManager.getInstance().getNetPack(name);
//                if (dialogWaiting.isShowing()) {
//                    dialogWaiting.dismiss();
//                }
//                if (pack == null) {
//                    return;
//                }
//                if (isempty) {
//                    // 语音文件为空的时候
//                    downSpeedfunction();
//                } else {
//                    // 检测是否有最新语音包
//                    speedfunction();
//                }
//
//            }
//        }
//    }
//
//    private Toast toast;
//
//    private void speedfunction() {
//
//        try {
//            SpeechManager speech = SpeechManager
//                    .getInstance();
//            String[] filename = pack.url.split("/");
//            if (speech.beExist()) {
//                if ("".equals(pack.version.trim())
//                        || pack.version.trim() == null) {
//                    if (toast == null) {
//                        toast = Toast.makeText(ActivityWeatherClock.this,
//                                "网络异常", Toast.LENGTH_SHORT);
//                    }
//                    toast.setText("网络异常");
//                    toast.show();
//                }
//                // 文件存在
//                if (speech.getVersion().trim().equals(pack.version.trim())) {
//                    if (toast == null) {
//                        toast = Toast.makeText(ActivityWeatherClock.this,
//                                "语音可以正常使用", Toast.LENGTH_SHORT);
//                    } else {
//                        toast.setText("语音可以正常使用");
//                    }
//                    toast.show();
//                } else {
//                    // 版本不同则提示更新
//                    View view = LayoutInflater.from(ActivityWeatherClock.this)
//                            .inflate(R.layout.dialog_message, null);
//                    ((TextView) view.findViewById(R.id.dialogmessage))
//                            .setText("语音包需要更新，是否更新？");
//                    if (yuyinDownload == null) {
//                        yuyinDownload = new DialogTwoButton(
//                                ActivityWeatherClock.this, view, "立即更新",
//                                "以后再说", new DialogListener() {
//                            @Override
//                            public void click(String str) {
//                                yuyinDownload.dismiss();
//                                if (str.equals("立即更新")) {
//                                    downSpeedfunction();
//                                } else {
//                                    finish();
//                                }
//
//                            }
//                        });
//                    }
//                    yuyinDownload.show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void downSpeedfunction() {
//        // 网络请求语音包数据
//        View viewdownload = LayoutInflater.from(ActivityWeatherClock.this)
//                .inflate(R.layout.dialog_download, null);
//        desc_download = (TextView) viewdownload
//                .findViewById(R.id.desc_download);
//        progerssBar = (ProgressBar) viewdownload.findViewById(R.id.progressbar);
//
//        if (checkDialogdownload == null) {
//            checkDialogdownload = new DialogOneButton(
//                    ActivityWeatherClock.this, viewdownload, "取消",
//                    new DialogListener() {
//                        @Override
//                        public void click(String str) {
//                            checkDialogdownload.dismiss();
//                            mFileDownload.cancel();
//                        }
//                    });
//        }
//        checkDialogdownload.setTitle("正在下载");
//        checkDialogdownload.show();
//        String[] filename = pack.url.split("/");
//        mFileDownload.downloadFile(
//                downloadlistener,
//                getString(R.string.file_download_url) + pack.url,
//                PcsGetPathValue.getInstance().getVoicePath()
//                        + filename[filename.length - 1]);
//    }
//
//    private PcsFileDownloadListener downloadlistener = new PcsFileDownloadListener() {
//        @Override
//        public void progress(String url, String fileName, long netSize,
//                             long downSize) {
//            if (checkDialogdownload.isShowing()) {
//                progerssBar.setMax((int) netSize);
//                desc_download.setText("语音包下载中      " + downSize / (1024 * 1024)
//                        + "M/" + netSize / (1024 * 1024) + "M");
//                progerssBar.setProgress((int) downSize);
//            }
//        }
//
//        @Override
//        public void downloadSucc(String url, String fileName) {
//            SpeechManager speech = SpeechManager
//                    .getInstance();
//            String[] filename = pack.url.split("/");
//            if (checkDialogdownload.isShowing()) {
//                checkDialogdownload.dismiss();
//            }
//            File loadFile = new File(PcsGetPathValue.getInstance()
//                    .getVoicePath() + filename[filename.length - 1]);
//            try {
//                ZipUtil.unZip(new FileInputStream(loadFile), PcsGetPathValue
//                        .getInstance().getVoicePath());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (speech.beExist()) {
//                if (toast == null) {
//                    toast = Toast.makeText(ActivityWeatherClock.this,
//                            "语音包下载完成", Toast.LENGTH_SHORT);
//                } else {
//                    toast.setText("语音包下载完成");
//                }
//                toast.show();
//            }
//        }
//
//        @Override
//        public void downloadErr(String url, String fileName, String errMsg) {
//            if (toast == null) {
//                toast = Toast.makeText(ActivityWeatherClock.this, "语音包下载出错",
//                        Toast.LENGTH_SHORT);
//            } else {
//                toast.setText("语音包下载出错");
//            }
//            toast.show();
//        }
//    };
//
//    private void init() {
//        setbtn = (Button) findViewById(R.id.setcity);
//        firsttime = (Button) findViewById(R.id.firsttime);
//        secondtime = (Button) findViewById(R.id.secondtime);
//        thirdtime = (Button) findViewById(R.id.thirdtime);
//        firstopenClosebtn = (CheckBox) findViewById(R.id.first_check);
//        secondOpenClosebtn = (CheckBox) findViewById(R.id.second_check);
//        thirdopenClosebtn = (CheckBox) findViewById(R.id.third_check);
//        firsttime.setOnClickListener(this);
//        secondtime.setOnClickListener(this);
//        thirdtime.setOnClickListener(this);
//        firstopenClosebtn.setOnCheckedChangeListener(new CheckBoxListener());
//        secondOpenClosebtn.setOnCheckedChangeListener(new CheckBoxListener());
//        thirdopenClosebtn.setOnCheckedChangeListener(new CheckBoxListener());
//        initData();
//    }
//
//    private void initData() {
//        first_checkbox = Util.getPreferencesBooleanValue(
//                ActivityWeatherClock.this, timeXML, first_check);
//        second_checkbox = Util.getPreferencesBooleanValue(
//                ActivityWeatherClock.this, timeXML, second_check);
//        third_checkbox = Util.getPreferencesBooleanValue(
//                ActivityWeatherClock.this, timeXML, third_check);
//        firstopenClosebtn.setChecked(first_checkbox);
//        secondOpenClosebtn.setChecked(second_checkbox);
//        thirdopenClosebtn.setChecked(third_checkbox);
//        Calendar c = Calendar.getInstance();
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//        String time = "00:00";
//        // String time = hour + ":" + minute;
//        String f_time = Util.getPreferencesValue(ActivityWeatherClock.this,
//                timeXML, first);
//        String s_time = Util.getPreferencesValue(ActivityWeatherClock.this,
//                timeXML, second);
//        String t_time = Util.getPreferencesValue(ActivityWeatherClock.this,
//                timeXML, third);
//        if ("".endsWith(f_time)) {
//            firsttime.setText(time);
//        } else {
//            firsttime.setText(f_time);
//        }
//        if ("".endsWith(s_time)) {
//            secondtime.setText(time);
//        } else {
//            secondtime.setText(s_time);
//        }
//        if ("".endsWith(t_time)) {
//            thirdtime.setText(time);
//        } else {
//            thirdtime.setText(t_time);
//        }
//    }
//
//    private class CheckBoxListener implements OnCheckedChangeListener {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView,
//                                     boolean isChecked) {
//            switch (buttonView.getId()) {
//                case R.id.first_check:
//                    Util.setPreferencesBooleanValue(ActivityWeatherClock.this,
//                            timeXML, first_check, isChecked);
//                    if (isChecked) {
//                        checkBoxCheck(firsttime.getText().toString().trim(),
//                                FIRSTTIME);
//                    } else {
//                        checkBoxCancel(FIRSTTIME);
//                    }
//                    break;
//                case R.id.second_check:
//                    Util.setPreferencesBooleanValue(ActivityWeatherClock.this,
//                            timeXML, second_check, isChecked);
//                    if (isChecked) {
//                        checkBoxCheck(secondtime.getText().toString().trim(),
//                                SECONDTIME);
//                    } else {
//                        checkBoxCancel(SECONDTIME);
//                    }
//                    break;
//                case R.id.third_check:
//                    Util.setPreferencesBooleanValue(ActivityWeatherClock.this,
//                            timeXML, third_check, isChecked);
//                    if (isChecked) {
//                        checkBoxCheck(thirdtime.getText().toString().trim(),
//                                THIRDTIME);
//                    } else {
//                        checkBoxCancel(THIRDTIME);
//                    }
//                    break;
//            }
//        }
//    }
//
//    /**
//     * 设置时间dialog
//     *
//     * @param btn 选择要改变的button
//     *            保存改变时间的key
//     */
//    private void showTimeDialog(final Button btn) {
//        final SimpleDateFormat f = new SimpleDateFormat("HH:ss");
//        Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//        final TimePickerDialog dialog = new TimePickerDialog(
//                ActivityWeatherClock.this, new OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay,
//                                  int minute) {
//                String changedata = hourOfDay + ":" + minute;
//                try {
//                    // 格式转换；如果取出的时候为0.则格式转换则成了00——》两位数字
//                    Date time = f.parse(changedata);
//                    changedata = f.format(time);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                btn.setText(changedata);
//                switch (btn.getId()) {
//                    case R.id.firsttime:
//                        Util.setPreferencesValue(ActivityWeatherClock.this,
//                                timeXML, first, changedata);
//                        // 开关已经打开
//                        if (first_checkbox) {
//                            checkBoxCheck(changedata, FIRSTTIME);
//                        }
//                        break;
//                    case R.id.secondtime:
//                        Util.setPreferencesValue(ActivityWeatherClock.this,
//                                timeXML, second, changedata);
//                        if (second_checkbox) {
//                            checkBoxCheck(changedata, SECONDTIME);
//                        }
//                        break;
//                    case R.id.thirdtime:
//                        Util.setPreferencesValue(ActivityWeatherClock.this,
//                                timeXML, third, changedata);
//                        if (third_checkbox) {
//                            checkBoxCheck(changedata, THIRDTIME);
//                        }
//                        break;
//                }
//            }
//        }, hour, minute, true);
//        dialog.show();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.firsttime:
//                showTimeDialog(firsttime);
//                break;
//            case R.id.secondtime:
//                showTimeDialog(secondtime);
//                break;
//            case R.id.thirdtime:
//                showTimeDialog(thirdtime);
//                break;
//        }
//    }
//
//    /**
//     * 打开闹钟
//     *
//     * @param flags
//     */
//    public void checkBoxCheck(String timet, int flags) {
//        SpeechManager speech = SpeechManager
//                .getInstance();
//        if (!speech.beExist()) {
//            Toast.makeText(ActivityWeatherClock.this, "语音包不存在，下载后才能使用",
//                    Toast.LENGTH_SHORT).show();
//            switch (flags) {
//                case FIRSTTIME:
//                    firstopenClosebtn.setChecked(false);
//                    break;
//                case SECONDTIME:
//                    secondOpenClosebtn.setChecked(false);
//                    break;
//                case THIRDTIME:
//                    thirdopenClosebtn.setChecked(false);
//                    break;
//                default:
//                    break;
//            }
//            return;
//        }
//        String[] timelist = timet.split(":");
//        if (timelist.length < 2) {
//            return;
//        }
//        Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timelist[0]));
//        c.set(Calendar.MINUTE, Integer.parseInt(timelist[1]));
//        // Date time = new Date(System.currentTimeMillis());
//        try {
//            // time.setHours(Integer.parseInt(timelist[0]));
//            // time.setMinutes(Integer.parseInt(timelist[1]));
//            // long addtime = time.getTime() - System.currentTimeMillis();
//            // Log.i("z", "闹钟与现状时间相差" + (addtime / 1000) / 60 + "分钟");
//            long addtime = c.getTimeInMillis() - System.currentTimeMillis();
//            if (addtime < 0) {
//                addtime += ONEDAY;
//                setClock(addtime, flags);
//            } else {
//                setClock(addtime, flags);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 关闭闹钟
//     *
//     * @param flags
//     */
//    public void checkBoxCancel(int flags) {
//        setCancelClock(flags);
//    }
//
//    public void setClock(long addtime, int flags) {
//        Intent intent = new Intent(ActivityWeatherClock.this, AlarmReceiver.class);
//        intent.putExtra("key", "colock");
//        PendingIntent pendtime = PendingIntent.getBroadcast(
//                ActivityWeatherClock.this, 0, intent, flags);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + addtime, ONEDAY, pendtime);
//        // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//        // System.currentTimeMillis() + addtime, 10 * 1000, pendtime);
//    }
//
//    public void setCancelClock(int flags) {
//        Intent intent = new Intent(ActivityWeatherClock.this, AlarmReceiver.class);
//        intent.putExtra("key", "colock");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                ActivityWeatherClock.this, 0, intent, flags);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
//    }
//}
