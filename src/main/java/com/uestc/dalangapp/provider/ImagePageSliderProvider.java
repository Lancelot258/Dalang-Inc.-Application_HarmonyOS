package com.uestc.dalangapp.provider;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.PageSliderProvider;
import ohos.org.w3c.dom.ls.LSInput;

import java.util.List;

public class ImagePageSliderProvider extends PageSliderProvider {
    private AbilitySlice abilitySlice;
    private List<Image> listData;

    public ImagePageSliderProvider() {
    }

    public ImagePageSliderProvider(AbilitySlice abilitySlice, List<Image> listData) {
        this.abilitySlice = abilitySlice;
        this.listData = listData;
    }
    public ImagePageSliderProvider(AbilitySlice abilitySlice)
    {
        this.abilitySlice=abilitySlice;
    }
    public void SetData(List<Image> listData)
    {
        this.listData=listData;
    }
    public ImagePageSliderProvider(List<Image> listData)
    {
        this.listData= listData;
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        Object obj=listData.get(i);
        componentContainer.addComponent((Component) obj);
        return obj;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        componentContainer.removeComponent(listData.get(i));
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o)
    {
        return component == o;
    }
}
