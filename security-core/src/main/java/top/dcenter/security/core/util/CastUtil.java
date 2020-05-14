package top.dcenter.security.core.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
     * @return  List, 当 str 为空时，返回空的 List
     */
    public static List<String> string2List(String str, String separator){
        String[] splits = StringUtils.splitByWholeSeparator(str, separator);
        if (splits == null)
        {
            return new ArrayList<>();
        }
        return Stream.of(splits).collect(Collectors.toList());
    }

    /**
     * List 转换为 Map 类型
     * @param list
     * @param value map 的 value
     * @return  HashMap<String, T>
     */
    public static <T> Map<String, T> list2Map(List<String> list, T value){

        return list.stream().collect(Collectors.toMap(Function.identity(), (s) -> value));

    }

    /**
     * List 转换为 Map 类型
     * @param list
     * @param value map 的 value
     * @param map 用于存储结果的 Map
     * @return  HashMap<String, T>
     */
    public static <T> void list2Map(List<String> list, T value, Map<String, T> map){

        list.forEach(s -> map.put(s, value));
    }

    /**
     * 字符转换为 Map 类型
     * 当 kvStrings 为空时，返回空的 map
     * @param kvStrings   字符串
     * @param separator 分隔符
     * @param kvSeparator key 与 value 的分隔符
     * @return  HashMap<String, String>, 当 kvStrings 为空时，返回空的 map
     */
    public static Map<String, String> string2Map(String kvStrings, String separator, String kvSeparator){
        String[] splits = StringUtils.splitByWholeSeparator(kvStrings, separator);
        if (splits == null)
        {
            return new HashMap<>(0);
        }
        int length = splits.length;
        Map<String, String> map = new HashMap<>(length);

        String[] kvArr;
        for (int i = 0; i < length; i++)
        {
            if (StringUtils.isNotBlank(splits[i]))
            {
                kvArr = StringUtils.splitByWholeSeparator(splits[i], kvSeparator);
                if (kvArr != null && kvArr.length == 2)
                {
                    map.put(kvArr[0], kvArr[1]);
                }
            }
        }
        return map;
    }

    /**
     * 字符转换为 Map 类型
     * @param keyStr   字符串
     * @param separator 分隔符
     * @param value map 的 value
     * @return  HashMap<String, T>, 当 keyStr 为空时，返回空的 map
     */
    public static <T> Map<String, T> keyString2Map(String keyStr, String separator, T value){
        String[] splits = StringUtils.splitByWholeSeparator(keyStr, separator);
        if (splits == null)
        {
            return new HashMap<>(0);
        }
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
     * @param keyStr   字符串
     * @param separator 分隔符
     * @param value map 的 value
     * @param map 用于存储结果的 Map
     * @return  HashMap<String, T>
     */
    public static <T> void keyString2Map(String keyStr, String separator, T value, Map<String, T> map){
        String[] splits = StringUtils.splitByWholeSeparator(keyStr, separator);
        if (splits == null)
        {
            return;
        }
        int length = splits.length;

        for (int i = 0; i < length; i++)
        {
            map.put(splits[i], value);
        }
    }
}
