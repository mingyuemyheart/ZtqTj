package com.pcs.ztqtj.control.tool.videocompressor;

/*
* By Jorge E. Hernandez (@lalongooo) 2015
* */

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VideoFileUtils {

    private static final String TAG = "VideoFileUtils";

    public static void createVideoFolder(String root) {
        File f = new File(root);
        if(!f.exists()) {
            f.mkdirs();
        }
        f = new File(root, File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME);
        if(!f.exists()) {
            f.mkdirs();
        }
        f = new File(root, File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_COMPRESSED_VIDEOS_DIR);
        if(!f.exists()) {
            f.mkdirs();
        }
        f = new File(root, File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_TEMP_DIR);
        if(!f.exists()) {
            f.mkdirs();
        }
    }

    public static File saveTempFile(String root, Context context, String filePath) {
        // init
        createVideoFolder(root);

        Uri uri = Uri.fromFile(new File(filePath));
        String fileName = getFileName(filePath);

        File mFile = null;
        ContentResolver resolver = context.getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = resolver.openInputStream(uri);

            mFile = new File(root + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_TEMP_DIR, fileName);
            out = new FileOutputStream(mFile, false);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return mFile;
    }

    /**
     * 通过路径获得文件名称
     * @param path
     * @return
     */
    private static String getFileName(String path) {
        if(TextUtils.isEmpty(path)) {
            return null;
        }
        String[] paths = path.split("/");
        if(paths.length == 0) {
            return null;
        }
//        String[] names = paths[paths.length-1].split(".");
//        if(names.length == 0) {
//            return null;
//        }
//        if(names.length >= 2) {
//            return names[0];
//        }
//        return null;
        String name = paths[paths.length-1];
        if(name.contains(".")) {
            return name;
        }
        return null;
    }
}
