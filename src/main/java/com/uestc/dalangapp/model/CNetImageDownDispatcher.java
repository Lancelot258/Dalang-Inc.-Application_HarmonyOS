package com.uestc.dalangapp.model;

import com.bumptech.glide.request.RequestListener;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.agp.components.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class CNetImageDownDispatcher extends Thread{
    private EventHandler handler = new EventHandler(EventRunner.getMainEventRunner());//用来更新ui的handler

    private LinkedBlockingQueue<CNetImageDownRequest > mRequestQueue;//存放请求的集合

    public CNetImageDownDispatcher(LinkedBlockingQueue<CNetImageDownRequest> queue) {
        this.mRequestQueue = queue;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if (mRequestQueue == null)
                continue;
            try {
                CNetImageDownRequest request = mRequestQueue.take();
                if (request == null)
                    continue;
                showLoadingImg(request);//1.设置占位图片
                PixelMap pixelMap = findPixelMap(request);//2.加载图片数据
                showImageView(request, pixelMap);//3.将图片显示到Image
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置默认图
     *
     * @param request
     */
    private void showLoadingImg(CNetImageDownRequest request) {
        Image image = request.getImage().get();
        int resId = request.getResId();
        if (image != null && resId != 0) {
            handler.postTask(() -> {
                image.setPixelMap(request.getResId());
            });
        }
    }

    /**
     * 查找PixelMap的逻辑（包含三级缓存）
     *
     * @param request
     * @return
     */
    private PixelMap findPixelMap(CNetImageDownRequest request) {
        //1.先在缓存中查找 todo
        //2.没有再从网络上下载
        PixelMap pixelMap = downloadPixelMap(request);
        //3.下载完毕返回并缓存 todo
        return pixelMap;
    }

    /**
     * 根据url下载PixelMap
     *
     * @param request
     * @return
     */
    private PixelMap downloadPixelMap(CNetImageDownRequest request) {
        InputStream is = null;
        PixelMap pixelMap = null;
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = urlConnection.getInputStream();
            ImageSource imageSource = ImageSource.create(is, new ImageSource.SourceOptions());
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            decodingOptions.desiredPixelFormat = PixelFormat.RGB_565;
            pixelMap = imageSource.createPixelmap(decodingOptions);
            return pixelMap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将结果设置到控件上
     *
     * @param request
     * @param pixelMap
     */
    private void showImageView(CNetImageDownRequest request, PixelMap pixelMap) {
        Image image = request.getImage().get();
        RequestListener listener = request.getListener();
        if (image != null && pixelMap != null && request.getUrlMd5() != null && request.getUrlMd5().equals(image.getTag())) {
            handler.postTask(() -> {
                image.setPixelMap(pixelMap);
            });
            if (listener != null)
            {
                //listener.onSuccess(request.getUrl(), pixelMap);
            }
        } else {
            if (listener != null) {
                //listener.onFail(request.getUrl());
            }
        }
    }
}