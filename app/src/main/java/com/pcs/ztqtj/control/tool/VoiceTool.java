package com.pcs.ztqtj.control.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.ztqtj.control.main_weather.CommandMainRow0;
import com.pcs.ztqtj.control.main_weather.CommandMainRow1;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.ActivityMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ${jx_chen}
 * on 2019-3-15.
 */

public class VoiceTool {

    private static VoiceTool instance;
    private ActivityMain mContext;
    private CommandMainRow0 mCommandMainRow0;

    public static VoiceTool getInstance(ActivityMain context,CommandMainRow0 commandMainRow0) {
        if (instance == null || (context != null)) {
            instance = new VoiceTool(context,commandMainRow0);
        }
        return instance;
    }

    public VoiceTool(ActivityMain context,CommandMainRow0 commandMainRow0) {
        mContext = context;
        initView(context);
        mCommandMainRow0=commandMainRow0;
        initCityInfo();
    }


    // 语音听写对象
    private static String TAG;
    public SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    // 引擎类型TYPE_CLOUD为云端
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 语音合成对象
    public SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    // 保存听写参数的对象 参数是jar 里写好的
    private SharedPreferences mSharedPreferences;

    private void initView(Context context){
        //讯飞语音开发
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mSharedPreferences = context.getSharedPreferences("com.iflytek.setting",
                Activity.MODE_PRIVATE);
        // 语音合成所需要用到的对象
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        TAG = context.getClass().getSimpleName();
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                readResult("没听清楚，请重新说一遍");
            }
        }
    };


    /**
     * 初始化监听。
     * <p>
     * **************************************重要*************************************
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                readResult("没听清楚，请重新说一遍");
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    int ret = 0; // 函数调用返回值

    public void initCityInfo() {
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        String content = changeArrayDateToJson(ZtqCityDB.getInstance().getAllCityInfos());
        ret = mIat.updateLexicon("userword", content, mLexiconListener);
    }

    /**
     * 上传联系人/词表监听器。
     */
    private LexiconListener mLexiconListener = new LexiconListener() {

        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                showTip(error.toString());
            }
        }
    };

    /**
     * 弹框提示用户信息
     *
     * @param str
     */
    public void showTip(final String str) {
//        EditText ed = (EditText) findViewById(R.id.iat_text);
//        ed.setText(str);
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
    /**
     * 将数组转换为JSON格式的数据。
     *
     * @param list 数据源
     * @return JSON格式的数据
     */
    public String changeArrayDateToJson(List<PackLocalCity> list) {
        try {
            JSONArray array = new JSONArray();
            JSONArray array_down = new JSONArray();
            JSONObject object = new JSONObject();
            for (int i = 0; i < 1; i++) {
                JSONObject stoneObject = new JSONObject();
                stoneObject.put("name", "全国城市列表");
                for (int j = 0; j < list.size(); j++) {
                    PackLocalCity packLocalCity = list.get(j);
                    String words = packLocalCity.NAME;
                    array_down.put(words);
                }
                stoneObject.put("words", array_down);
                array.put(stoneObject);
            }
            object.put("userword", array);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 合成回调监听。
     * **************************************重要*************************************
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            mCommandMainRow0.speakBegin();
//            main_voice.setBackgroundResource(R.drawable.btn_mainvoice_sel);
//            isPlay = true;
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            mCommandMainRow0.completeRead();
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    /**
     * 读出翻译结果
     */
    public void readResult(String str) {
//        String text = ((EditText) findViewById(R.id.iat_text)).getText()
//                .toString();
        mContext.dismissProgressDialog();
        setVecParam();
        int code = mTts.startSpeaking(str, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            readResult("没听清楚，请重新说一遍");
        }
    }

    /**
     * 听写参数设置
     * <p>
     * **************************************重要*************************************
     *
     * @return
     */
    public void setRecParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//        String lag = mSharedPreferences.getString("iat_language_preference",
//                "mandarin");
//        if (lag.equals("en_us")) {
//            // 设置语言
//            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
//        } else {
//        mSharedPreferences.getBoolean()
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "zh_cn");
//        }
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,
                mSharedPreferences.getString("iat_vadbos_preference", "2000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,
                mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,
                mSharedPreferences.getString("iat_punc_preference", "0"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/iat.wav");

    }


    /**
     * 合成参数设置
     * <p>
     * **************************************重要*************************************
     */
    public void setVecParam() {

        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        // 设置合成语速
        mTts.setParameter(SpeechConstant.SPEED,
                mSharedPreferences.getString("speed_preference", "50"));
        // 设置合成音调
        mTts.setParameter(SpeechConstant.PITCH,
                mSharedPreferences.getString("pitch_preference", "50"));
        // 设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME,
                mSharedPreferences.getString("volume_preference", "50"));
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE,
                mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }
    /**
     * 听写监听器。
     * <p>
     * **************************************重要*************************************
     */
    public RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            mCommandMainRow0.dismissPopupWindow();
            if (error.getErrorCode() == 10118) {
                readResult("没听清楚，请重新说一遍");
            }
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
//            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            mCommandMainRow0.dismissPopupWindow();
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            // showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            mCommandMainRow0.printResult(results);

            if (isLast) {
                // TODO 最后的结果
                Log.d(TAG, results.getResultString());
            }
        }

        /**
         * 音量发生变化
         */
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            mCommandMainRow0.setImageChange(volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        /**
         * 云端出错联系技术人员使用该方法
         */
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            /*
             * if (SpeechEvent.EVENT_SESSION_ID == eventType) { String sid =
			 * obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID); Log.d(TAG,
			 * "session id =" + sid); }
			 */
        }
    };

}
