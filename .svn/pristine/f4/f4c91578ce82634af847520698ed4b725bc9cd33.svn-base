package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.db.DBHelper;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import static android.content.ContentValues.TAG;
import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * @author chenjh
 */
public class KWHttpRequest {

    public static final int BUFFER_SIZE = 4 * 1024;

    private static final int DEFAULT_THREAD_POOL_SIZE = 15;
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    // private static ThreadPoolExecutor executor = (ThreadPoolExecutor)
    // Executors.newCachedThreadPool();//
    private static Handler mHandler = null;
    private Context mContext;

    private String strURL = "";

    /**
     * json包开头的p
     */
    private static String mP = "";

    private static String mFilePath = "";

    private HashMap<String, String> uploadFile = new HashMap<String, String>();
    private HashMap<String, String> headers = new HashMap<String, String>();
    private HashMap<String, String> postData = new HashMap<String, String>();
    /**
     * 请求列表
     */
    private Map<String, PcsPackUp> requestsMap = new HashMap<String, PcsPackUp>();
    private KwHttpRequestListener callBack;
    private int nThreadID = 0;

    // public final String BOUNDARY = "7cd4a6d158c";
    // public final String MP_BOUNDARY = "--" + BOUNDARY;
    // public final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
    // public final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static int ERRCODE_NETWORK = 1001;
    public static int ERRCODE_READ = 1002;
    public static int ERRCODE_HTTP = 1003;

    static void checkHandler() {
        try {
            if (mHandler == null) {
                mHandler = new Handler();
            }
        } catch (Exception e) {
            mHandler = null;
        }
    }

    private static String concatParams(NameValuePair[] params) {
        if (params == null || params.length <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            NameValuePair param = params[i];
            if (i == 0) {
                sb.append("?");
                sb.append(URLEncoder.encode(param.getName()) + "="
                        + URLEncoder.encode(param.getValue()));
            } else {
                sb.append("&");
                sb.append(URLEncoder.encode(param.getName()) + "="
                        + URLEncoder.encode(param.getValue()));
            }
        }
        return sb.toString();
    }

    public static KWHttpRequest requestWithURL(Context context, String baseUrl,
                                               NameValuePair... params) {
        String url = baseUrl + concatParams(params);
        KWHttpRequest request = new KWHttpRequest(context, url);
        return request;
    }

    public static KWHttpRequest requestWithURL(Context context, String url) {
        KWHttpRequest request = new KWHttpRequest(context, url);
        return request;
    }

    public KWHttpRequest(Context context, String url) {
        this.mContext = context;
        setURL(url);
        postData.clear();
        requestsMap.clear();
        uploadFile.clear();
    }

    public KWHttpRequest(Context context) {
        this.mContext = context;
        postData.clear();
        requestsMap.clear();
        headers.clear();
        uploadFile.clear();
    }

    public void setURL(String url) {
        strURL = url;
    }

    public void setmP(String p) {
        mP = p;
    }

    public void setFilePath(String filePath, FILETYPE filetype) {
        mFilePath = filePath;
        this.fileType = filetype;
    }

    public enum FILETYPE {
        VIDEO, IMG, MUSIC, NONE
    }

    private FILETYPE fileType = FILETYPE.IMG;


    //
    public void setHeaderValueForKey(String key, String value) {
        headers.put(key, value);
    }

    // POST
    public void setPostValueForKey(String key, String value) {
        postData.put(key, value);
    }

    public void addDownload(PcsPackUp upPack) {
        requestsMap.put(upPack.getName(), upPack);
    }

    public void setUploadFileForKey(String key, String value) {
        uploadFile.put(key, value);
    }

    public void setHeaders(HttpUriRequest request) {
        if (headers.size() <= 0) {
            return;
        }

        Set<String> headerKey = headers.keySet();
        for (String key : headerKey) {
            String value = headers.get(key);
            request.addHeader(key, value);
        }
        request.setHeader("User-Agent",
                System.getProperties().getProperty("http.agent")
                        + " SurfingClub");
    }

    public String encodeParameters(HashMap<String, String> postParams) {
        if (postParams.size() <= 0) {
            return "";
        }

        StringBuilder buf = new StringBuilder();
        int j = 0;
        Set<String> keyset = postParams.keySet();
        for (String key : keyset) {
            String value = postParams.get(key);
            if (j != 0) {
                buf.append("&");
            }
            try {
                buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(URLEncoder.encode(value, "UTF-8"));
            } catch (java.io.UnsupportedEncodingException neverHappen) {
            }
            j++;
        }
        return buf.toString();
    }

