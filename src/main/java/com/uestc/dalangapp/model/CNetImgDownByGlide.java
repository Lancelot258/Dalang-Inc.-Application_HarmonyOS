package com.uestc.dalangapp.model;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.slice.MainAbilitySlice;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Button;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.app.Context;
import ohos.media.image.PixelMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class CNetImgDownByGlide{
    private String saveFileName;
    private Context context;
    /**
     * 下载到本地
     * @param context 上下文
     * @param urlstr 网络图
     */
    public boolean saveImgToLocal(Context context, String urlstr, String saveName) throws Exception {
//        //定义一个URL对象，就是你想下载的图片的URL地址
//        URL url = new URL(urlstr);
//        //打开连接
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        //设置请求方式为"GET"
//        //conn.setRequestMethod("GET");
//        //超时响应时间为10秒
//        conn.setConnectTimeout(5 * 1000);
//        //防止屏蔽程序抓取而返回403错误
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//        //通过输入流获取图片数据
//        InputStream is = conn.getInputStream();
//        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
//        byte[] data = readInputStream(is);
//        //创建一个文件对象用来保存图片，默认保存当前工程根目录，起名叫Copy.jpg
//        File imageFile = new File(saveName);
//        //创建输出流
//        FileOutputStream outStream = new FileOutputStream(imageFile);
//        //写入数据
//        outStream.write(data);
//        //关闭输出流，释放资源
//        outStream.close();
        //如果是网络图片，抠图的结果，需要先保存到本地
        saveFileName=saveName;
        this.context=context;
        Glide.with(context)
                .asBitmap()
                .load(urlstr)
                .into(new SimpleTarget<PixelMap>() {
                    @Override
                    public void onResourceReady(@NotNull PixelMap pixelMap, @Nullable Transition<? super PixelMap> transition) {
                       OperToolImagFile tools=new OperToolImagFile();
                       //Image image=new Image(null);
                       //image.setPixelMap(pixelMap);
                       //tools.SaveImageToFile(image,saveFileName,1280,800);
                        AbilitySlice slice=(AbilitySlice)context;
                        Text txt=(Text)slice.findComponentById(ResourceTable.Id_txt_show_down_filename);
                        txt.setText(saveFileName);
                        txt.invalidate();
                        tools.SavePixelMapToFile(pixelMap,saveFileName);
                    }
                });
        return true;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[6024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
