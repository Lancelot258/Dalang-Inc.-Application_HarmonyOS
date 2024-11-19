package com.uestc.dalangapp;

import com.uestc.dalangapp.slice.PageCategoryManageAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class PageCategoryManageAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(PageCategoryManageAbilitySlice.class.getName());
    }
}
