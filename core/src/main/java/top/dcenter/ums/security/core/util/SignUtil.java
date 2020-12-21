/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 签到工具类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/14 18:52
 */
public final class SignUtil {

    private SignUtil() { }

    /**
     * 格式化日期到 yyyyMM 模式
     * @param date  date
     * @return  返回 yyyyMM 模式的日期字符串
     */
    public static String formatDate(LocalDate date) {
        return formatDate(date, "yyyyMM");
    }

    /**
     * 根据指定的 pattern 格式化日期
     * @param date  date
     * @param pattern   日期格式
     * @return  返回指定的 pattern 模式的日期字符串
     */
    public static String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 构建 redis 上用户签到 key 字符串
     * @param uid   用户 ID
     * @param date  date
     * @return  返回签到 key 字符串， 格式如：uid:yyyyMM
     */
    public static String buildSignKey(String uid, LocalDate date) {
        return String.format("%s:%s", uid, formatDate(date));
    }

}