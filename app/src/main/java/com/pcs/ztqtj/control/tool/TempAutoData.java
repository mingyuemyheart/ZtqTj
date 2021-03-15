package com.pcs.ztqtj.control.tool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * JiangZy on 2016/9/22.
 * 临时自动下载数据
 */

public class TempAutoData {


        private static String upload(String urlStr, String argStr,String[] uploadFiles,String character) {
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            try {
                URL url = new URL(urlStr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                // 发送POST请求必须设置如下两行
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Charset", "UTF-8");
                con.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
                DataOutputStream ds =new DataOutputStream(con.getOutputStream());
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; name=\"p\"" + end);
                ds.writeBytes(end);
                ds.write(argStr.replace("p=", "").getBytes(character));
                ds.writeBytes(end);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
                ds.flush();

                for (int i = 0; i < uploadFiles.length; i++) {
                    //String uploadFile = uploadFiles[i];
                    File uploadFile = new File(uploadFiles[i]);
                    String filename = uploadFile.getName();
                    ds.writeBytes(twoHyphens + boundary + end);
                    ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" +  filename + "\" " + end);
                    ds.writeBytes(end);
                    FileInputStream fStream = new FileInputStream(uploadFile);
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length = -1;
                    while ((length = fStream.read(buffer)) != -1) {
                        ds.write(buffer, 0, length);
                    }
                    ds.writeBytes(end);
              /* close streams */
                    fStream.close();
                }
                ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
                ds.flush();
                ds.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),character));
                String line = null;

                StringBuffer sb = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
}
