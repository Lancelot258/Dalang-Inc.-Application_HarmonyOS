package com.uestc.dalangapp.slice;

import com.uestc.dalangapp.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.PageSlider;

public class ShowPicsSlice7 extends AbilitySlice implements Component.ClickedListener   {
    Button btnBack;
    @Override
    public  void  onStart(Intent intent)
    {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ShowPicsSlice7);
        //setting button
        btnBack=(Button) findComponentById(ResourceTable.Id_idBtnBack_7);
        btnBack.setClickedListener(this);
        //得到页面跳转的参数
        int index_slider=0;
        if(intent!= null)
        {
            //从意图对象中加载参数并赋值到index中
            IntentParams params = intent.getParams();
            index_slider=(int)params.getParam("slider_choose");
        }
        if(index_slider==1)
        {

        }
        else if(index_slider==2)
        {

        }
        else if(index_slider==3)
        {

        }
        else if(index_slider==4)
        {

        }
        else if(index_slider==5)
        {

        }
        else if(index_slider==6)
        {

        }
        else if(index_slider==7)
        {

        }
        else if(index_slider==8)
        {

        }
        else if(index_slider==9)
        {

        }
        PageSlider  slider= (PageSlider) findComponentById(ResourceTable.Id_pageslider_showpic_slice_7);
       // PageSliderProvider sliderProvider=new ImagePageSliderProvider(flag,this);
       //set slider
       // slider.setProvider(sliderProvider);
    }
    @Override
    public void onClick(Component component) {
        if (component==btnBack)
        {
            Intent intent = new Intent();
            present(new MainAbilitySlice(),intent);
            terminate();//销毁当前slice &&————>>“terminateAbility”销毁当前Ability
        }
    }

}
