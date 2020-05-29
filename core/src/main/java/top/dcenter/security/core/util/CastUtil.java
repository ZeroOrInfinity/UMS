package top.dcenter.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
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
     * @param list
     * @param value map 的 value
     * @param map 用于存储结果的 Map
     * @return  HashMap<String, T>
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
     * 字符转换为 Map 类型，比如：name=tom,age=18
     * @param keyStr   字符串
     * @param separator 分隔符，不为 null
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

        for (int i = 0; i < length; i++)
        {
            map.put(splits[i], value);
        }
        return map;
    }

    /**
     * 字符转换为 Map 类型，比如：name=tom,age=18
     * @param keyStr   字符串
     * @param separator 分隔符，不为 null
     * @param value map 的 value，不为 null
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

    /**
     * 将输入流转换为 map<Stirng, String>
     * @param inputStream   json类型的输入流
     * @param length  inputStream 的长度
     * @param charset  字符集
     * @param objectMapper
     * @return Map<String, String>, 当出现错误或没有元素时返回一个 {@link Collections.EMPTY_MAP}
     */
    @SuppressWarnings("JavadocReference")
    public static Map<String, Object> jsonInputStream2Map(ServletInputStream inputStream, int length, String charset,
                                                          ObjectMapper objectMapper) {
        ReadableByteChannel byteChannel = Channels.newChannel(inputStream);
        try
        {
            ByteBuffer byteBuffer = ByteBuffer.allocate(length);
            byteChannel.read(byteBuffer);
            byteBuffer.flip();
            System.out.println("byteBuffer.capacity() = " + byteBuffer.capacity());
            String json = new String(byteBuffer.array(), charset);
            String json1 = new String(byteBuffer.array());
            return objectMapper.readValue(json, Map.class);
        }
        catch (Exception e)
        {
            return Collections.EMPTY_MAP;
        } finally
        {
            try
            {
                byteChannel.close();
            }
            catch (IOException e) {}
        }
    }
}
