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
 *
 */

package org.askerov.dynamicgrid;

import ohos.agp.components.Attr;
import ohos.agp.components.AttrSet;
import ohos.agp.components.element.Element;
import ohos.agp.utils.Color;

/**
 * xml 属性读取工具类
 *
 * @since 2021-04-07
 */
public class AttrValue {
    private AttrValue() {
        // do nothing
    }

    /**
     * 读取 xml 属性值
     *
     * @param attrSet 属性集合
     * @param key xml 属性键
     * @param defValue 默认值
     * @param <T> 属性值类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked cast")
    public static <T> T get(AttrSet attrSet, String key, T defValue) {
        if (!attrSet.getAttr(key).isPresent()) {
            return (T) defValue;
        }

        Attr attr = attrSet.getAttr(key).get();
        if (defValue instanceof String) {
            return (T) attr.getStringValue();
        } else if (defValue instanceof Long) {
            return (T) (Long) (attr.getLongValue());
        } else if (defValue instanceof Float) {
            return (T) (Float) (attr.getFloatValue());
        } else if (defValue instanceof Integer) {
            return (T) (Integer) (attr.getIntegerValue());
        } else if (defValue instanceof Boolean) {
            return (T) (Boolean) (attr.getBoolValue());
        } else if (defValue instanceof Color) {
            return (T) (attr.getColorValue());
        } else if (defValue instanceof Element) {
            return (T) (attr.getElement());
        } else {
            return (T) defValue;
        }
    }

    /**
     * 读取 xml 尺寸型属性, 例如 cornerRadius="4vp"
     *
     * @param attrSet 属性集合
     * @param key 属性键
     * @param defDimensionValue 默认值
     * @return 属性值
     */
    public static int getDimension(AttrSet attrSet, String key, int defDimensionValue) {
        if (!attrSet.getAttr(key).isPresent()) {
            return defDimensionValue;
        }

        Attr attr = attrSet.getAttr(key).get();
        return attr.getDimensionValue();
    }
}
