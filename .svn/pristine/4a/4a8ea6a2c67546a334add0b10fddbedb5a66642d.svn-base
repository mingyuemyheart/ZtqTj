package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.db.DBHelper;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
import com.pcs.lib_ztqfj_v2.model.pack.FactoryPack;
import com.pcs.ztqtj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tyaathome on 2018/1/4.
 */

public class NetMultiTask extends AsyncTask<PcsPackUp, Void, List<PcsPackDown>> {

    private WeakReference<Context> weakContext;
    private NetListener listener;
    private final Object mLock = new Object();

    public NetMultiTask(Context context, NetListener listener) {
        weakContext = new WeakReference<>(context);
        this.listener = listener;
    }

    @Override
    protected List<PcsPackDown> doInBackground(PcsPackUp... params) {
        return getDown(params);
    }

    @Override
    protected void onPostExecute(List<PcsPackDown> down) {
        super.onPostExecute(down);
        Context context = weakContext.get();
        if (listener != null && context != null) {
            listener.onComplete(context, down);
        }
    }

    private String getUpJson(PcsPackUp... pack) {
        // json
        JSONObject json = new JSONObject();
        // head
        JSONObject head = new JSONObject();
        // body
        JSONObject body = new JSONObject();
        try {
            head.put("p", "");
            json.put("h", head);
            for(PcsPackUp up : pack) {
                body.put(up.getName(), up.toJSONObject());
            }
            json.put("b", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("jzy", "up_net_json:" + json.toString());
        return json.toString();
    }

    private String postDownload(String upJson) {
        try {
            Context context = weakContext.get();
            if(context != null) {
                String mUrl = context.getResources().getString(R.string.url);
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                OutputStream os = connection.getOutputStream();
                upJson = URLEncoder.encode(upJson, "UTF-8");
                String request = "p=" + upJson;
                os.write(request.getBytes());
                os.flush();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String temp;
                    while ((temp = bufferedReader.readLine()) != null) {
                        result.append(temp);
                    }
                    Log.e("jzy", "down_json:" + result.toString());
                    return result.toString();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getBody(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            // Body
            JSONObject b = jsonObject.getJSONObject("b");
            return b.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<PcsPackDown> getDown(PcsPackUp... up) {
        String upjson = getUpJson(up);
        String downJson = postDownload(upjson);
        saveData(downJson);
        String bodyJson = getBody(downJson);
//        FactoryPack factoryPack = new FactoryPack();
//        PcsPackDown down = factoryPack.createDownPack(up.getName());
//        if (down != null) {
//            down.fillData(bodyJson);
//        }
        return fillData(bodyJson);
    }

    private List<PcsPackDown> fillData(String bodyJson) {
        List<PcsPackDown> result = new ArrayList<>();
        try {
            JSONObject b = new JSONObject(bodyJson);
            Iterator<String> keys = b.keys();
            FactoryPack factoryPack = new FactoryPack();
            while(keys.hasNext()) {
                String key = keys.next();
                if(b.get(key) instanceof JSONObject) {
                    JSONObject obj = b.getJSONObject(key);
                    PcsPackDown down = factoryPack.createDownPack(key);
                    if(down != null) {
                        down.fillData(obj.toString());
                        result.add(down);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject b = jsonObject.getJSONObject("b");
            if (b != null) {
                // 保存
                SQLiteDatabase db = null;
                synchronized (mLock) {
                    try {
                        Context context = weakContext.get();
                        if(context != null) {
                            db = DBHelper.getInstance(context).getReadableDatabase();
                            Iterator<String> keys = b.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (b.get(key) instanceof JSONObject) {
                                    JSONObject obj = b.getJSONObject(key);
                                    PcsDataManager.getInstance().seriesSaveData(db, key, obj.toString());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (db != null) {
                            db.close();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface NetListener {
        void onComplete(Context context, List<PcsPackDown> down);
    }
}