    private HttpResponse requestHttp() {
        HttpResponse response = null;
        try {
            HttpUriRequest request = null;
            ByteArrayOutputStream bos = null;
            HttpClient client = new DefaultHttpClient();

            // if (uploadFile.size() > 0)
            if (!TextUtils.isEmpty(mFilePath)) {
                HttpPost httpPost = new HttpPost(strURL);
                request = httpPost;
                MultipartEntity mpEntity = new MultipartEntity();
                StringBody stringBody = null;
                FileBody fileBody;
                FormBodyPart fbp;
                File targetFile;
                // 上传字符串
                String upStr = null;
                upStr = getUpJson();
                if (upStr != null) {
                    Log.e("jzy", "up_json" + upStr);
                    stringBody = new StringBody(upStr, Charset.forName("UTF-8"));
                }
                System.out.println("mFilePath-->" + mFilePath);

                targetFile = new File(mFilePath);
                fileBody = new FileBody(targetFile, targetFile.getName(), "application/octet-stream", null);
//				fileBody = new FileBody(targetFile, "application/octet-stream");
                mpEntity.addPart("file", fileBody);

                if (fileType == FILETYPE.VIDEO) {
                    try {
                        MediaMetadataRetriever media = new MediaMetadataRetriever();
                        media.setDataSource(mFilePath);
                        Bitmap bitmap = media.getFrameAtTime();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        String str = targetFile.getName();
                        str = str.substring(0, str.indexOf("."));
                        String filePath = PcsGetPathValue.getInstance().getMyPhotoPath() + str + ".png";
                        File targetFile2 = new File(filePath);
                        if(!targetFile2.exists()){
                            saveBitmap(bitmap, targetFile2);
                        }
//						byte[] byteArray = stream.toByteArray();
//						InputStreamBody imBody=new InputStreamBody(new ByteArrayInputStream(byteArray), targetFile.getName());
                        FileBody fileBody2 = new FileBody(targetFile2, str + ".png", "application/octet-stream", null);
                        mpEntity.addPart("imgData", fileBody2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mpEntity.addPart("p", stringBody);

                // Set<String> keyset = uploadFile.keySet();
                //String tempkey = "";
                // for (String key : keyset) {
                // tempkey =key;
                // filePath = uploadFile.get(key);
                // targetFile = new File(filePath);
                // fileBody = new FileBody(targetFile,
                // "application/octet-stream");
                // mpEntity.addPart(key, fileBody);
                // mpEntity.addPart("p",stringBody);
                // }
                httpPost.setEntity(mpEntity);
                setHeaders(request);
                response = client.execute(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestsMap.clear();
        return response;
    }


    public void saveBitmap(Bitmap bm, File file) {
        Log.e(TAG, "保存图片");
        if(file == null) {
            return;
        }
        if (file.exists()) {
            file.delete();
        }
        File rootFile = file.getParentFile();
        if(rootFile != null && !rootFile.exists()) {
            if(!rootFile.mkdirs()) {
                return;
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理下载Json
     *
     * @param json
     * @throws JSONException
     */
    private void procDownJson(String json) throws JSONException {
        String key = null;
        JSONObject jsonObject = new JSONObject(json);
        // Head
        JSONObject h = jsonObject.getJSONObject("h");
        int is = h.getInt("is");
        if (is < 0) {
            String error = h.getString("error");
            PcsDataBrocastReceiver.sendBroadcast(context, key, error);
            com.pcs.lib.lib_pcs_v3.control.log.Log.e("NetError", error);
            return;
        }
        // Body
        JSONObject b = jsonObject.getJSONObject("b");
        //PcsDataManager.getInstance().seriesDataBegin();
        // 保存
        SQLiteDatabase db = null;
        synchronized (PcsDataManager.mLock) {
            try {
                Context context = PcsInit.getInstance().getContext();
                if (context == null) {
                    com.pcs.lib.lib_pcs_v3.control.log.Log.e("jzy", "数据库，procDownJson，context=null");
                }
                db = DBHelper.getInstance(context).getReadableDatabase();
                Iterator it = b.keys();

                if (it.hasNext()) {
                    key = (String) it.next();
                }
                JSONObject obj = b.getJSONObject(key);
                obj.put("updateMill", System.currentTimeMillis());
                PcsDataManager.getInstance().seriesSaveData(db, key,
                        obj.toString());
                PcsDataBrocastReceiver.sendBroadcast(context, key, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
    }

    /**
     * 获取完整JSON
     *
     * @return
     * @throws JSONException
     */
    private String getUpJson() {
        // 当前系统毫秒
        long currMill = System.currentTimeMillis();
        // json
        JSONObject json = new JSONObject();
        // head
        JSONObject head = new JSONObject();
        // 计数
        int count = 0;

        String key = null;
        try {
            head.put("p", mP);
            json.put("h", head);

            // body
            JSONObject body = new JSONObject();
            // 遍历
            Set<String> keys = requestsMap.keySet();
            Iterator<String> it = keys.iterator();
            for (; it.hasNext(); ) {
                key = it.next();
                PcsPackUp pack = requestsMap.get(key);

                body.put(pack.getName(), pack.toJSONObject());
                // 计数
                count++;
            }
            json.put("b", body);
        } catch (JSONException e) {
            // 错误广播
            PcsDataBrocastReceiver.sendBroadcast(mContext, key, e.getMessage());
            e.printStackTrace();
        }

        if (count > 0) {
            return json.toString();
        }

        return null;
    }

    public InputStream startSynchronous() {
        InputStream is = null;
        HttpResponse response = requestHttp();
        if (response == null) {
            return null;
        }

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) // OK
            {
                is = new BufferedInputStream(response.getEntity().getContent());
                return is;
            }
        } catch (Exception e) {
        }
        return is;
    }

    public String startSyncRequestString() {
        InputStream is = startSynchronous();
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
        } catch (Exception e) {
        }
        return baos.toString();
    }

    public Bitmap startSyncRequestBitmap() {
        InputStream is = startSynchronous();
        if (is == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    public void setListener(int nThreadID, KwHttpRequestListener callBack) {
        this.callBack = callBack;
        this.nThreadID = nThreadID;
    }

    private void loadFinished(final int nThreadID, final byte[] b) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callBack != null) {
                        callBack.loadFinished(nThreadID, b);
                    }
                }
            });
        } else {
            if (callBack != null) {
                callBack.loadFinished(nThreadID, b);
            }
        }
    }

    private void loadFailed(final int nThreadID, final int nErrorCode) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callBack != null) {
                        callBack.loadFailed(nThreadID, nErrorCode);
                    }
                }
            });
        } else {
            if (callBack != null) {
                callBack.loadFailed(nThreadID, nErrorCode);
            }
        }
    }

    // 鐎殿喖鍊归鐐垫嫚鐠囨彃绲块柡浣哄瀹擄拷
    public void startAsynchronous() {
        checkHandler();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpResponse response = requestHttp();
//				if (callBack == null) {
//					return;
//				}

                if (response == null) {
                    loadFailed(nThreadID, ERRCODE_NETWORK);
                    return;
                }

                try {
                    InputStream is = null;
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        byte bResult[] = read(response);
                        loadFinished(nThreadID, bResult);
                        String result = new String(bResult);
                        procDownJson(result);
                    } else {
                        String result = new String(read(response));
                        String err = null;
                        int errCode = statusCode;
                        try {
                            // System.out.println(result);
                            JSONObject json = new JSONObject(result);
                            err = json.getString("error");
                            errCode = json.getInt("error_code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadFailed(nThreadID, errCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loadFailed(nThreadID, ERRCODE_READ);
                    // LogUtil.e(new Throwable().getStackTrace()[0].toString() +
                    // " Exception ", e);
                }
            }
        });
    }


    private byte[] read(HttpResponse response) throws Exception {
        // String result = "";
        HttpEntity entity = response.getEntity();
        InputStream inputStream;
        try {
            inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            Header header = response.getFirstHeader("Content-Encoding");
            if (header != null
                    && header.getValue().toLowerCase().indexOf("gzip") > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }

            // Read response into a buffered stream
            int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }

            // result = new String(content.toByteArray());
            return content.toByteArray();
        } catch (IllegalStateException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    public static void shutdown() {

        // executor.shutdownNow();
    }

    public interface KwHttpRequestListener {
        public void loadFinished(int nThreadID, byte[] b);

        public void loadFailed(int nThreadID, int nErrorCode);
    }

}
