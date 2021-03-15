package com.pcs.ztqtj.control.tool;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;

import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SpeechManager {
    private static final int HEAD_LENGTH = 56;
    private AudioTrack audioTrack;
    private static SpeechManager speechManager;
    private OnStatuListener onStatuListener;
    private String version;
    public static boolean soundIsPlaying = false;

    public boolean isPlaying() {
        return soundIsPlaying;
    }

    private static String Voice_PATH;
    private HashMap<String, String> map;

    public static SpeechManager getInstance() {
        if (null == speechManager) {
            speechManager = new SpeechManager();
        }
        return speechManager;
    }

    private SpeechManager() {

        DealWidthFile fileSearch = new DealWidthFile();
        String path = PcsGetPathValue.getInstance().getVoicePath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Voice_PATH = fileSearch.seachFilePath(path, "list");
        mHandler = new Handler();
        init();
    }

    public void init() {
        map = new HashMap<String, String>();
        JSONObject json = null;
        try {
            String text = getString(new File(Voice_PATH + "/" + "list"));
            json = new JSONObject(text);
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                if ("version".equals(key)) {
                    this.version = json.getString(key);

                } else {
                    map.put(key, Voice_PATH + "//" + json.getString(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != onStatuListener) {
                onStatuListener.noHashSpeechPackage();
            }
            speechManager = null;
        }
        initAudio();
    }

    /**
     * 初始化音頻播放器
     */
    @SuppressWarnings("deprecation")
    private void initAudio() {
        final int frequency = 24000;
        final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        final int playBufSize = AudioTrack.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                channelConfiguration, audioEncoding, playBufSize,
                AudioTrack.MODE_STREAM);
    }

    /**
     * 获取语音包版本号
     *
     * @return String
     */
    public String getVersion() {
        if (version == null) {
            speechManager = null;
        }
        return version;
    }

    /**
     * list文件时否存在
     *
     * @return
     */
    public static boolean beExist() {
        DealWidthFile fileSearch = new DealWidthFile();
        String path = PcsGetPathValue.getInstance().getVoicePath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Voice_PATH = fileSearch.seachFilePath(path, "list");
        File file2 = new File(Voice_PATH + "//" + "list");
        return file2.exists();
    }

    public void stop() {
        if (null != audioTrack) {
            audioTrack.stop();
        }
    }

    private MediaPlayer mMediaPlayer = null;

    /**
     * 获取语音字符串
     *
     * @return
     */
    public String getSpeechStr() {
        String speechStr = "";
        try {
            PackLocalCity cityMain = ZtqCityDB.getInstance().getCityMain();

            //实时
            PackSstqUp packSstqUp = new PackSstqUp();
            packSstqUp.area = cityMain.ID;
            PackSstqDown packSstqDown = (PackSstqDown) PcsDataManager.getInstance().getNetPack(packSstqUp.getName());
            if (packSstqDown == null) {
                return speechStr;
            }
            //一周
            PackMainWeekWeatherUp packWeekUp = new PackMainWeekWeatherUp();
            packWeekUp.setCity(cityMain);
            PackMainWeekWeatherDown packWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack(packWeekUp.getName());
            if (packWeekDown == null || packWeekDown.getThreeDay().size() < 2) {
                return speechStr;
            }

            List<WeekWeatherInfo> list = packWeekDown.getThreeDay();

            if (!"1".equals(list.get(0).is_night)) {
                // 若时间在6：40至18：30之间，则播报：天津气象为您播报最新天气预报+城市名称+今天白天+天气情况+最高气温+今天夜间+天气情况+最低气温+风向风力+现在气温；
                // 若时间在18：30至第二日6：40之间，则播报：天津气象为您播报最新天气预报+城市名称+今天夜间+天气情况+最低气温+明天白天+天气情况+最高气温+风向风力+现在气温；
                speechStr = "，，，，天津气象为您播报最新天气预报" + "，今天白天，"
                        + list.get(0).wd_day + "，最高温度"
                        + list.get(0).higt.replace(".", "点")
                        + "摄氏度，今天夜间，" + list.get(0).wd_night
                        + "，最低温度"
                        + list.get(0).lowt.replace(".", "点")
                        + "摄氏度，，， 现在温度" + packSstqDown.ct.replace(".", "点") + "摄氏度，，，";
            } else {
                speechStr = "，，，，天津气象为您播报最新天气预报" + "，今天夜间，"
                        + list.get(0).wd_night + "，最低温度"
                        + list.get(0).lowt.replace(".", "点")
                        + "摄氏度，明天白天，" + list.get(1).wd_day
                        + "，最高温度"
                        + list.get(1).higt.replace(".", "点")
                        + "摄氏度，，，，现在温度，" + packSstqDown.ct.replace(".", "点") + "摄氏度，，，";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return speechStr;
    }

    /**
     * 播放声音
     *
     * @param text 要播放的字符串
     */
    public void startSpeech(String text) {
        mMediaPlayer = new MediaPlayer();

        try {
            File file = new File(Voice_PATH + "/bg_audio.mp3");
            if (file.exists()) {
                mMediaPlayer.setDataSource(Voice_PATH + "/bg_audio.mp3");
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (null != audioTrack) {
            if (AudioTrack.PLAYSTATE_STOPPED != audioTrack.getPlayState()) {
                if (onStatuListener != null) {
                    onStatuListener.alreadyPlaying();
                }
                return;
            }
        }
        ArrayList<File> files = new ArrayList<File>();
        int start = 0;
        int end = text.length();
        while (start < text.length()) {
            String key = text.substring(start, end);
            if (map.containsKey(key)) {
                files.add(new File(map.get(key)));
                start = end;
                end = text.length();
            } else {
                if (end > start) {
                    end--;
                } else {
                    end = text.length();
                    start++;
                }
            }
        }
        final byte[] bytes = getStream(files);
        soundIsPlaying = true;
        audioTrack.play();
        new Thread(new Runnable() {
            public void run() {
                if (bytes != null) {
                    audioTrack.write(bytes, 0, bytes.length);
                    audioTrack.flush();
                    audioTrack.stop();
                    soundIsPlaying = false;
                    mMediaPlayer.stop();
                    if (null != onStatuListener) {
                        onStatuListener.complete();
                    }
                } else {
                }
            }
        }).start();
    }

    private Handler mHandler;

    @SuppressWarnings("finally")
    private String getString(File file) {
        BufferedReader br = null;
        StringBuffer text = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(file));
            String temp = null;
            while (null != (temp = br.readLine())) {
                text.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null == onStatuListener) {
                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            onStatuListener.noHashSpeechPackage();
                        } catch (Exception e2) {

                        }
                    }
                });
            }
            speechManager = null;
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return text.toString();
        }

    }

    /**
     * 获取转换的音频字节
     *
     * @param files
     * @return
     */
    @SuppressWarnings("finally")
    private byte[] getStream(ArrayList<File> files) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        DataInputStream dis = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            int size = 0;
            byte[] buffer = new byte[1024];
            for (int i = 0; i < files.size(); i++) {
                final File f = files.get(i);
                fis = new FileInputStream(f);
                dis = new DataInputStream(fis);
                byte[] head = new byte[HEAD_LENGTH];
                dis.read(head);
                while ((size = dis.read(buffer)) != -1) {
                    baos.write(buffer, 0, size);
                }
            }
            bytes = baos.toByteArray();
            dis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bytes;
        }
    }

    /**
     * 设置音频文件播放监听
     *
     * @param listener
     */
    public void setOnStatuListener(OnStatuListener listener) {
        this.onStatuListener = listener;
    }

    public interface OnStatuListener {

        /**
         * 没有找到相应的字符串
         */
        void noFoundString(String text);

        /**
         * 正在播放
         */
        void alreadyPlaying();

        /**
         * 播放完成
         */
        void complete();

        /**
         * 没有找到相对于的音频文件
         */
        void noHashSpeechPackage();
    }

    public static void closecontext() {
        if (speechManager != null) {
            speechManager = null;
        }
    }
}
