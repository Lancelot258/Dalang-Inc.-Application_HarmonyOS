package com.uestc.dalangapp.slice;


import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.model.CSaveDataInoFIle;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.agp.components.TabList;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;
import org.askerov.dynamicgrid.DragGridView;
import org.askerov.dynamicgrid.GridItemInfo;


import ohos.aafwk.content.Intent;
import ohos.multimodalinput.event.KeyEvent;
import ohos.aafwk.ability.Ability;
import org.askerov.dynamicgrid.OperToolImagFile;

import java.io.*;
import java.util.*;

import static java.lang.Math.max;

public class MangePageAbilitySlice extends AbilitySlice {
    OperToolImagFile OperFile=new OperToolImagFile();
    TabList tabList=null;
    private DragGridView gridView;
    private final int imgRequestCode=1123;
    //
    int m_curIDCategory=-1;
    List<GridItemInfo> dataItemList;
    Map<Integer, List<GridItemInfo> > m_mpAllData=null;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_MangePageAbility);
        //read the data sets
        m_mpAllData=CSaveDataInoFIle.ReadCategoryFileName(this);
        //
        gridView = (DragGridView) findComponentById(ResourceTable.Id_manage_data_gridView);
        //
        gridView.SetFatherAbility(this);
        //tab list
        tabList = (TabList) findComponentById(ResourceTable.Id_tab_mange_data_list);
        //tab
        String[] tabListTags={"安放上品","添寿上品","鲜花装饰","礼仪安放","法事项目","大朗风水","人像雕塑","相关服务","墓型展示","exit"};
        if(tabList.getTabCount()<9) {
            for (int i = 0; i < tabListTags.length; i++) {
                {
                    TabList.Tab tab = tabList.new Tab(this);
                    tab.setText(tabListTags[i]);
                    tabList.addTab(tab);
                }
                //
                List<GridItemInfo> listData = m_mpAllData.get(i);
                if (listData == null) listData = new ArrayList<>();
                m_mpAllData.put(i, listData);
            }
        }
        tabList.addTabSelectedListener(new TabList.TabSelectedListener() {
                                           @Override
                                           public void onSelected(TabList.Tab tab) {
                                               int index=tab.getPosition();
                                               if(index==9)//exit
                                               {
                                                   CSaveDataInoFIle.WriteFile(getContext(),m_mpAllData);
                                                   present(new MainAbilitySlice(),new Intent());
                                               }else{
                                                   try {
                                                       ShowCategoryPics(index);
                                                   } catch (IOException | ClassNotFoundException e) {
                                                       e.printStackTrace();
                                                   }
                                               }
                                           }
                                           @Override
                                           public void onUnselected(TabList.Tab tab) {
                                           }
                                           @Override
                                           public void onReselected(TabList.Tab tab) {
                                           }
                                       }

        );
        tabList.selectTabAt(0);
    }
    private void ShowCategoryPics(int idc) throws IOException, ClassNotFoundException {
        //////
        m_curIDCategory=idc;
        gridView = (DragGridView) findComponentById(ResourceTable.Id_manage_data_gridView);
        dataItemList = m_mpAllData.get(idc);
        // 设置列数
        gridView.setColumn(3);
        // 设置数据
        gridView.setData(dataItemList);
    }
    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode==imgRequestCode && resultCode!=0)
        {
            String newFileName=null;
            int idOrder=resultData.getIntParam("indexOrder",-1);
            String chooseImgUri=resultData.getUriString();
            DataAbilityHelper helper=DataAbilityHelper.creator(getContext());
            ImageSource imageSource = null;
            String chooseImgId=null;
            if(chooseImgUri.lastIndexOf("%3A")!=-1){
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf("%3A")+3);
            }
            else {
                chooseImgId = chooseImgUri.substring(chooseImgUri.lastIndexOf('/')+1);
            }
            Uri uri=Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI,chooseImgId);
            try {//读取图片
                FileDescriptor fd = helper.openFile(uri, "r");
                imageSource = ImageSource.create(fd, null);
                //创建位图
                PixelMap pixelMap = imageSource.createPixelmap(null);
                //find the maximum order
                int idOrderMax=0;
                for (int i=0;i<dataItemList.size();i++)
                {
                    idOrder=max(idOrder,dataItemList.get(i).getIdOrder());
                }
                //save file
                int idx=dataItemList.size();
                String name=String.format("%d_%d_%d.png",m_curIDCategory,idx,System.currentTimeMillis());
                newFileName=new File(getFilesDir(),name).getPath();
                OperFile.SavePixelMapToFile(pixelMap,newFileName);
                //save the data information to container
                GridItemInfo data=new GridItemInfo(newFileName,idx,idOrder);
                dataItemList.add(idx,data);
                Collections.sort(dataItemList); //order
                //save the info.file
                CSaveDataInoFIle.WriteFile(this,m_mpAllData);
                //
            } catch (Exception e) {
                new ToastDialog(getContext())
                        .setText("请选择.png,.jpg,.bmp格式的图片文件，建议图片长宽为16:10清晰图片.")
                        .setAlignment(LayoutAlignment.CENTER)
                        .setTransparent(false)
                        .setSize(600,300)
                        .setDuration(8000)
                        .show();

                e.printStackTrace();
            } finally {
                if (imageSource != null) {
                    imageSource.release();
                }
            }
            try {
                gridView.AddNewData(newFileName);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEY_BACK:
                if (!gridView.isBack()) {
                    gridView.resetAnimation();
                    gridView.setBack(true);
                    return true;
                }
        }
        return false;
    }

}
