package com.uestc.dalangapp;

import com.uestc.dalangapp.slice.MainAbilitySlice;
import com.uestc.dalangapp.slice.ShowPicsSlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        //
        super.addActionRoute("picSlice", ShowPicsSlice.class.getName());


    }
}
