package com.uestc.dalangapp.provider;

import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.model.OperToolImagFile;
import com.uestc.dalangapp.model.Page;
import com.uestc.dalangapp.slice.MainAbilitySlice;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.global.resource.Resource;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import org.askerov.dynamicgrid.GridItemInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class PicShowPageProvider extends PageSliderProvider implements Component.ClickedListener {
    private List<GridItemInfo> pagesList;
    private AbilitySlice abilitySlice;
    private String backImageFileName;
    private OperToolImagFile imagFile=null;

    public PicShowPageProvider(List<GridItemInfo> pages, AbilitySlice abilitySlice) {
        this.pagesList = pages;
        this.abilitySlice=abilitySlice;
        imagFile=new OperToolImagFile();
    }

    public void SetBackIconFileName(String fileName)
    {
        backImageFileName=fileName;
    }

    @Override
    public int getCount() {
        return pagesList.size();
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        GridItemInfo page=pagesList.get(i);
        //创建层布局
        //StackLayout s0 = (StackLayout) abilitySlice.findComponentById(ResourceTable.Id_pic_show_page_ability);
        //StackLayout sl=new StackLayout(abilitySlice);
        //sl.setLayoutConfig(s0.getLayoutConfig());
        StackLayout sl=new StackLayout(abilitySlice);
        StackLayout.LayoutConfig layoutConfig=new StackLayout.LayoutConfig(StackLayout.LayoutConfig.MATCH_PARENT,
                StackLayout.LayoutConfig.MATCH_PARENT);
        sl.setLayoutConfig(layoutConfig);
        //将图片和文本组件添加至层布局
        Image image= new Image(sl.getContext());
        try {
            PixelMap pixelMap=imagFile.ReadPixelMapFile(page.getFileName());
            if(pixelMap==null){
                //miss picture
                pixelMap=imagFile.ReadPixelMapFile(backImageFileName);
            }
            image.setPixelMap(pixelMap);
            image.setHeight(1600);
            image.setWidth(2560);
            image.setScaleMode(Image.ScaleMode.STRETCH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DirectionalLayout.LayoutConfig layoutConfigImage=new DirectionalLayout.LayoutConfig(DirectionalLayout.LayoutConfig.MATCH_PARENT,
                DirectionalLayout.LayoutConfig.MATCH_PARENT);
        layoutConfigImage.setMarginLeft(-5); layoutConfigImage.setMarginRight(-5);
        layoutConfigImage.setMarginTop(-5);  layoutConfigImage.setMarginBottom(-5);
        sl.addComponent(image,layoutConfigImage);
        //sl.addComponent(image);
        //add back icon
        Image backImageRes=(Image) abilitySlice.findComponentById(ResourceTable.Id_show_pic_back);// new Image(sl.getContext());
        Image backImage=new Image(sl.getContext());
        backImage.setPixelMap(backImageRes.getPixelMap());
//        try {
//            backImage.setPixelMap(imagFile.ReadPixelMapFile(backImageFileName));
//            backImage.setScaleMode(Image.ScaleMode.STRETCH);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        DirectionalLayout.LayoutConfig layoutConfig1=new DirectionalLayout.LayoutConfig();
        layoutConfig1.alignment= LayoutAlignment.LEFT|LayoutAlignment.TOP;
        layoutConfig1.setMarginTop(20);
        layoutConfig1.setMarginLeft(10);
        layoutConfig1.width=72;
        layoutConfig1.height=72;
        sl.addComponent(backImage,layoutConfig1);
        backImage.setClickedListener(component ->
        {
            abilitySlice.present(new MainAbilitySlice(),new Intent());
        });

        //将层布局放入滑页组件中
        componentContainer.addComponent(sl);
        return sl;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        //滑出屏幕的组件进行移除
        componentContainer.removeComponent((Component) o);
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        //判断滑页上的每一页的组件和内容是否保持一致
        return true;
    }

    @Override
    public void onClick(Component component) {

    }
}
