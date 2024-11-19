package com.uestc.dalangapp.model;

import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.InputStream;

public class NetImageUtils {
    public static PixelMap createPixelMap(String imageUrl) {
        InputStream inputStream = HttpsUtils.getInputStream(imageUrl, RequestMethod.GET.name());
        ImageSource.SourceOptions sourceOptions = new ImageSource.SourceOptions();
        sourceOptions.formatHint = "image/jpeg";
        ImageSource imageSource = ImageSource.create(inputStream,sourceOptions);
        PixelMap pixelMap = imageSource.createPixelmap(null);
        HttpsUtils.closeStream();
        return pixelMap;
    }
}
