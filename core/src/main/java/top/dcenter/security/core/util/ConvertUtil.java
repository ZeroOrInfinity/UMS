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
public class ConvertUtil {
    /**
     * 字符转换为 List 类型，比如：name,age,job
     * @param str   字符串
     * @param separator 分隔符，不为 null
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
     * List 转换为 Map 类型，map 的 v 的值统一为 参数 value
     * @param list  不为 null
     * @param value map 的 value，不为 null
     * @return  HashMap<String, T>, 如果没有之会返回空的 Map
     */
    public static <T> Map<String, T> list2Map(List<String> list, T value){
        return list.stream().collect(Collectors.toMap(Function.identity(), (s) -> value));
    }

    /**
     * List 转换为 Map 类型，map 的 v 的值统一为 参数 value
     * @param list  {@link List}
     * @param value map 的 value
     * @param map 用于存储结果的 Map
     */
    public static <T> void list2Map(List<String> list, T value, Map<String, T> map){
        list.forEach(s -> map.put(s, value));
    }

    /**
     * 字符转换为 Map 类型，比如：name=tom,age=18
     * 当 kvStrings 为空时，返回空的 map
     * @param kvStrings   字符串
     * @param separator 分隔符，不为 null
     * @param kvSeparator key 与 value 的分隔符，不为 null
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

        string2Map(kvSeparator, splits, map);

        return map;
    }

    /**
     * 字符转换为 Map 类型，比如：name=tom,age=18
     * 当 kvStrings 为空时，返回空的 map
     * @param kvStrings   字符串
     * @param separator 分隔符，不为 null
     * @param kvSeparator key 与 value 的分隔符，不为 null
     * @return  HashMap<String, Object>, 当 kvStrings 为空时，返回空的 map
     */
    public static Map<String, Object> string2MapOfObj(String kvStrings, String separator, String kvSeparator){
        String[] splits = StringUtils.splitByWholeSeparator(kvStrings, separator);
        if (splits == null)
        {
            return new HashMap<>(0);
        }
        int length = splits.length;
        Map<String, Object> map = new HashMap<>(length);

        string2Map(kvSeparator, splits, map);

          return map;
    }

    private static void string2Map(String kvSeparator, String[] splits, Map<String, ? super String> map) {
        String[] kvArr;
        for (String split : splits)
        {
            if (StringUtils.isNotBlank(split))
            {
                kvArr = StringUtils.splitByWholeSeparator(split, kvSeparator);
                if (kvArr != null && kvArr.length == 2)
                {
                    map.put(kvArr[0], kvArr[1]);
                }
            }
        }
    }


    /**
     * 字符转换为 Map 类型
     * @param keyStr   keyStr 格式：name,age
     * @param separator 分隔符, 如 ','，不为 null
     * @param value map 的 value，不为 null
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

        for (String split : splits)
        {
            map.put(split, value);
        }
        return map;
    }

    /**
     * 字符转换为 Map 类型，比如：name=tom,age=18
     * @param keyStr   keyStr 格式：name,age
     * @param separator 分隔符, 如 ','，不为 null
     * @param value map 的 value，不为 null
     * @param map 用于存储结果的 Map
     */
    public static <T> void keyString2Map(String keyStr, String separator, T value, Map<String, T> map){
        String[] splits = StringUtils.splitByWholeSeparator(keyStr, separator);
        if (splits == null)
        {
            return;
        }

        for (String split : splits)
        {
            map.put(split, value);
        }
    }

}
