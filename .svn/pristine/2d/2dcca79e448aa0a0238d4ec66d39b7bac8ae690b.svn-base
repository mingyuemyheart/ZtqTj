package com.pcs.ztqtj.control.receiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;
import com.pcs.ztqtj.control.tool.PushHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HWMessageReceiver extends PushReceiver {

    private static final String TAG = "HWMessageReceiver";
    private static final String Vertical_Line = "[|]";
    private static final String Equal_Sign = "=";
    private static final String Semicolon = ";";

    public HWMessageReceiver() {
        super();
    }

    @Override
    public void onPushMsg(Context context, byte[] bytes, String s) {
        super.onPushMsg(context, bytes, s);
        try {
            //CP可以自己解析消息内容，然后做相应的处理
            String result = new String(bytes, "UTF-8");
            Log.e(TAG, "收到PUSH透传消息,消息内容为:" + result);
            Map<String, String> dataMap = new HashMap<>();
            if(!TextUtils.isEmpty(result)) {
                JSONObject jsonObject = new JSONObject(result);
                String xgContent = jsonObject.optString("content");
                String xgTitle = jsonObject.optString("title");
                dataMap.put("TITLE", xgTitle);
                String[] splitArray = xgContent.split(Vertical_Line);
                if (splitArray.length != 2) {
                    return;
                }
                Map<String, String> contentMap = new HashMap<>();
                for (String str : splitArray) {
                    if(str.startsWith("CONTENT=")) {
                        contentMap.put("CONTENT", str.substring(8));
                    } else if(str.startsWith("PARAM=")) {
                        contentMap.put("PARAM", str.substring(6));
                    }
                }
                String content = contentMap.get("CONTENT");
                String param = contentMap.get("PARAM");
                if (!TextUtils.isEmpty(param) && param.startsWith("{") && param.endsWith("}")) {
                    param = param.substring(1, param.length()-1);
                    String[] paramArray = param.split(Semicolon);
                    for(String str : paramArray) {
                        String[] array = str.split(Equal_Sign);
                        if (array.length == 2) {
                            dataMap.put(array[0], array[1]);
                        }
                    }
                }
                dataMap.put("CONTENT", content);
                PushHelper.send(context, dataMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
