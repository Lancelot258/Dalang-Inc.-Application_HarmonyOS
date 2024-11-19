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

import ohos.agp.components.AttrSet;
import ohos.agp.components.TableLayout;
import ohos.app.Context;

/**
 * The GridView
 */
public class GridView extends TableLayout {
    /**
     * 构造方法
     *
     * @param context
     */
    public GridView(Context context) {
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrSet
     */
    public GridView(Context context, AttrSet attrSet) {
        super(context, attrSet);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrSet
     * @param styleName
     */
    public GridView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
    }

    /**
     * The setAdapter
     *
     * @param adapter adapter
     * @param longClickedListener longClickedListener
     * @param clickedListener clickedListener
     */
    void setAdapter(GridAdapter adapter, LongClickedListener longClickedListener,ClickedListener clickedListener) {
        removeAllComponents();
        for (int i = 0; i < adapter.getComponentList().size(); i++) {
            adapter.getComponentList().get(i).setLongClickedListener(longClickedListener);
            adapter.getComponentList().get(i).setClickedListener(clickedListener);
            addComponent(adapter.getComponentList().get(i));
        }
    }
}
