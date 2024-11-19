package com.uestc.dalangapp.model;


import ohos.net.NetHandle;
import ohos.net.NetManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpsUtils {
    private static InputStream inputStream;
    private static HttpsURLConnection connection;

    public static InputStream getInputStream(String urlStr,String methodType) {
        NetManager netManager = NetManager.getInstance(null);
        if (!netManager.hasDefaultNet()) {
            return null;
        }
        NetHandle netHandle = netManager.getDefaultNet();
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = netHandle.openConnection(url, java.net.Proxy.NO_PROXY);
            if (urlConnection instanceof HttpsURLConnection) {
                connection = (HttpsURLConnection) urlConnection;
            }
            connection.setRequestMethod(methodType);
            connection.connect();
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static void closeStream() {
        try {
            if (inputStream == null) {
                return;
            }
            inputStream.close();
            if (connection == null) {
                return;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
