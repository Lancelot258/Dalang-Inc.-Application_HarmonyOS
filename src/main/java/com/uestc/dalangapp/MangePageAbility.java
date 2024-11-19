package com.uestc.dalangapp;

import com.uestc.dalangapp.slice.MangePageAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.PageSlider;
import ohos.agp.components.TabList;

public class MangePageAbility extends Ability {
    private TabList m_tabList=null;
    private PageSlider m_pageslider=null;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MangePageAbilitySlice.class.getName());
        //
       // m_tabList = (TabList) findComponentById(ResourceTable.Id_manage_tab_list);
        //m_pageslider = (PageSlider) findComponentById(ResourceTable.Id_manage_page_slider);
        //


    }


}
