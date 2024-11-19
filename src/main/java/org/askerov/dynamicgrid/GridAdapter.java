/*
 * Copyright (C) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.askerov.dynamicgrid;


import com.uestc.dalangapp.ResourceTable;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;
import ohos.app.Context;
import ohos.media.image.PixelMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The GridAdapter
 *
 * @since 2021-05-15
 */
public class GridAdapter {
    private static final int GRID_LAYOUT_BORDER_MARGIN = 24;
    private static final int GRID_ITEM_RIGHT_MARGIN = 8;
    OperToolImagFile fileOper=new OperToolImagFile();
    private List<Component> componentList = new ArrayList<>();
    private int itemPx=-1;
    private Context context=null;
    private List<GridItemInfo> dataList=new ArrayList<>();
    DragGridView fatherClass;

    public  void setFatherClass(DragGridView gridView)
    {
        this.fatherClass=gridView;
    }
    /**
     * 适配器
     *
     * @param context
     * @param itemInfos
     * @param column
     */
    public GridAdapter(Context context, List<GridItemInfo> itemInfos,int column) throws IOException {
        this.context=context;
        //
        itemPx = getItemWidthByScreen(context,column);
        dataList=itemInfos;
        Collections.sort(dataList);
        //file operation
        for (GridItemInfo item : dataList) {
            Component gridItem = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_grid_item, null, false);
            gridItem.setTag(item.getTag());
            if (gridItem.findComponentById(ResourceTable.Id_grid_item_image) instanceof Image) {
                Image imageItem = (Image) gridItem.findComponentById(ResourceTable.Id_grid_item_image);
                //read file data and clone
                String fileName=item.getFileName();
                PixelMap img=fileOper.ReadPixelMapFile(fileName);
                imageItem.setPixelMap(img);
                //
                //imageItem.setPixelMap(item.getIconId());
                //imageItem.setScaleMode(Image.ScaleMode.STRETCH);
                imageItem.setWidth(itemPx);
                imageItem.setHeight((int)(itemPx/1.6));
                //
                //delete image
                Image imageDel = (Image) gridItem.findComponentById(ResourceTable.Id_grid_item_delete);
                imageDel.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        dataList.remove(item);
                        try {
                            fatherClass.DeleteDataMsg();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            gridItem.setWidth(itemPx);
            gridItem.setHeight((int)(itemPx/1.6));
            gridItem.setMarginsLeftAndRight(10,10);
            gridItem.setMarginsTopAndBottom(10,10);
            componentList.add(gridItem);
        }

        //add last add icon
        Component gridItem = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_gridaddnewicon, null, false);
        gridItem.setTag("addGrid");
        Image imageItem = (Image) gridItem.findComponentById(ResourceTable.Id_grid_item_addnewicon);
        imageItem.setPixelMap(imageItem.getPixelMap());
        imageItem.setWidth(itemPx);
        imageItem.setHeight((int)(itemPx/1.6));
        imageItem.setScaleMode(Image.ScaleMode.STRETCH);
        //
        gridItem.setWidth(itemPx);
        gridItem.setHeight((int)(itemPx/1.6));
        gridItem.setMarginsLeftAndRight(10,10);
        gridItem.setMarginsTopAndBottom(10,10);
        componentList.add(gridItem);
    }

    public void AddNewData(String fileName) throws IOException {
        //delete the last add Icon
        int nsize=dataList.size();
        componentList.remove(nsize-1);
        //
        GridItemInfo item=dataList.get(nsize-1);
        Component gridItem = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_grid_item, null, false);
        gridItem.setTag("upGrid");//item.getTag());
        if (gridItem.findComponentById(ResourceTable.Id_grid_item_image) instanceof Image) {
            Image imageItem = (Image) gridItem.findComponentById(ResourceTable.Id_grid_item_image);
            //read file data and clone
            PixelMap img=fileOper.ReadPixelMapFile(fileName);
            imageItem.setPixelMap(img);
            //
            imageItem.setWidth(itemPx/2);
            imageItem.setHeight((int)(0.5*itemPx/1.6));
            imageItem.setScaleMode(Image.ScaleMode.STRETCH);
        }
        gridItem.setWidth(itemPx);
        gridItem.setHeight((int)(itemPx/1.6));
        gridItem.setMarginsLeftAndRight(10,10);
        gridItem.setMarginsTopAndBottom(10,10);
        componentList.add(gridItem);

        //add add Icon
        //add last add icon
        Component gridItemAdd = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_gridaddnewicon, null, false);
        gridItemAdd.setTag("addGrid");//("addGrid");
        Image imageItemAdd = (Image) gridItemAdd.findComponentById(ResourceTable.Id_grid_item_addnewicon);
        imageItemAdd.setPixelMap(imageItemAdd.getPixelMap());
        imageItemAdd.setWidth(itemPx);
        imageItemAdd.setHeight((int)(itemPx/1.6));
        //
        gridItemAdd.setWidth(itemPx);
        gridItemAdd.setHeight((int)(itemPx/1.6));
        gridItemAdd.setMarginsLeftAndRight(10,10);
        gridItemAdd.setMarginsTopAndBottom(10,10);
        componentList.add(gridItemAdd);
    }

    /**
     * method for get componentList
     *
     * @return componentList
     */
    public List<Component> getComponentList() {
        return componentList;
    }

    private int getItemWidthByScreen(Context context,int column) {
        int screenWidth = AppUtils.getScreenInfo(context).getPointXToInt();
        return (screenWidth-250-20*4) / column;
    }
}
