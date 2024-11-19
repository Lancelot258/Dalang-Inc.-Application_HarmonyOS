package com.uestc.dalangapp.model;

import com.bumptech.glide.request.RequestListener;
import ohos.app.AbilityContext;
import ohos.agp.components.*;

import ohos.data.orm.util.StringUtils;

import java.lang.ref.SoftReference;

public class CNetImageDownRequest {
    /**
     * 需要下载的url
     */
    private String mUrl;

    /**
     * 关联的image控件,使用软引用持有
     */
    private SoftReference<Image> mImage;

    /**
     * 上下文对象
     */
    private AbilityContext mContext;

    /**
     * 占位图资源id
     */
    private int mResId;

    /**
     * 请求监听
     */
    private RequestListener mListener;

    /**
     * 请求标志，用于防止图片错乱，和三级缓存
     */
    private String mUrlMd5;

    public CNetImageDownRequest(AbilityContext context) {
        this.mContext = context;
    }

    /**
     * 设置下载的路径
     *
     * @param url
     * @return
     */
    public CNetImageDownRequest load(String url) {
        this.mUrl = url;
        if (!url.isEmpty()) {
            this.mUrlMd5 = MD5Utils.encrypt(url);
        }
        return this;
    }

    /**
     * 设置默认图资源
     *
     * @param resId
     * @return
     */
    public CNetImageDownRequest loading(int resId) {
        this.mResId = resId;
        return this;
    }

    /**
     * 设置请求监听
     *
     * @param listener
     * @return
     */
    public CNetImageDownRequest setListener(RequestListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 绑定关联的image控件，并将请求加入队列
     *
     * @param image
     */
    public void into(Image image) {
        image.setTag(mUrlMd5);
        this.mImage = new SoftReference<>(image);
        // 发起请求
        CNetImageDownManager.getInstance().addPixelMapRequest(this);
    }

    /**
     * 获取请求url
     *
     * @return
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 获取控件实例
     *
     * @return
     */
    public SoftReference<Image> getImage() {
        return mImage;
    }

    /**
     * 获取展位图资源id
     *
     * @return
     */
    public int getResId() {
        return mResId;
    }

    /**
     * 获取监听器实例
     *
     * @return
     */
    public RequestListener getListener() {
        return mListener;
    }

    /**
     * 获取请求标志位
     *
     * @return
     */
    public String getUrlMd5() {
        return mUrlMd5;
    }
}
