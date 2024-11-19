/*
 * Copyright (C) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.askerov.dynamicgrid;

import ohos.agp.colors.RgbColor;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Text;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.agp.window.service.DisplayAttributes;
import ohos.agp.window.service.DisplayManager;
import ohos.app.Context;

/**
 * Toast工具类
 *
 * @since 2021-05-15
 */
public class Toast {
    public static final int LENGTH_LONG = 4000;
    public static final int LENGTH_SHORT = 2000;
    public enum ToastLayout {
        DEFAULT,
        CENTER,
        TOP,
        BOTTOM,
    }
    public static void showShort(Context mContext, String content) {
        createTost(mContext, content, LENGTH_SHORT, ToastLayout.DEFAULT);
    }
    public static void showLong(Context mContext, String content) {
        createTost(mContext, content, LENGTH_LONG, ToastLayout.DEFAULT);
    }
    public static void show(Context mContext, String content) {
        createTost(mContext, content, LENGTH_SHORT, ToastLayout.DEFAULT);
    }
    public static void show(Context mContext, String content, int duration) {
        createTost(mContext, content, duration, ToastLayout.DEFAULT);
    }
    public static void show(Context mContext, String content, ToastLayout layout) {
        createTost(mContext, content, LENGTH_SHORT, layout);
    }
    public static void show(Context mContext, String content, int duration, ToastLayout layout) {
        createTost(mContext, content, duration, layout);
    }
    public static void showShort(Context mContext, int content) {
        createTost(mContext, getString(mContext, content), LENGTH_SHORT, ToastLayout.DEFAULT);
    }
    public static void showLong(Context mContext, int content) {
        createTost(mContext, getString(mContext, content), LENGTH_LONG, ToastLayout.DEFAULT);
    }
    public static void show(Context mContext, int content) {
        createTost(mContext, getString(mContext, content), LENGTH_SHORT, ToastLayout.DEFAULT);
    }
    public static void show(Context mContext, int content, int duration) {
        createTost(mContext, getString(mContext, content), duration, ToastLayout.DEFAULT);
    }
    public static void show(Context mContext, int content, ToastLayout layout) {
        createTost(mContext, getString(mContext, content), LENGTH_SHORT, layout);
    }
    public static void show(Context mContext, int content, int duration, ToastLayout layout) {
        createTost(mContext, getString(mContext, content), duration, layout);
    }
    private static void createTost(Context mContext, String content, int duration, ToastLayout layout) {
        DirectionalLayout toastLayout = new DirectionalLayout(mContext);
        DirectionalLayout.LayoutConfig textConfig = new DirectionalLayout.LayoutConfig(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT);
        Text text = new Text(mContext);
        text.setText(content);
        text.setTextColor(new Color(Color.getIntColor("#000000")));
        text.setPadding(vp2px(mContext, 16), vp2px(mContext, 4), vp2px(mContext, 16), vp2px(mContext, 4));
        text.setTextSize(vp2px(mContext, 20));
        text.setBackground(buildDrawableByColorRadius(Color.getIntColor("#90E3E3E3"), vp2px(mContext, 20)));
        text.setLayoutConfig(textConfig);
        toastLayout.addComponent(text);
        int mLayout = LayoutAlignment.CENTER;
        switch (layout) {
            case TOP:
                mLayout = LayoutAlignment.TOP;
                break;
            case BOTTOM:
                mLayout = LayoutAlignment.BOTTOM;
                break;
            case CENTER:
                mLayout = LayoutAlignment.CENTER;
                break;
        }
        toastLayout.setMarginTop(1400);
        ToastDialog toastDialog = new ToastDialog(mContext);
        toastDialog.setComponent(toastLayout);
        toastDialog.setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT);
        toastDialog.setAlignment(mLayout);
        toastDialog.setTransparent(true);
        toastDialog.setDuration(duration);
        toastDialog.show();
    }
    private static ohos.agp.components.element.Element buildDrawableByColorRadius(int color, float radius) {
        ShapeElement drawable = new ShapeElement();
        drawable.setShape(0);
        drawable.setRgbColor(RgbColor.fromArgbInt(color));
        drawable.setCornerRadius(radius);
        return drawable;
    }
    private static String getString(Context mContent, int resId) {
        try {
            return mContent.getResourceManager().getElement(resId).getString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static int vp2px(Context context, float vp) {
        DisplayAttributes attributes = DisplayManager.getInstance().getDefaultDisplay(context).get().getAttributes();
        return (int) (attributes.densityPixels * vp);
    }
}
