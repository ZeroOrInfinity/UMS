package top.dcenter.ums.security.core.util;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.net.URLDecoder.decode;

/**
 * 类型转换工具栏
 * @author zyw
 * @version V1.0  Created by 2020/5/6 13:59
 */
public class ConvertUtil {


    /**
     * 字符转换为 Set 类型，比如：name,age,job
     * @param str   字符串
     * @param separator 分隔符，不为 null
     * @return  Set, 当 str 为空时，返回空的 Set
     */
    public static Set<String> string2Set(String str, String separator){
        String[] splits = StringUtils.splitByWholeSeparator(str, separator);
        if (splits == null)
        {
            return new HashSet<>();
        }
        return Stream.of(splits).collect(Collectors.toSet());
    }

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
     * @return  HashMap&#60;String, T&#62;, 如果没有之会返回空的 Map
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
     * @return  HashMap&#60;String, Object&#62;, 当 kvStrings 为空时，返回空的 map
     */
    public static Map<String, Object> string2JsonMap(String kvStrings, String separator, String kvSeparator){
        String[] splits = StringUtils.splitByWholeSeparator(kvStrings, separator);
        if (splits == null)
        {
            return new JsonMap<>(0);
        }
        int length = splits.length;
        Map<String, Object> map = new JsonMap<>(length);

        string2JsonMap(kvSeparator, splits, map);

        return map;
    }

    private static void string2JsonMap(String kvSeparator, String[] splits, Map<String, Object> map) {
        for (String split : splits)
        {
            if (StringUtils.isNotBlank(split))
            {
                final String[] kvArr = StringUtils.splitByWholeSeparator(split, kvSeparator);
                if (kvArr != null && kvArr.length == 2)
                {
                    map.compute(kvArr[0], (k, v) -> {
                        if (v == null)
                        {
                            v = decode(kvArr[1], StandardCharsets.UTF_8);
                        } else if (v instanceof JsonList)
                        {
                            ((JsonList) v).add(decode(kvArr[1], StandardCharsets.UTF_8));
                        } else
                        {
                            List list = new JsonList<>();
                            list.add(v);
                            list.add(decode(kvArr[1], StandardCharsets.UTF_8));
                            v = list;
                        }
                        return v;
                    });
                }
            }
        }
    }


    /**
     * 字符转换为 Map 类型
     * @param keyStr   keyStr 格式：name,age
     * @param separator 分隔符, 如 ','，不为 null
     * @param value map 的 value，不为 null
     * @return  HashMap&#60;String, T&#62;, 当 keyStr 为空时，返回空的 map
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

    /**
     * 修改 toString 输出符合Json 格式.
     * @param &#60;K&#62;
     * @param &#60;V&#62;
     */
    private static class JsonMap<K, V> extends HashMap<K, V> {

        public JsonMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public JsonMap(int initialCapacity) {
            super(initialCapacity);
        }

        public JsonMap() {
        }

        public JsonMap(Map<? extends K, ? extends V> m) {
            super(m);
        }

        @Override
        public String toString() {
            Iterator<Entry<K,V>> i = entrySet().iterator();
            if (! i.hasNext())
            {
                return "{}";
            }

            StringBuilder sb = new StringBuilder();
            sb.append('{');
            for (;;) {
                Entry<K,V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (key != this)
                {
                    sb.append("\"");
                    sb.append(key);
                    sb.append("\"");

                    sb.append(":");
                    if (value != this)
                    {
                        if (value instanceof String)
                        {
                            sb.append("\"");
                        }
                        sb.append(value);
                        if (value instanceof String)
                        {
                            sb.append("\"");
                        }
                    }
                    if (! i.hasNext())
                    {
                        return sb.append('}').toString();
                    }
                    sb.append(',');
                }
            }
        }
    }

    private static class JsonList<E> extends ArrayList<E> {
        public JsonList(int initialCapacity) {
            super(initialCapacity);
        }

        public JsonList() {
        }

        public JsonList(@NotNull Collection<? extends E> c) {
            super(c);
        }

        @Override
        public String toString() {
            Iterator<E> it = iterator();
            if (! it.hasNext())
            {
                return "[]";
            }

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (;;) {
                E e = it.next();
                if (e != this)
                {
                    sb.append("\"");
                    sb.append(e);
                    sb.append("\"");
                    if (! it.hasNext())
                    {
                        return sb.append(']').toString();
                    }
                    sb.append(',');
                }
            }
        }
    }

}
