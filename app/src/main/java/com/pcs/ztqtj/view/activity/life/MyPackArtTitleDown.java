package com.pcs.ztqtj.view.activity.life;

import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPackArtTitleDown extends PcsPackDown {
    public List<MyArtTitleInfo> artTitleList = new ArrayList();
    public String channel = "";
    public String page = "";

    public MyPackArtTitleDown() {
    }

    public void fillData(String jsonStr) {
        try {
            this.artTitleList.clear();
            JSONObject temp = new JSONObject(jsonStr);
            this.updateMill = temp.optLong("updateMill");
            this.channel = temp.getString("channel");
            this.page = temp.getString("page");
            JSONArray arr = temp.getJSONArray("titles");

            for (int i = 0; i < arr.length(); ++i) {
                MyArtTitleInfo title = new MyArtTitleInfo();
                JSONObject airJson = arr.getJSONObject(i);
                if (!airJson.isNull("title")) {
                    title.title = airJson.getString("title");
                }
                if (!airJson.isNull("desc")) {
                    title.desc = airJson.getString("desc");
                }
                if (!airJson.isNull("pubt")) {
                    title.pubt = airJson.getString("pubt");
                }
                if (!airJson.isNull("big_ico")) {
                    title.big_ico = airJson.getString("big_ico");
                }
                if (!airJson.isNull("small_ico")) {
                    title.small_ico = airJson.getString("small_ico");
                }
                if (!airJson.isNull("url")) {
                    title.url = airJson.getString("url");
                }
                if (!airJson.isNull("content")) {
                    title.content = airJson.getString("content");
                }
                this.artTitleList.add(title);
            }
        } catch (JSONException var7) {
            var7.printStackTrace();
        }

    }
}

