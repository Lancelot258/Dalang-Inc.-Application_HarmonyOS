package com.uestc.dalangapp.model;


import ohos.agp.components.Image;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.net.HttpResponseCache;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;
import ohos.utils.zson.ZSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetDataTools
{

    /**
     * 请求网络图片
     */
    public Image requestNetImage(String requestUrl) {

        //String requestUrl = "https://t7.baidu.com/it/u=4162611394,4275913936&fm=193&f=GIF";
        HttpURLConnection connection = null;
        try {
            connection = getHttpURLConnection(requestUrl, "POST");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //将网络请求数据转换成ImageSource
                ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
                ImageSource imageSource = ImageSource.create(connection.getInputStream(), srcOpts);
                //将ImageSource转换成可以设置给image控件的PixelMap对象
                PixelMap pixelMap = imageSource.createPixelmap(null);
                Image image=new Image(null);
                image.setPixelMap(pixelMap);
                return image;
            }
            connection.disconnect();
        } catch (Exception e) {
        }
        return null;
    }
    private HttpURLConnection getHttpURLConnection(String requestURL, String requestMethod) throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置连接超时时间
        connection.setConnectTimeout(10 * 1000);
        //设置读取服务器数据超时时间
        connection.setReadTimeout(15 * 1000);
        //设置请求方式
        connection.setRequestMethod(requestMethod);
        return connection;
    }
}