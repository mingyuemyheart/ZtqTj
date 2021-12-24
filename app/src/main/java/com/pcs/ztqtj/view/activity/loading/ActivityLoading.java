package com.pcs.ztqtj.view.activity.loading;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.CommandBatching;
import com.pcs.ztqtj.control.command.InterCommand;
import com.pcs.ztqtj.control.inter.Callback;
import com.pcs.ztqtj.control.loading.CommandLoadingCheck;
import com.pcs.ztqtj.control.loading.CommandLoadingCity;
import com.pcs.ztqtj.control.loading.CommandLoadingUnit;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.CommonUtil;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 闪屏页
 */
public class ActivityLoading extends FragmentActivityBase {

    private String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private boolean requestOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        
        TextView text_version = findViewById(R.id.text_version);
        text_version.setText(CommonUtil.getVersion(this));

        if (!policyFlag()) {
            promptDialog();
        }else {
            initWidget();
        }
    }

    private void initWidget() {
        checkPermissions(needPermissions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(requestOnResume) {
            checkPermissions(needPermissions);
            requestOnResume = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsTools.PERMISSON_REQUESTCODE) {
            init();
        }
    }

    /**
     * 检查权限
     */
    private void checkPermissions(String[] permissions) {
        if (PermissionsTools.checkPermissions(this, permissions)) {
            init();
        }
    }

    private void init() {
        clearUserInfo();
        createImageFetcher();
        ZtqCityDB.getInstance().initnew(this, new Callback() {
            @Override
            public void onCallback() {
                CommandBatching batchingLoading = new CommandBatching();
                // 超时20秒
                batchingLoading.setOverTime(40000);
                // 最少2秒
                batchingLoading.setMinTime(2000);
                // 顺序执行
                batchingLoading.setQueueExecute(true);
                //监听
                batchingLoading.addListener(mLoadingListener);
                //检查初始化
                CommandLoadingCheck commandCheck = new CommandLoadingCheck(getApplicationContext());
                batchingLoading.addCommand(commandCheck);
                // 城市
                CommandLoadingCity commandCity = new CommandLoadingCity(ActivityLoading.this);
                batchingLoading.addCommand(commandCity);
                // 单位
                CommandLoadingUnit commandUnit = new CommandLoadingUnit(ActivityLoading.this);
                batchingLoading.addCommand(commandUnit);
                // 执行
                batchingLoading.execute();
            }
        });
    }

    private void clearUserInfo() {
        SharedPreferences shared = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        boolean first = shared.getBoolean("first", true);
        if(first) {
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean("first", false);
            editor.apply();
        }
    }

    private InterCommand.InterCommandListener mLoadingListener = new InterCommand.InterCommandListener() {
        @Override
        public void done(InterCommand.Status status) {
            if (status == InterCommand.Status.FAIL) {
                Toast.makeText(ActivityLoading.this, R.string.init_error, Toast.LENGTH_SHORT).show();
                return;
            }
            okHttpTheme();
        }
    };

    /**
     * 获取闪屏页照片
     */
    private void okHttpTheme() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("type", "0");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"subject_list";
                    Log.e("subject_list", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent it = new Intent();
                                    it.setClass(ActivityLoading.this, ActivityMain.class);
                                    Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
                                    if(bundle != null) {
                                        it.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
                                    }
                                    startActivity(it);
                                    finish();
                                }
                            });
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("ztq_img")) {
                                                    JSONObject ztq_img = bobj.getJSONObject("ztq_img");
                                                    if (!ztq_img.isNull("dataList")) {
                                                        JSONObject dataList = ztq_img.getJSONObject("dataList");
                                                        String imgUrl = "";
                                                        long showTime = 1500;
                                                        if (!dataList.isNull("big_img_path")) {
                                                            String big_img_path = dataList.getString("big_img_path");
                                                            if (!TextUtil.isEmpty(big_img_path)) {
                                                                imgUrl = getString(R.string.msyb)+big_img_path;
                                                            }
                                                        }
                                                        if (!dataList.isNull("show_time")) {
                                                            showTime = dataList.getLong("show_time");
                                                        }

                                                        Intent it = new Intent();
                                                        if (!TextUtil.isEmpty(imgUrl)) {
                                                            it.setClass(ActivityLoading.this, ActivityLoadingImage.class);
                                                            it.putExtra("imgUrl", imgUrl);
                                                            it.putExtra("showTime", showTime);
                                                        } else {
                                                            it.setClass(ActivityLoading.this, ActivityMain.class);
                                                        }
                                                        Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
                                                        if(bundle != null) {
                                                            it.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
                                                        }
                                                        startActivity(it);
                                                        finish();
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 温馨提示对话框
     */
    private void promptDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_policy, null);
        TextView tvProtocal = view.findViewById(R.id.tvProtocal);
        TextView tvPolicy = view.findViewById(R.id.tvPolicy);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLoading.this, ActivityWebView.class);
                intent.putExtra("title", "天津气象软件许可及服务协议");
                intent.putExtra("url", CONST.PROTOCAL);
                intent.putExtra("shareContent", "天津气象软件许可及服务协议");
                startActivity(intent);
            }
        });
        tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLoading.this, ActivityWebView.class);
                intent.putExtra("title", "天津气象软件用户隐私政策");
                intent.putExtra("url", CONST.PRIVACY);
                intent.putExtra("shareContent", "天津气象软件用户隐私政策");
                startActivity(intent);
            }
        });
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                savePolicyFlag();
                initWidget();
            }
        });
    }

    private void savePolicyFlag() {
        SharedPreferences sp = getSharedPreferences("policy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isShow", true);
        editor.apply();
    }

    private boolean policyFlag() {
        SharedPreferences sp = getSharedPreferences("policy", Context.MODE_PRIVATE);
        return sp.getBoolean("isShow", false);
    }

}
