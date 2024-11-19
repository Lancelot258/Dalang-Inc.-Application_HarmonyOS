package com.uestc.dalangapp;

import com.uestc.dalangapp.provider.PicShowPageProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.PageSlider;
import ohos.agp.components.StackLayout;
import ohos.multimodalinput.event.MmiPoint;
import org.askerov.dynamicgrid.GridItemInfo;

import java.io.File;
import java.util.List;

public class PicShowPageAbility extends AbilitySlice{
    private PicShowPageProvider provider=null;
    private List<GridItemInfo> listPages=null;
    private  PageSlider slider=null;
    private MmiPoint prePoint;
    private int numPage=0;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        this.setUIContent(ResourceTable.Layout_PicShowPageAbility);
        //
        //add layout
        //StackLayout layout = (StackLayout) this.findComponentById(ResourceTable.Id_pic_show_page_ability);
        //slider
        slider = (PageSlider) this.findComponentById(ResourceTable.Id_show_pic_slider);
        //provider
        provider = new PicShowPageProvider(listPages, this);
        //
        //String fileName = new File(getFilesDir(), "icon_back_image.png").getPath();
        //provider.SetBackIconFileName(fileName);
        //set provider
        slider.setProvider(provider);
        //////////////
        //set initial page
        //slider.setCurrentPage(0,true);
        //set orientation
        //slider.setOrientation(Component.HORIZONTAL);
        //set the change time
        slider.setPageSwitchTime(500);
        //set the slide
        //slider.setSlidingPossible(true);
        //loop circle
        slider.setCircularModeEnabled(true);
        //add listener
//        slider.addPageChangedListener(new PageSlider.PageChangedListener() {
//            @Override
//            public void onPageSliding(int i, float v, int i1) {
//            }
//            @Override
//            public void onPageSlideStateChanged(int i) {
//            }
//            @Override
//            public void onPageChosen(int i) {
//            }
//        });
        //set page cache size
        //slider.setPageCacheSize(listPages.size());
        // 添加监听
        //this.addTouchEventListener(slider);
    }

//    /**
//     * 轮播图触摸事件
//     *
//     * @param pageSlider
//     */
//    public void addTouchEventListener(PageSlider pageSlider){
//        pageSlider.setTouchEventListener((component, touchEvent) -> {
//            MmiPoint pointerPosition;
//            int action = touchEvent.getAction();
//            if (action== touchEvent.PRIMARY_POINT_DOWN)
//            {
//                prePoint=touchEvent.getPointerPosition(0);
//            }else if (action== touchEvent.POINT_MOVE) {
//            }
//            else if (action== touchEvent.PRIMARY_POINT_UP) {
//
//                MmiPoint pointerPositionUp = touchEvent.getPointerPosition(0);
//                if(pointerPositionUp.getX()>prePoint.getX()){
//                    int pos=slider.getCurrentPage();
//                    if(pos==0){ //left
//                        provider.notifyAll();
//                        slider.setCurrentPage(numPage-1,true);
//                    }
//                }else if (pointerPositionUp.getX()<prePoint.getX())
//                {
//                    int pos=slider.getCurrentPage();
//                    if(pos==numPage-1){ //right
//                        provider.notifyAll();
//                        slider.setCurrentPage(0,true);
//                    }
//                }
//            }
//            return true;
//        });
//    }

    public void SetListPicInfo(List<GridItemInfo> list)
    {
        listPages=list;
        numPage=list.size();
    }


}
