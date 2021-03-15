package com.pcs.ztqtj.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pcs.ztqtj.control.tool.Sound;
import com.pcs.ztqtj.control.tool.SpeechManager;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("key");
        if (key != null) {
            if ("colock".equals(key)) {
                isspeed(context);
            } else if ("share".equals(key)) {
                autoShare();
            }
        }
    }

    /**
     * 自动分享
     */
    private void autoShare() {
        //TODO: 自定义分享没法使用 传入的参数为activity，
//		ShareUtil.autoShare(context,SHARE_MEDIA.SINA,shareC);
    }

    /**
     * 是语音播报
     *
     * @param context
     */
    private void isspeed(Context context) {
        try {
            SpeechManager spManager = SpeechManager.getInstance();
            if (spManager.isPlaying()) {
                // 如果正在播放则不继续重复播放
                return;
            }
            if (SpeechManager.beExist()) {
                String speechStr = spManager.getSpeechStr();
                spManager.startSpeech(speechStr);
            } else {
                Sound sound = new Sound(context);
                sound.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}