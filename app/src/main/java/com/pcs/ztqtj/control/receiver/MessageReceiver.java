package com.pcs.ztqtj.control.receiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pcs.ztqtj.control.tool.PushHelper;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理信鸽推送(预警、实况、天气预报、温馨提示)
 *
 * @author chenjh
 */
public class MessageReceiver extends XGPushBaseReceiver {
    public static final String LogTag = "TPushReceiver";
    private static final String Vertical_Line = "[|]";
    private static final String Equal_Sign = "=";
    private static final String Semicolon = ";";

    /**
     * 显示模式：0通知栏，1对话框
     **/
    private int displayModel = 0;

    /**
     * 数据
     **/
    private Map<String, String> dataMap = new HashMap<String, String>();

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {// 接收服务端发来的
        // TYPE_NOTIFICATION

    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = null;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Log.d(LogTag, text);
        // show(context, text);

    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = null;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        // show(context, text);

    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = null;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        // show(context, text);

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = null;
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
        // Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
        // Toast.LENGTH_SHORT).show();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Log.d(LogTag, text);
        // show(context, text);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = null;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
            // 在这里拿token
            String token = message.getToken();
            Log.d(LogTag, token);
        } else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        Log.d(LogTag, text);
        // show(context, text);
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        Map<String, String> dataMap = new HashMap<>();
        String xgContent = message.getContent();
        String xgTitle = message.getTitle();
        dataMap.put("TITLE", xgTitle);
        String[] splitArray = xgContent.split(Vertical_Line);
        if (splitArray.length != 2) {
            return;
        }
        Map<String, String> contentMap = new HashMap<>();
        for (String str : splitArray) {
            if (str.startsWith("CONTENT=")) {
                contentMap.put("CONTENT", str.substring(8));
            } else if (str.startsWith("PARAM=")) {
                contentMap.put("PARAM", str.substring(6));
            }
        }
        String content = contentMap.get("CONTENT");
        String param = contentMap.get("PARAM");
        if (!TextUtils.isEmpty(param) && param.startsWith("{") && param.endsWith("}")) {
            param = param.substring(1, param.length() - 1);
            String[] paramArray = param.split(Semicolon);
            for (String str : paramArray) {
                String[] array = str.split(Equal_Sign);
                if (array.length == 2) {
                    dataMap.put(array[0], array[1]);
                }
            }
        }
        dataMap.put("CONTENT", content);
        PushHelper.send(context, dataMap);
    }

}
