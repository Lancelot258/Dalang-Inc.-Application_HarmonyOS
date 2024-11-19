package com.uestc.dalangapp.provider;

import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.agp.components.Image;
import ohos.app.Context;
import ohos.data.resultset.ResultSet;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.List;

public class CReadPhotAlbum {
    private List<Image> listImage;
    public  List<Image>  GetImages()
    {
        return  listImage;
    }
    /**
     * 选择系统相册图片
     */
    public void pickFromGallery(Context ctx) {
        DataAbilityHelper helper = DataAbilityHelper.creator(ctx);
        try {
            // columns为null则查询记录所有字段，当前例子表示查询id字段
            ResultSet resultSet = helper.query(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, new String[]{AVStorage.Images.Media.ID},
                    null);
            while (resultSet != null && resultSet.goToNextRow()) {
                PixelMap pixelMap = null;
                ImageSource imageSource = null;
                Image image = new Image(ctx);
                // 获取id字段的值
                int id = resultSet.getInt(resultSet.getColumnIndexForName(AVStorage.Images.Media.ID));
                Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, String.valueOf(id));
                FileDescriptor fd = helper.openFile(uri, "r");
                ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
                try {
                    imageSource = ImageSource.create(fd, null);
                    pixelMap = imageSource.createPixelmap(null);
                    int height = pixelMap.getImageInfo().size.height;
                    int width = pixelMap.getImageInfo().size.width;
                    float sampleFactor = Math.max(height /250f, width/250f);
                    decodingOptions.desiredSize = new Size((int) (width/sampleFactor), (int)(height/sampleFactor));
                    pixelMap = imageSource.createPixelmap(decodingOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (imageSource != null) {
                        imageSource.release();
                    }
                }
                image.setPixelMap(pixelMap);
                listImage.add(image);
            }
        } catch (DataAbilityRemoteException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
