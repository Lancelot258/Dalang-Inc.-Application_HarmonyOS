package com.uestc.dalangapp.provider;

import com.uestc.dalangapp.model.OperToolImagFile;
import com.uestc.dalangapp.model.Page;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ManagePageSlider  extends PageSliderProvider {
    private List<Page> listData;
    private AbilitySlice abilitySlice;

    public ManagePageSlider(List<Page> listData,AbilitySlice abilitySlice)
    {
        this.listData=listData;
        this.abilitySlice=abilitySlice;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        Page page=listData.get(i);
        Image image= null;
        try {
            image = OperToolImagFile.ReadImageToFile(page.getName(),1280,800);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建层布局
        StackLayout sl=new StackLayout(abilitySlice);
        StackLayout.LayoutConfig layoutConfig=new StackLayout.LayoutConfig(StackLayout.LayoutConfig.MATCH_PARENT,
                StackLayout.LayoutConfig.MATCH_PARENT);
        sl.setLayoutConfig(layoutConfig);
        //将图片和文本组件添加至层布局
        sl.addComponent(image,layoutConfig);
        //将层布局放入滑页组件中
        componentContainer.addComponent(sl);
        return null;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {

    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return false;
    }
}
