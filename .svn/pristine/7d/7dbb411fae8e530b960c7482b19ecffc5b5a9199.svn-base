package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
import com.pcs.lib_ztqfj_v2.model.pack.FactoryPack;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.ztqtj.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tyaathome on 2018/1/4.
 */

public class NetTask extends AsyncTask<PcsPackUp, Void, PcsPackDown> {

    private Context context;
    private NetListener listener;
    private String mUrl;

    public NetTask(Context context, NetListener listener) {
        this.context = context;
        this.listener = listener;
        mUrl = context.getResources().getString(R.string.url);
    }

    @Override
    protected PcsPackDown doInBackground(PcsPackUp... params) {
        PcsPackUp up = params[0];
        return getDown(up);
    }

    @Override
    protected void onPostExecute(PcsPackDown down) {
        super.onPostExecute(down);
        if(listener != null) {
            listener.onComplete(down);
        }
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    private String getUpJson(PcsPackUp pack) {
        // json
        JSONObject json = new JSONObject();
        // head
        JSONObject head = new JSONObject();
        // body
        JSONObject body = new JSONObject();
        try {
            PackInitUp initUp = new PackInitUp();
            PackInitDown packInit = (PackInitDown) PcsDataManager.getInstance().getNetPack(initUp.getName());
            if(packInit == null || TextUtils.isEmpty(packInit.pid)) {
                head.put("p", "");
            } else {
                head.put("p", packInit.pid);
            }
            json.put("h", head);
            body.put(pack.getName(), pack.toJSONObject());
            json.put("b", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("jzy", "up_net_json:" + json.toString());
        return json.toString();
    }

    private String postDownload(String upJson) {
        List<BasicNameValuePair> httpParams = new LinkedList<BasicNameValuePair>();
        httpParams.add(new BasicNameValuePair("p", upJson));
        HttpPost postMethod = new HttpPost(mUrl);
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(httpParams, "utf-8"));
            HttpResponse response = createHttpClient().execute(postMethod);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String json = EntityUtils.toString(response.getEntity(),
                        "utf-8");
                Log.e("jzy", "down_json:" + json);
                return json;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
        HttpConnectionParams.setSoTimeout(params, 30 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
                params, schReg);

        return new DefaultHttpClient(connMgr, params);
    }

    private String getBody(String json, String name) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            // Body
            JSONObject b = jsonObject.getJSONObject("b").getJSONObject(name);
            return b.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private PcsPackDown getDown(PcsPackUp up) {
        String upjson = getUpJson(up);
        String downJson = postDownload(upjson);
        String bodyJson = getBody(downJson, up.getName());
        FactoryPack factoryPack = new FactoryPack();
        PcsPackDown down = factoryPack.createDownPack(up.getName());
        if(down != null) {
            down.fillData(bodyJson);
        }
        return down;
    }

    public interface NetListener {
        void onComplete(PcsPackDown down);
    }
}