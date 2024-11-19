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

/**
 * Grid item model
 */
public class GridItemInfo implements Comparable<GridItemInfo> {
    private String fileName;
    private int iconId;
    private String tag;
    private int idOrder;

    /**
     * Item data model Constructor
     *
     * @param itemText item text
     * @param iconId image resource ID
     * @param idOrder component textSize
     */
    public GridItemInfo(String itemText, int iconId, int idOrder) {
        this.fileName = itemText;
        this.iconId = iconId;
        this.tag = "upGrid";
        this.idOrder = idOrder;
    }
    public GridItemInfo(GridItemInfo data) {
        this.fileName = data.getFileName();
        this.iconId = data.getIconId();
        this.tag =data.getTag();
        this.idOrder = data.getIdOrder();
    }

    public String getFileName() {
        return fileName;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTag() {
        return tag;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idt)
    {
        idOrder=idt;
    }

    @Override
    public int compareTo(GridItemInfo gridItemInfo) {
        return this.getIdOrder()-gridItemInfo.getIdOrder();
    }
}
