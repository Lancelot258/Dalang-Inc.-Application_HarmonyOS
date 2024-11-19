package com.uestc.dalangapp.slice;

import  com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.provider.ImagePageSliderProvider;
import com.uestc.dalangapp.provider.TablePageSliderProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.service.DisplayAttributes;
import ohos.agp.window.service.DisplayManager;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.*;
import java.util.*;

public class ShowPicsSlice extends AbilitySlice implements Component.ClickedListener   {
    Button btnBack;
    Map<Integer, List<Image> > m_mpData=new HashMap<>();
    PageSlider m_pageslider=null;
    TableLayout m_tblLayout=null;
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
    public  void  onStart(Intent intent)
    {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ShowPicsSlice);
        //从意图对象中加载参数并赋值到index中
        IntentParams params = intent.getParams();
        int idc= (int) params.getParam("slider_choose");
        if(idc==m_curID) return;

        if(m_curID==-1) //initial
        {
            //add picture data to slider
            DisplayAttributes displayAttributes = DisplayManager.getInstance().getDefaultDisplay(this.getContext()).get().getAttributes();
            widthScreen=displayAttributes.width;
            heighScreen=displayAttributes.height;
            //page slider
            m_pageslider = (PageSlider) findComponentById(ResourceTable.Id_pageslider_showpic_slice);
            //slider provider
            List<Integer> layoutFileIds = new ArrayList<>();
            layoutFileIds.add(ResourceTable.Layout_ability_show_pictures);
            m_pageslider.setProvider(new TablePageSliderProvider(layoutFileIds,this));
            m_tblLayout = (TableLayout) findComponentById(ResourceTable.Id_pictures_tablayout);
            m_tblLayout.setFadeEffectBoundaryWidth(10);

            //Affection of page slider
            m_pageslider.setSlidingPossible(false);


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
        //clear current display component
        while(true)
        {
            Component component=m_tblLayout.getComponentAt(0);
            if(component==null) break;
            component.release();
            m_tblLayout.removeComponentAt(0);
        }
        //display images
        List<Image> listData=m_mpData.get(idc);
        for (int idx=0;idx< listData.size();idx++)
        {
            Image image=listData.get(idx);
            StackLayout newStack=new StackLayout(m_tblLayout.getContext());
            //add data image
            StackLayout.LayoutConfig config0 = new StackLayout.LayoutConfig(newStack.getLayoutConfig());
            config0.alignment= LayoutAlignment.CENTER;
            config0.height=heighScreen;            config0.width=widthScreen;
            newStack.addComponent(image,0,config0);
            //add back image
            Image closeImage=new Image(newStack.getContext());
            closeImage.setPixelMap(ResourceTable.Media_back);
            closeImage.setHeight(100);
            closeImage.setWidth(140);
            closeImage.setScaleMode(Image.ScaleMode.STRETCH);
            //set layout of close image
            StackLayout.LayoutConfig config1 = new StackLayout.LayoutConfig(newStack.getLayoutConfig());
            config1.alignment= LayoutAlignment.LEFT|LayoutAlignment.TOP;
            config1.height=100;            config1.width=140;
            newStack.addComponent(closeImage,1,config1);
            newStack.setClickedListener(component -> {
                Intent intMain=new Intent();
                intMain.setParam("category",m_curID);
                present(mainSlice,intMain);
            });
            m_tblLayout.addComponent(newStack,idx);
        }

     //PageSlider  slider= (PageSlider) findComponentById(ResourceTable.Id_pageslider_showpic_slice);
    // PageSliderProvider sliderProvider=new ImagePageSliderProvider(flag,this);
    //set slider
    // slider.setProvider(sliderProvider);
//        List<Image> list = getData();
//        PageSliderProvider provider = new ImagePageSliderProvider(list);
//        pageslider.setProvider(provider);
}
    @Override
    public void onClick(Component component) {
        if (component==btnBack)
        {
            Intent intent = new Intent();
            present(new MainAbilitySlice(),intent);
            // terminate();//销毁当前slice &&————>>“terminateAbility”销毁当前Ability
        }
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
}
