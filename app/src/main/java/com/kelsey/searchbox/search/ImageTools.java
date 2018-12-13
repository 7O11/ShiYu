package com.kelsey.searchbox.search;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/* * 获取网上的图片 */
public class ImageTools {
    private static  Handler handler=new Handler();

    public void getIcon(String path, ViewHolder viewHolder) {
            new Thread(new MyThread(path, viewHolder)).start();
    }

    public class MyThread implements Runnable {

        private String path;
        Bitmap bitmap = null;
        ViewHolder vHolder;

        MyThread(String p, ViewHolder viewHolder){
            this.path = p;
            this.vHolder = viewHolder;
        }

        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(path);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;//打开连接
            try {
                connection = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            connection.setConnectTimeout(5 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            try {
                connection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream stream = null;//获取输输入流
            try {
                stream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(stream);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // 在Post中操作UI组件ImageView
                    vHolder.vedioPicInfo.setImageBitmap(bitmap);
                }
            });
        }

    }
}