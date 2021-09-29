package com.pcs.ztqtj.view.activity.service;

import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.util.ColumnDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPackQxfuMyproV2Down extends PcsPackDown {
    public boolean auth_pass;
    public List<MyPackQxfuMyproV2Down.ClassList> classList;

    public MyPackQxfuMyproV2Down() {
    }

    public void fillData(String jsonStr) {
        this.classList = new ArrayList();

        try {
            JSONObject obj = new JSONObject(jsonStr);
            this.auth_pass = obj.getBoolean("auth_pass");
            JSONArray channelsArray = obj.getJSONArray("channels");

            for(int i = 0; i < channelsArray.length(); ++i) {
                MyPackQxfuMyproV2Down.ClassList listBean = new MyPackQxfuMyproV2Down.ClassList();
                JSONObject objOne = channelsArray.getJSONObject(i);
                listBean.channel_name = objOne.getString("channel_name");
                listBean.channel_id = objOne.getString("channel_id");
                listBean.channels = new ArrayList();
                JSONArray subChannel = objOne.getJSONArray("children");

                for(int j = 0; j < subChannel.length(); ++j) {
                    JSONObject objTwo = subChannel.getJSONObject(j);
                    MyPackQxfuMyproV2Down.SubClassList channels = new MyPackQxfuMyproV2Down.SubClassList();
                    channels.org_name = objTwo.optString("org_name");
                    channels.org_id = objTwo.optString("org_id");
                    channels.channel_name = objTwo.optString("channel_name");
                    channels.channel_id = objTwo.optString("channel_id");
                    channels.pro_list = new ArrayList();
                    JSONArray desServer = objTwo.getJSONArray("pro_list");

                    for(int k = 0; k < desServer.length(); ++k) {
                        JSONObject objDes = desServer.getJSONObject(k);
                        MyPackQxfuMyproV2Down.DesServer des = new MyPackQxfuMyproV2Down.DesServer();
                        des.html_url = objDes.optString("html_url");
                        des.title = objDes.optString("title");
                        des.create_time = objDes.optString("create_time");
                        des.type = objDes.optString("type");
                        des.id = objDes.optString("id");
                        des.style = objDes.optString("style");
                        channels.pro_list.add(des);
                    }

                    if (!objTwo.isNull("childList")) {
                        ArrayList<ColumnDto> childList = new ArrayList<>();
                        JSONArray childArray = objTwo.getJSONArray("childList");
                        for (int k = 0; k < childArray.length(); k++) {
                            ColumnDto dto = new ColumnDto();
                            JSONObject itemObj = childArray.getJSONObject(k);
                            parseItemObj(itemObj, dto);
                            childList.add(dto);
                        }
                        channels.childList.addAll(childList);
                    }

                    listBean.channels.add(channels);
                }

                this.classList.add(listBean);
            }
        } catch (JSONException var15) {
            var15.printStackTrace();
        }

    }

    public String toString() {
        return null;
    }

    public class DesServer {
        public String html_url;
        public String title;
        public String create_time;
        public String type;
        public String id;
        public String style;

        public DesServer() {
        }
    }

    public class SubClassList {
        public String org_name;
        public String org_id;
        public String channel_name;
        public String channel_id;
        public List<MyPackQxfuMyproV2Down.DesServer> pro_list;
        public ArrayList<ColumnDto> childList = new ArrayList<>();

        public SubClassList() {
        }
    }

    public class ClassList {
        public String channel_name;
        public String channel_id;
        public List<MyPackQxfuMyproV2Down.SubClassList> channels;

        public ClassList() {
        }
    }

    private void parseItemObj(JSONObject itemObj, ColumnDto dto) {
        try {
            if (!itemObj.isNull("dataCode")) {
                dto.dataCode = itemObj.getString("dataCode");
            }
            if (!itemObj.isNull("dataName")) {
                dto.dataName = itemObj.getString("dataName");
            }
            if (!itemObj.isNull("parentId")) {
                dto.parentId = itemObj.getString("parentId");
            }
            if (!itemObj.isNull("icon")) {
                dto.icon = itemObj.getString("icon");
            }
            if (!itemObj.isNull("flag")) {
                dto.flag = itemObj.getString("flag");
            }
            if (!itemObj.isNull("url")) {
                dto.url = itemObj.getString("url");
            }
            if (!itemObj.isNull("desc")) {
                dto.desc = itemObj.getString("desc");
            }
            if (TextUtils.equals(dto.flag, "0")) {
                MyApplication.LIMITINFO += dto.dataCode+",";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

