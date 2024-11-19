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

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * Log
 *
 * @since 2021-04-10
 */
public class Log {
    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x01, "MyDemo");
    private static final String SPACE = " ";

    private Log() {
    }

    /**
     * 日志格式转换，自动加 public
     * From: width=%d, height=%f, text=%s
     * To:width=%{public}d, height=%{public}f, text=%{public}s
     *
     * @param logMessageFormat 日志格式，未加public
     * @return 日志格式，已加public
     */
    private static String replaceFormat(String logMessageFormat) {
        return logMessageFormat.replaceAll("%([d|f|s])", "%{public}$1");
    }

    /**
     * 打印
     *
     * @param tag tag
     * @param format 格式
     * @param args 打印内容
     */
    public static void d(String tag, String format, Object... args) {
        HiLog.debug(LABEL, tag + SPACE + replaceFormat(format), args);
    }

    /**
     * 打印
     *
     * @param tag tag
     * @param format 格式
     * @param args 打印内容
     */
    public static void i(String tag, String format, Object... args) {
        HiLog.info(LABEL, tag + SPACE + replaceFormat(format), args);
    }

    /**
     * 打印
     *
     * @param tag tag
     * @param format 格式
     * @param args 打印内容
     */
    public static void w(String tag, String format, Object... args) {
        HiLog.warn(LABEL, tag + SPACE + replaceFormat(format), args);
    }

    /**
     * 打印
     *
     * @param tag tag
     * @param format 格式
     * @param args 打印内容
     */
    public static void e(String tag, String format, Object... args) {
        HiLog.error(LABEL, tag + SPACE + replaceFormat(format), args);
    }
}
