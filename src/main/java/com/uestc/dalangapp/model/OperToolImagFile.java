package com.uestc.dalangapp.model;

import ohos.agp.components.Image;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.*;
public class OperToolImagFile {
    public static Image ReadImageToFile(String fileName,int w,int h) throws IOException {
        File file=new File(fileName);
        if(!file.exists()) return null;
        int nFileSize= (int) file.length();
        FileInputStream fileInputStream=new FileInputStream(fileName);
        if(fileInputStream!=null)
        {
            byte rdata[]=new byte[nFileSize];
            fileInputStream.read(rdata,0,nFileSize);
            fileInputStream.close();
            Image image=ByteToImage(rdata,w,h);
            return  image;
        }
        return  null;
    }
    public PixelMap ReadPixelMapFile(String fileName) throws IOException {
        File file=new File(fileName);
        if(!file.exists()) return null;
        int nFileSize= (int) file.length();
        FileInputStream fileInputStream=new FileInputStream(fileName);
        if(fileInputStream!=null)
        {
            byte rdata[]=new byte[nFileSize];
            fileInputStream.read(rdata,0,nFileSize);
            fileInputStream.close();
            PixelMap image=ByteToPixelMap(rdata);
            return  image;
        }
        return  null;
    }
    private static Image ByteToImage(byte[] bytes,int w,int h){
        // 用于 ImageSource的 create(bytes,srcOpts)方法
        ImageSource.SourceOptions srcOpts=new ImageSource.SourceOptions();
        //设置图片原格式也可以用 null
        srcOpts.formatHint="image/jpg";
        ImageSource imageSource=ImageSource.create(bytes,srcOpts);
        //通过ImageSource创建 PixelMap文件
        PixelMap pixelMap=imageSource.createPixelmap(null);
        Image image=new Image(null);
        image.setPixelMap(pixelMap);
        image.setHeight(h);
        image.setWidth(w);
        image.setScaleMode(Image.ScaleMode.STRETCH);
        return image;
    }
    private PixelMap ByteToPixelMap(byte[] bytes){
        // 用于 ImageSource的 create(bytes,srcOpts)方法
        ImageSource.SourceOptions srcOpts=new ImageSource.SourceOptions();
        //设置图片原格式也可以用 null
        srcOpts.formatHint="image/jpg";
        ImageSource imageSource=ImageSource.create(bytes,srcOpts);
        //通过ImageSource创建 PixelMap文件
        PixelMap pixelMap=imageSource.createPixelmap(null);
        return pixelMap;
    }
    public void SaveImageToFile(Image image,String fileName,int w,int h)
    {
        image.setWidth(w);
        image.setHeight(h);
        byte[] byteData = GetBytesByImage(image);
        try {
            int nFileSize=byteData.length;
            FileOutputStream fileOutputStream=new FileOutputStream(fileName);
            fileOutputStream.write(byteData,0,nFileSize);
            fileOutputStream.close();
            //
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void SavePixelMapToFile(PixelMap pixelMap,String fileName)
    {
        byte[] byteData = GetBytesByPixelMap(pixelMap);
        try {
            int nFileSize=byteData.length;
            FileOutputStream fileOutputStream=new FileOutputStream(fileName);
            fileOutputStream.write(byteData,0,nFileSize);
            fileOutputStream.close();
            //
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //将图片转换成byte[]
    private byte[] GetBytesByImage(Image image) {
        PixelMap pm = image.getPixelMap();
        ImagePacker imagePacker = ImagePacker.create();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
        imagePacker.initializePacking(byteArrayOutputStream, packingOptions);
        imagePacker.addImage(pm);
        imagePacker.finalizePacking();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
    //将图片转换成byte[]
    private byte[] GetBytesByPixelMap(PixelMap pm) {
        ImagePacker imagePacker = ImagePacker.create();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
        imagePacker.initializePacking(byteArrayOutputStream, packingOptions);
        imagePacker.addImage(pm);
        imagePacker.finalizePacking();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
}
