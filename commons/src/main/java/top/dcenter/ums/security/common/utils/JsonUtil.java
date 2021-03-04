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
package top.dcenter.ums.security.common.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import top.dcenter.ums.security.common.jackson.SimpleGrantedAuthorityMixin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static top.dcenter.ums.security.common.consts.SecurityConstants.CHARSET_UTF8;
import static top.dcenter.ums.security.common.consts.SecurityConstants.HEADER_ACCEPT;

/**
 * json 工具类
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.6 18:03
 */
public final class JsonUtil {

    public static final String ANY_ACCEPT = "*/*";

    private JsonUtil() { }

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * jackson 封装
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String HEADER_X_REQUESTED_WITH_NAME = "X-Requested-With";
    public static final String X_REQUESTED_WITH = "XMLHttpRequest";

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
        // 解决jackson2无法反序列化LocalDateTime的问题
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 判断是否为 ajax 请求或者支持接收 json 格式
     * @param request   request
     * @return  但为 ajax 请求或者支持接收 json 格式返回 true
     */
    public static boolean isAjaxOrJson(HttpServletRequest request) {
        //判断是否为ajax请求 或 支持接收 json 格式
        String xRequestedWith = request.getHeader(HEADER_X_REQUESTED_WITH_NAME);
        String accept = request.getHeader(HEADER_ACCEPT);
        if (ANY_ACCEPT.equals(accept)) {
            return true;
        }
        return (StringUtils.hasText(accept) && accept.contains(MediaType.APPLICATION_JSON_VALUE))
                || (xRequestedWith != null && xRequestedWith.equalsIgnoreCase(X_REQUESTED_WITH));
    }

    /**
     * 向客户端响应 json 格式
     * @param response  response
     * @param status    响应的状态码
     * @param result    相应的结果字符串
     * @throws IOException IOException
     */
    public static void responseWithJson(HttpServletResponse response, int status, String result) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            PrintWriter writer = response.getWriter();
            writer.write(result);
            writer.flush();
            writer.close();
        }
    }

    /**
     * 通过 {@link ObjectMapper} 转换对象到 JSONString, 主要目的用于日志输出对象字符串时使用, 减少 try catch 嵌套, 转换失败记录日志并返回空字符串.
     * @param obj   Object
     * @return  返回 JSONString, 转换失败记录日志并返回空字符串.
     */
    public static String toJsonString(Object obj) {
        try
        {
            return OBJECT_MAPPER.writeValueAsString(obj);
        }
        catch (JsonProcessingException e)
        {
            String msg = String.format("Object2JsonString 失败: %s, Object=%s", e.getMessage(), obj);
            log.error(msg, e);
            return "";
        }
    }

    /**
     * 使用 {@link ObjectMapper} 把 jsonString 反序列化为 T 对象.
     * @param jsonString    json string
     * @param clz           要反序列化的目标 class
     * @return  返回反序列化对象, 如果反序列化错误返回 null
     */
    @Nullable
    public static <T> T json2Object(@NonNull String jsonString, @NonNull Class<T> clz) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, clz);
        }
        catch (JsonProcessingException e) {
            log.error(String.format("[%s] 反序列化为 [%s] 时错误: %s", jsonString, clz.getName(), e.getMessage()), e);
            return null;
        }
    }

    /**
     * 使用 {@link ObjectMapper} 把 jsonString 反序列化为 T 对象.
     * @param jsonString    json string
     * @param valueTypeRef  要反序列化的目标 type
     * @return  返回反序列化对象, 如果反序列化错误返回 null
     */
    @Nullable
    public static <T> T json2Object(@NonNull String jsonString, @NonNull TypeReference<T> valueTypeRef) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, valueTypeRef);
        }
        catch (JsonProcessingException e) {
            log.error(String.format("[%s] 反序列化为 [%s] 时错误: %s", jsonString, valueTypeRef.getType(), e.getMessage()), e);
            return null;
        }
    }

    /**
     * 专门用于反序列化, 根据 JsonNode 中的 "@class" 进行 json 反序列化
     * @param mapper    {@link ObjectMapper}
     * @param jsonNode  {@link JsonNode}
     * @return  返回 json 反序列化后 JsonNode 中的 "@class" 所指定的对象
     * @throws IOException  反序列化异常
     */
    public static Object getObject(ObjectMapper mapper, JsonNode jsonNode) throws IOException {
        Object result;
        // 获取 result 实际的全类名
        final String resultString = jsonNode.toString();
        String resultClassName = resultString.substring(1);
        String prefix = "\"@class\":\"";
        if (resultClassName.startsWith(prefix)) {
            resultClassName = resultClassName.substring(resultClassName.indexOf(prefix) + 10);
        } else {
            resultClassName = resultClassName.substring(1);
        }
        resultClassName = resultClassName.substring(0, resultClassName.indexOf("\""));

        try {
            final Class<?> resultClass = Class.forName(resultClassName);
            final JavaType javaType = mapper.getTypeFactory().constructType(resultClass);
            result = mapper.convertValue(jsonNode, javaType);
        }
        catch (Exception e) {
            String msg = String.format("Jackson 反序列化错误: %s", jsonNode.toString());
            throw new IOException(msg, e);
        }
        return result;
    }

}
