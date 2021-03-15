package com.pcs.ztqtj.control.observables;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListDown.CityDBInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.NetTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tyaathome on 2019/04/01.
 */
public class CityListObservable {

    private Context context;
    private static final String APP_TYPE = "2";
    private static final int TIMEOUT = 10;
    private CityListCallback callback;

    public CityListObservable(Context context) {
        this.context = context;
    }

    public void execute() {
        PackCityListUp up = new PackCityListUp();
        up.app_type = "3";
        NetTask task = new NetTask(context, netListener);
        task.setUrl(context.getString(R.string.url));
        //task.setUrl("http://218.28.7.251:8096/ztq_hn_jc/service.do");
        task.execute(up);
    }

    private NetTask.NetListener netListener = new NetTask.NetListener() {
        @SuppressLint("CheckResult")
        @Override
        public void onComplete(PcsPackDown down) {
            if(down instanceof PackCityListDown) {
                PackCityListDown cityListDown = (PackCityListDown) down;
                List<CityDBInfo> cityDBList = cityListDown.info_list;
                List<Observable<FileEmitterValue>> observableList = new ArrayList<>();
                if(cityDBList.size() != 5) {
                    observableList = getAssetsObservableList();
                } else {
                    for (CityDBInfo info : cityDBList) {
                        observableList.add(getObservable(info));
                    }
                }
                Observable.zip(observableList, new Function<Object[], List<FileEmitterValue>>() {
                    @Override
                    public List<FileEmitterValue> apply(Object[] objects) throws Exception {
                        List<FileEmitterValue> list = new ArrayList<>();
                        for(Object object : objects) {
                            if(object instanceof FileEmitterValue) {
                                FileEmitterValue value = (FileEmitterValue) object;
                                if(value.type == null) continue;
                                list.add(value);
                            }
                        }
                        return list;
                    }
                }).observeOn(Schedulers.io()).doOnNext(new Consumer<List<FileEmitterValue>>() {
                    @Override
                    public void accept(List<FileEmitterValue> fileEmitterValues) {
                        if(callback != null) {
                            callback.onNext(fileEmitterValues);
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if(callback != null) {
                            callback.onComplete();
                        }
                    }
                }).subscribe();
            }
        }
    };

    private Observable<FileEmitterValue> getObservable(CityDBInfo info) {
        return Observable.just(info).observeOn(Schedulers.io()).flatMap(new Function<CityDBInfo, ObservableSource<FileEmitterValue>>() {
            @Override
            public ObservableSource<FileEmitterValue> apply(CityDBInfo cityDBInfo) throws Exception {
                // 下载城市文件
                FileEmitterValue value = getFileFromNet(cityDBInfo);
                return Observable.just(value);
            }
        }).timeout(TIMEOUT, TimeUnit.SECONDS, Observable.just(info).observeOn(Schedulers.io()).flatMap(new Function<CityDBInfo,
                ObservableSource<FileEmitterValue>>() {
            @Override
            public ObservableSource<FileEmitterValue> apply(CityDBInfo cityDBInfo) throws Exception {
                // 从assets中读取文件
                FileEmitterValue value = getFileFromAssets(cityDBInfo.channel_id);
                return Observable.just(value);
            }
        }));
    }

    /**
     * 获取所有从从assets中读取的城市列表的observable数据
     * @return
     */
    private List<Observable<FileEmitterValue>> getAssetsObservableList() {
        List<Observable<FileEmitterValue>> result = new ArrayList<>();
        result.add(getAssetsObservable(FileType.CITYLIST));
        result.add(getAssetsObservable(FileType.TRAVELCITYLIST));
        result.add(getAssetsObservable(FileType.LIVECITY));
        result.add(getAssetsObservable(FileType.STATIONS));
        result.add(getAssetsObservable(FileType.STATIONS_NATIONAL));
        return result;
    }

    /**
     * 获取observable从assets中读取的城市列表数据
     * @return
     */
    private Observable<FileEmitterValue> getAssetsObservable(FileType type) {
        return Observable.just(type).observeOn(Schedulers.io()).flatMap(new Function<FileType, ObservableSource<FileEmitterValue>>() {
            @Override
            public ObservableSource<FileEmitterValue> apply(FileType fileType) throws Exception {
                FileEmitterValue value = getFileFromAssets(fileType.value());
                return Observable.just(value);
            }
        }).timeout(TIMEOUT, TimeUnit.SECONDS, Observable.just(type).observeOn(Schedulers.io()).flatMap(new Function<FileType,
                ObservableSource<FileEmitterValue>>() {
            @Override
            public ObservableSource<FileEmitterValue> apply(FileType fileType) throws Exception {
                FileEmitterValue value = getFileFromAssets(fileType.value());
                return Observable.just(value);
            }
        }));
    }

    /**
     * 从网上获取文件
     * @param cityDBInfo
     * @return
     */
    private FileEmitterValue getFileFromNet(CityDBInfo cityDBInfo) {
        if(cityDBInfo == null || cityDBInfo.channel_id == 0) return null;
        FileType type = FileType.getType(cityDBInfo.channel_id);
        // 缓存文件路径：citydb文件路径/channelId_pubTime (example:citydb/1_201804192110)
        String cacheFilePath = PcsGetPathValue.getInstance().getCityDBPath() + cityDBInfo.channel_id + "_" + cityDBInfo.pub_time;
        File cacheFile = new File(cacheFilePath);
        if(cacheFile.exists()) {
            // 存在缓存文件
            String result = null;
            try {
                InputStream inputStream = new FileInputStream(cacheFile);
                result = getStringFromStream(inputStream);
                if(!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                } else {
                    result = null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                result = null;
            }
            if(TextUtils.isEmpty(result)) {
                // 如果从缓存文件中获取的文件有问题则删除该文件并从assets中获取文件
                cacheFile.delete();
                return getFileFromAssets(cityDBInfo.channel_id);
            }
            return new FileEmitterValue(type, result);
        } else {
            String path = context.getString(R.string.file_download_url) + cityDBInfo.url;
            String result = null;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    long netFileSize = connection.getContentLength();
                    InputStream inputStream = connection.getInputStream();
                    result = getStringFromStream(inputStream);
                    inputStream.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                } else {
                    result = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                result = null;
            }
            // 如果解析出来的json不正确的话就删除刚刚创建的文件并且从assets中载入json文件，反之则将json写入文件
            if(TextUtils.isEmpty(result)) {
                cacheFile.delete();
                return getFileFromAssets(cityDBInfo.channel_id);
            } else {
                saveFile(result, cacheFilePath);
            }
            return new FileEmitterValue(type, result);
        }
    }

    /**
     * 从assets获取文件
     * @param channelId
     * @return
     */
    private FileEmitterValue getFileFromAssets(int channelId) {
        Log.e("getFileFromAssets", "getFileFromAssets : " + channelId);
        FileType type = FileType.getType(channelId);
        if(type == null) return new FileEmitterValue(null, null);
        String fileName = FileType.getFileName(channelId) + ".json";
        if(TextUtils.isEmpty(fileName)) return null;
        String path = "city_info/" + fileName;
        String result = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(path);
            result = getStringFromStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileEmitterValue(type, result);
    }

    /**
     * 存储文件
     * @param data
     * @param path
     */
    private void saveFile(String data, String path) {
        if(TextUtils.isEmpty(path) || TextUtils.isEmpty(data)) return;
        File file = new File(path);
        if(file.exists()) return;
        File dir = file.getParentFile();
        if(!dir.exists() && !dir.mkdirs()) return;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从流获取字符串
     * @param inputStream
     * @return
     */
    private String getStringFromStream(InputStream inputStream) {
        if(inputStream == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public void setCityListCallback(CityListCallback callback) {
        this.callback = callback;
    }

    /**
     * 文件类型枚举
     */
    public enum FileType {
        CITYLIST(2),// 全国城市
        TRAVELCITYLIST(4),// 旅游城市
        LIVECITY(5),// 风雨查询城市
        STATIONS(6),// 省内自动站
        STATIONS_NATIONAL(7);// 全国自动站

        private int _value;

        FileType(int value) {
            _value = value;
        }

        public int value() {
            return _value;
        }

        public static FileType getType(int value) {
            switch (value) {
                case 2:
                    return CITYLIST;
                case 4:
                    return TRAVELCITYLIST;
                case 5:
                    return LIVECITY;
                case 6:
                    return STATIONS;
                case 7:
                    return STATIONS_NATIONAL;
                default:
                    return null;
            }
        }

        public static String getFileName(int value) {
            switch (value) {
                case 2:
                    return "CITYLIST";
                case 4:
                    return "TRAVELCITYLIST";
                case 5:
                    return "LIVECITY";
                case 6:
                    return "STATIONS";
                case 7:
                    return "STATIONS_NATIONAL";
                default:
                    return null;
            }
        }
    }

    public class FileEmitterValue {
        private FileType type;
        private String string;
        public FileEmitterValue(FileType type, String string) {
            this.type = type;
            this.string = string;
        }

        public String getString() {
            return string;
        }

        public FileType getType() {
            return type;
        }
    }

    public interface CityListCallback {
        void onNext(List<FileEmitterValue> list);
        void onComplete();
    }

}
