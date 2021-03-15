package com.pcs.ztqtj.control.loading;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.control.tool.FileUtils;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.AsyncTask;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListUp;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 初始化城市列表command
 * Created by tyaathome on 2017/3/4.
 */

public class CommandInitCityList extends AbstractCommand {

    private Context mContext;
    private PackCityListUp cityListUp = new PackCityListUp();
    private PackCityListDown.CityDBInfo mCityDBInfo = null;

    public CommandInitCityList(Context context) {
        this.mContext = context;
    }


    @Override
    public void execute() {
        super.execute();
        Log.e("jzy", "CommandInitCityList");
        PcsDataBrocastReceiver.registerReceiver(mContext,
                receiver);
        String useAssetDB = mContext.getString(R.string.use_asset_db);
        if(useAssetDB.equals("1")) {
            afterGetCityDB();
        } else {
            // 请求数据
            reqCityList();
        }
    }

    /**
     * 请求城市列表数据
     */
    private void reqCityList() {
        cityListUp = new PackCityListUp();
        cityListUp.app_type = "1";
        PcsDataDownload.addDownload(cityListUp);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getDB(mContext, mCityDBInfo);
        }
    };

    private class MyTask extends AsyncTask<Object, Object, Void> {


        @Override
        protected Void doInBackground(Object... params) {
            Context context = (Context) params[0];
            PackCityListDown.CityDBInfo info = (PackCityListDown.CityDBInfo) params[1];
            getDB(context, info);
            return null;
        }
    }

    /**
     * 初始化数据库
     * @param context
     * @param info 城市数据库更新时间
     * @return
     */
    private void getDB(final Context context, PackCityListDown.CityDBInfo info) {
        String dbPath = PcsGetPathValue.getInstance().getCityDBPath();
//        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
//        Date date = null;
//        try {
//            date = oldFormat.parse(info.pub_time);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String suffix = "";
//        if(date != null) {
//            suffix = newFormat.format(date);
//        }
        String fileName = "pcs" + info.pub_time + ".db";
        String filePath = dbPath + "/" + fileName;

        final File file = new File(filePath);
        if (!file.exists()) {
            // 删除城市数据库父文件夹
            try {
                File parentFolder = new File(PcsGetPathValue.getInstance().getCityDBPath());
                if (parentFolder.exists()) {
                    FileUtils.deleteAllFile(parentFolder);
                }
                parentFolder.mkdirs();
                file.createNewFile();
                String url = context.getResources().getString(R.string.file_download_url) + info.url;
                HttpGet httpGet = new HttpGet(url);
                // 响应
                HttpResponse response = PcsInit.getInstance().getHttpClient()
                        .execute(httpGet);
                writeFile(file, response, new FileDownListener() {
                    @Override
                    public void onDo() {
                        // 获取城市列表成功后
                        afterGetCityDB();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                afterGetCityDB();
            }
        } else {
            // 城市数据库
            String url = context.getResources().getString(R.string.file_download_url) + info.url;
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response;
            long fileSize = 0;
            // 响应
            try {
                response = PcsInit.getInstance().getHttpClient()
                        .execute(httpGet);
                fileSize = response.getEntity().getContentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File dir = new File(PcsGetPathValue.getInstance().getCityDBPath());
            for (File subFile : dir.listFiles()) {
                // 如果城市列表db的名字不对或者城市列表db的大小不对则删除该文件
                if (!subFile.getName().equals(fileName) || subFile.length() != fileSize) {
                    subFile.delete();
                }
            }
            afterGetCityDB();
        }
    }



    /**
     * 写入文件
     */
    private void writeFile(File file, HttpResponse response, FileDownListener listener) {
        long fileSize = 0;
        long netFileSize = response.getEntity().getContentLength();
        // 输入流
        InputStream in = null;
        // 输出流
        RandomAccessFile out = null;
        try {
            in = response.getEntity().getContent();
            if (in == null) {
                return;
            }
            out = new RandomAccessFile(file, "rw");
            out.seek(fileSize);
            byte buf[] = new byte[1024];
            do {
                int numread = in.read(buf);
                if (numread <= 0) {
                    break;
                }
                out.write(buf, 0, numread);
                fileSize += numread;
                // 下载成功
                if (fileSize == netFileSize) {
                    break;
                }
            } while (true);

        } catch (IllegalStateException e) {
            e.printStackTrace();
            if(listener != null) {
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(listener != null) {
            }
        } finally {
            listener.onDo();
            // 关闭输入输出
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取城市列表之后的操作
     */
    private void afterGetCityDB() {
        // 获取城市列表成功后
        ZtqCityDB.getInstance().init(mContext);
        ControlAppInit.getInstance().reqInit(mContext);
    }

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(nameStr.equals(cityListUp.getName())) {
                PcsDataBrocastReceiver.unregisterReceiver(mContext, receiver);
                PackCityListDown down = (PackCityListDown) PcsDataManager.getInstance().getNetPack(cityListUp.getName());
                if(down == null || down.info_list.size() == 0) {
                    afterGetCityDB();
                    return ;
                }

                for(PackCityListDown.CityDBInfo bean : down.info_list) {
                    if(bean.channel_id == 0) { // 决策版
                        mCityDBInfo = bean;
                        PcsInit.getInstance().getExecutorService().execute(new MyRun());
                        //MyTask task = new MyTask();
                        //task.execute(mContext, mCityDBInfo);
                        //mHandler.sendEmptyMessage(0);
                        break;
                    }
                }
            }
        }
    };

    private class MyRun implements Runnable {

        @Override
        public void run() {
            Looper.prepare();
            getDB(mContext, mCityDBInfo);
            Looper.loop();
        }
    }

    public interface FileDownListener {
        void onDo();
    }
}
