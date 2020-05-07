package top.dcenter.security.core.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 类型转换工具栏
 * @author zyw
 * @version V1.0  Created by 2020/5/6 13:59
 */
public class CastUtil {
    /**
     * 字符转换为 List 类型
     * @param str   字符串
     * @param separator 分隔符
     * @return  List
     */
    public static List<String> string2List(String str, String separator){
        String[] splits = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
        return Stream.of(splits).collect(Collectors.toList());
    }
    /**
     * 字符转换为 Map 类型
     * @param str   字符串
     * @param separator 分隔符
     * @return  HashMap<String, T>
     */
    public static <T> Map<String, T> string2Map(String str, String separator, T value){
        String[] splits = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
        int length = splits.length;
        Map<String, T> map = new HashMap<>(length);

        for (int i = 0; i < length; i++)
        {
            map.put(splits[i], value);
        }
        return map;
    }

    /**
     * 字符转换为 Map 类型
     * @param str   字符串
     * @param separator 分隔符
     * @return  HashMap<String, T>
     */
    public static <T> void string2Map(String str, String separator, T value, Map<String, T> map){
        String[] splits = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
        int length = splits.length;

        for (int i = 0; i < length; i++)
        {
            map.put(splits[i], value);
        }
    }
}
