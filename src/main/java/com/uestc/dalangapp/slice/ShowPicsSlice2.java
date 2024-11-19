package com.uestc.dalangapp.slice;

import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.provider.ImagePageSliderProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowPicsSlice2 extends AbilitySlice implements Component.ClickedListener   {
    Button button_shouye;
    Map<Integer, List<Image> > m_mpData=new HashMap<>();
    PageSlider pageslider;
    ImagePageSliderProvider provider;
    int m_curID=-1;
    //
    Map<Integer, List<String>> m_mpCategoryFileName=null;
    int widthScreen,heighScreen;
    MainAbilitySlice mainSlice=null;
    public void SetMainSlice(AbilitySlice slice)
    {
        mainSlice=(MainAbilitySlice)slice;
    }

    private void ReadCategoryFileName()
    {
        if(m_mpCategoryFileName!=null) return;
        //save file
        String name="categoryDataConfig.txt";
        String fileName=new File(getFilesDir(),name).getPath();
        try {
            File file=new File(fileName);
            if(!file.exists()) return;
            if(m_mpCategoryFileName==null) m_mpCategoryFileName=new HashMap<>();
            m_mpCategoryFileName.clear();
            StringBuilder result = new StringBuilder();
            BufferedReader br=new BufferedReader(new FileReader(file));
            String str=null;
            while ((str=br.readLine())!=null)
            {
                if(str.isEmpty()) continue;
                String strUnit[]=str.split(":");
                int idCatory=Integer.parseInt(strUnit[0]);
                String nameArr[]=strUnit[1].split(";");
                List<String> listData=new ArrayList<>();
                for (int i=0;i<nameArr.length;i++)
                {
                    if(nameArr[i].isEmpty()) continue;
                    listData.add(i,nameArr[i]);
                }
                m_mpCategoryFileName.put(idCatory,listData);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ReadCategoryImages(int idc) throws IOException {
        if(m_mpData.get(idc)!=null) return;
        List<String> listName=m_mpCategoryFileName.get(idc);
        List<Image> listData=new ArrayList<>();
        for (int n=0;n<listName.size();n++)
        {
            Image image=ReadImageToFile(listName.get(n));
            listData.add(n,image);
        }
        m_mpData.put(idc, listData);
    }
    public Image ByteToPixelMap(byte[] bytes){
        // 用于 ImageSource的 create(bytes,srcOpts)方法
        ImageSource.SourceOptions srcOpts=new ImageSource.SourceOptions();
        //设置图片原格式也可以用 null
        srcOpts.formatHint="image/jpg";
        ImageSource imageSource=ImageSource.create(bytes,srcOpts);
        //通过ImageSource创建 PixelMap文件
        PixelMap pixelMap=imageSource.createPixelmap(null);
        Image image=new Image(getContext());
        image.setPixelMap(pixelMap);
        image.setHeight(heighScreen);
        image.setWidth(widthScreen);
        image.setScaleMode(Image.ScaleMode.STRETCH);
        return image;
    }
    private Image ReadImageToFile(String fileName) throws IOException {
        File file=new File(fileName);
        if(!file.exists()) return null;
        int nFileSize=(int)file.length();
        FileInputStream fileInputStream=new FileInputStream(fileName);
        if(fileInputStream!=null)
        {
            byte rdata[]=new byte[nFileSize];
            fileInputStream.read(rdata,0,nFileSize);
            fileInputStream.close();
            Image image=ByteToPixelMap(rdata);
            return  image;
        }
        return  null;
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ShowPicsSlice2);
        //
        IntentParams params = intent.getParams();
        int idc= (int) params.getParam("slider_choose");
        if(idc==m_curID) return;
        if(m_curID==-1) //initial
        {
            pageslider = (PageSlider) findComponentById(ResourceTable.Id_pageslider_showpic_slice_2);
            button_shouye = (Button) findComponentById(ResourceTable.Id_idBtnBack_2);
            button_shouye.setClickedListener(this);
            provider = new ImagePageSliderProvider(this);
        }
        m_curID=idc;
        try {
            //read file names of category
            ReadCategoryFileName();
            //read image file of category
            ReadCategoryImages(idc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //display images
        List<Image> listData=m_mpData.get(idc);
        pageslider.setCurrentPage(1);
        //渲染图片到pageslider
        provider.SetData(listData);
        pageslider.setProvider(provider);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void onClick(Component component) {
        if(component==button_shouye)
        {
            Intent i = new Intent();
            present(new MainAbilitySlice(),i);
        }
    }


}
