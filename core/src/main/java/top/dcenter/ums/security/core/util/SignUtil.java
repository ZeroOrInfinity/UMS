package top.dcenter.ums.security.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 签到工具类
 * @author zyw
 * @version V1.0  Created by 2020/9/14 18:52
 */
public class SignUtil {

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
