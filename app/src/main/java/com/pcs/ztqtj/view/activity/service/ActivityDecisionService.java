package com.pcs.ztqtj.view.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterDecisionService;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.ColumnDto;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 专项服务-决策服务
 */
public class ActivityDecisionService extends FragmentActivityZtqWithHelp {

    private TextView null_data;
    private ExpandableListView explistviw;
    private AdapterDecisionService listAdatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_service);
        initWidget();
        initExpandableListView();
    }

    private void initWidget() {
        null_data = findViewById(R.id.null_data);

        String title = getIntent().getStringExtra("title");
        if (title != null) {
            setTitleText(title);
        }

        okHttpInfoList();
    }

    private void initExpandableListView() {
        String showSubtitle = getIntent().getStringExtra("subtitle");// 是否显示副标题：-决策报告-
        explistviw = findViewById(R.id.myexlistviw);
        listAdatper = new AdapterDecisionService(ActivityDecisionService.this, showSubtitle);
        listAdatper.setMoreListener(new AdapterDecisionService.MoreClickListener() {
            @Override
            public void onClick(PackQxfuMyproV2Down.SubClassList bean) {
                Intent intent = new Intent(ActivityDecisionService.this, ActivityPdfList.class);
                ColumnDto data = new ColumnDto();
                data.dataCode = bean.channel_id;
                data.dataName = bean.channel_name;
                ArrayList<ColumnDto> childList = new ArrayList<>();
                ColumnDto child = new ColumnDto();
                child.dataCode = bean.channel_id;
                child.dataName = bean.org_name;
                childList.add(child);
                data.childList.addAll(childList);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        explistviw.setAdapter(listAdatper);
        explistviw.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        explistviw.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                PackQxfuMyproV2Down.DesServer currentDes = (PackQxfuMyproV2Down.DesServer) listAdatper.getChild(groupPosition, childPosition);
                PackQxfuMyproV2Down.SubClassList currentSubClass = (PackQxfuMyproV2Down.SubClassList) listAdatper.getGroup(groupPosition);
                Intent intent = new Intent(ActivityDecisionService.this, ActivityServeDetails.class);
                SharedPreferencesUtil.putData(getString(R.string.file_download_url)+currentDes.html_url,getString(R.string.file_download_url)+currentDes.html_url);
                intent.putExtra("url", getString(R.string.file_download_url)+currentDes.html_url);
                intent.putExtra("title", currentSubClass.channel_name);
                intent.putExtra("style", currentDes.style);
                startActivity(intent);
                return true;
            }
        });
    }

    /**
     * 获取我的服务
     */
    private void okHttpInfoList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", "101030109");//101030301决策服务，101030109决策报告都是一样的数据
                    info.put("extra", "");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("info_list", json);
                    final String url = CONST.BASE_URL+"info_list";
                    Log.e("info_list", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
                                    dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("info_list", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("qxfw_mypro_v2")) {
                                                    JSONObject qxfw_mypro_v2 = bobj.getJSONObject("qxfw_mypro_v2");
                                                    if (!TextUtil.isEmpty(qxfw_mypro_v2.toString())) {
                                                        PackQxfuMyproV2Down down = new PackQxfuMyproV2Down();
                                                        down.fillData(qxfw_mypro_v2.toString());
                                                        if (down != null) {
                                                            try {
                                                                listAdatper.setData(down.classList);
                                                                for (int j = 0; j < listAdatper.getGroupCount(); j++) {
                                                                    explistviw.expandGroup(j);
                                                                }
                                                                if (down.classList == null || down.classList.size() == 0) {
                                                                    null_data.setVisibility(View.VISIBLE);
                                                                    explistviw.setVisibility(View.GONE);
                                                                } else {
                                                                    null_data.setVisibility(View.GONE);
                                                                    explistviw.setVisibility(View.VISIBLE);
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (!obj.isNull("errorMessage")) {
                                                    String errorMessage = obj.getString("errorMessage");
                                                    if (errorMessage != null) {
                                                        Toast.makeText(ActivityDecisionService.this, errorMessage, Toast.LENGTH_SHORT).show();
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

}