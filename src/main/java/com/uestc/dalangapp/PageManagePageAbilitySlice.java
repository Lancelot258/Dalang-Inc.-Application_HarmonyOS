package com.uestc.dalangapp;

import com.uestc.dalangapp.slice.PageManagePageAbilitySliceSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class PageManagePageAbilitySlice extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(PageManagePageAbilitySliceSlice.class.getName());
    }
}
