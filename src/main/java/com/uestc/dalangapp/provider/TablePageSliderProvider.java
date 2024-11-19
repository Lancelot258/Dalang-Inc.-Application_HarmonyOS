package com.uestc.dalangapp.provider;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.PageSliderProvider;
import java.util.List;

public class TablePageSliderProvider  extends  PageSliderProvider{
    private List<Integer> layoutFileIds;
    private AbilitySlice abilitySlice;


    public TablePageSliderProvider(List<Integer> layoutFileds, AbilitySlice abilitySlice) {
        this.layoutFileIds = layoutFileds;
        this.abilitySlice = abilitySlice;
    }

    @Override
    public int getCount() {
        return layoutFileIds.size();
    }


    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int i) {
        Integer id = layoutFileIds.get(i);
        Component component = LayoutScatter.getInstance(abilitySlice).parse(id, null, false);
        componentContainer.addComponent(component);
        return component;
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int i, Object o) {
        componentContainer.removeComponent((Component) o);
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object o) {
        return true;
    }
}
