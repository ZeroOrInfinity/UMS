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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.net.URLDecoder.decode;

/**
 * 类转换工具栏
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/6 13:59
 */
@SuppressWarnings("unused")
public final class ConvertUtil {

    private ConvertUtil() {}

    /**
     * 特殊字符正则，sql特殊字符和空白符
     */
    private final static Pattern SPECIAL_CHARS_REGEX = Pattern.compile("[`'\"|/,;()-+*%#·•�　\\s]");

    /**
     * 清理字符串，清理出某些不可见字符和一些sql特殊字符
     *
     * @param txt 文本
     * @return {String}
     */
    @Nullable
    public static String cleanText(@Nullable String txt) {
        if (txt == null) {
            return null;
        }
        return SPECIAL_CHARS_REGEX.matcher(txt).replaceAll("");
    }

    /**
     * 字符转换为 Set 类型，比如：name,age,job
     * @param str   字符串
     * @param separator 分隔符，不为 null
     * @return  Set, 当 str 为空时，返回空的 Set
     */
    public static Set<String> string2Set(String str, String separator){
        if (str == null)
        {
            return new HashSet<>();
        }
        String[] splits = str.split(separator);
        return Stream.of(splits).collect(Collectors.toSet());
    }

    /**
     * 字符转换为 List 类型，比如：name,age,job
     * @param str   字符串
     * @param separator 分隔符，不为 null
     * @return  List, 当 str 为空时，返回空的 List
     */
    public static List<String> string2List(String str, String separator){
        if (str == null)
        {
            return new ArrayList<>();
        }
        String[] splits = str.split(separator);
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
        if (kvStrings == null)
        {
            return new HashMap<>(16);
        }
        String[] splits = kvStrings.split(separator);
        int length = splits.length;
        Map<String, Object> map = new JsonMap<>(length);

        string2JsonMap(kvSeparator, splits, map);

        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void string2JsonMap(String kvSeparator, String[] splits, Map<String, Object> map) {
        for (String split : splits)
        {
            final String[] kvArr = StringUtils.split(split, kvSeparator);
            if (kvArr != null)
            {
                map.compute(kvArr[0], (k, v) -> {
                    try {
                        if (v == null)
                        {
                            v = decode(kvArr[1], StandardCharsets.UTF_8.name());
                        } else if (v instanceof JsonList)
                        {
                            ((JsonList) v).add(decode(kvArr[1], StandardCharsets.UTF_8.name()));
                        } else
                        {
                            List list = new JsonList<>();
                            list.add(v);
                            list.add(decode(kvArr[1], StandardCharsets.UTF_8.name()));
                            v = list;
                        }
                        return v;
                    }
                    catch (UnsupportedEncodingException e) {
                        if (v == null)
                        {
                            v = kvArr[1];
                        } else if (v instanceof JsonList)
                        {
                            ((JsonList) v).add(kvArr[1]);
                        } else
                        {
                            List list = new JsonList<>();
                            list.add(v);
                            list.add(kvArr[1]);
                            v = list;
                        }
                        return v;
                    }
                });
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
        if (keyStr == null)
        {
            return new HashMap<>(16);
        }
        String[] splits = keyStr.split(separator);
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
        if (keyStr == null)
        {
            return;
        }
        String[] splits = keyStr.split(separator);

        for (String split : splits)
        {
            map.put(split, value);
        }
    }

    /**
     * Copy from {@code org.apache.commons.lang3.StringUtils}
     * <p>Splits the provided text into an array with a maximum length,
     * separators specified.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} separatorChars splits on whitespace.</p>
     *
     * <p>If more than {@code max} delimited substrings are found, the last
     * returned string includes all characters after the first {@code max - 1}
     * returned strings (including separator characters).</p>
     *
     * <pre>
     * StringUtils.split(null, *, *)            = null
     * StringUtils.split("", *, *)              = []
     * StringUtils.split("ab cd ef", null, 0)   = ["ab", "cd", "ef"]
     * StringUtils.split("ab   cd ef", null, 0) = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  {@code null} splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars, final int max) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[0]);
    }


    /**
     * Copy from {@code org.apache.commons.lang3.StringUtils}
     * <p>Splits a String by Character type as returned by
     * {@code java.lang.Character.getType(char)}. Groups of contiguous
     * characters of the same type are returned as complete tokens, with the
     * following exception: the character of type
     * {@code Character.UPPERCASE_LETTER}, if any, immediately
     * preceding a token of type {@code Character.LOWERCASE_LETTER}
     * will belong to the following token rather than to the preceding, if any,
     * {@code Character.UPPERCASE_LETTER} token.
     * <pre>
     * StringUtils.splitByCharacterTypeCamelCase(null)         = null
     * StringUtils.splitByCharacterTypeCamelCase("")           = []
     * StringUtils.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringUtils.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringUtils.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringUtils.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
     * StringUtils.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
     * StringUtils.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
     * StringUtils.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
     * </pre>
     * @param str the String to split, may be {@code null}
     * @param camelCase whether to use so-called "camel-case" for letter types
     * @return an array of parsed Strings, {@code null} if null String input
     * @since 2.4
     */
    public static String[] splitByCharacterTypeCamelCase(final String str, final boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new String[0];
        }
        final char[] c = str.toCharArray();
        final List<String> list = new ArrayList<>();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            final int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                final int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return list.toArray(new String[0]);
    }

    /**
     * 修改 toString 输出符合Json 格式.
     */
    private static class JsonMap<K, V> extends HashMap<K, V> {

        private static final long serialVersionUID = 5603812891003320178L;

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
        private static final long serialVersionUID = 752591930857178273L;

        public JsonList(int initialCapacity) {
            super(initialCapacity);
        }

        public JsonList() {
        }

        public JsonList(@NonNull Collection<? extends E> c) {
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