package top.dcenter.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import top.dcenter.security.core.consts.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * request 工具
 * @author zyw
 * @version V1.0  Created by 2020/5/30 16:19
 */
public class RequestUtil {

    /**
     * 把 ajax/form/queryString 请求中的数据转换为(Map)存储在 request scope 中的 key,
     */
    public static final String REQUEST_PARAMETER_MAP  = "REQUEST_PARAMETER_MAP";
    /**
     * 把请求中的数据转换为 String 存储在 Map 中的 key,
     */
    public static final String NATIVE_REQUEST_BODY_AS_STRING = "NATIVE_REQUEST_BODY_AS_STRING";
    /**
     * 验证 request 中参数是否是 json 的字符串的前缀,
     */
    public static final String VALIDATE_JSON_PREFIX  = "{";

    /**
     * 提取 request 中指定 paramName 的值. <br>
     *     支持: application/x-www-form-urlencoded, application/json
     * @param request   {@link HttpServletRequest}, not Null
     * @param objectMapper  jackson 实列, not Null
     * @param paramName 参数名称, not Null
     * @return Object, 当出现错误或没有元素时返回一个 null
     */
    public static Object getParameter(HttpServletRequest request, ObjectMapper objectMapper,
                                      String paramName) {

        try
        {
            Object data = null;
            // 考虑到 ajax 请求时, 不指定 ContentType, 默认为 application/x-www-form-urlencoded, 为了兼容性, 此格式与 json 作同样处理
            if (request.getContentType().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                || request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE))
            {
                Map<String, Object> dataMap = extractRequestJsonData2Map(request, objectMapper);

                if (MapUtils.isNotEmpty(dataMap))
                {
                    data = dataMap.get(paramName);
                }
            } else
            {
                data = request.getParameter(paramName);
            }
            return data;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    /**
     * 提取 request 中的 json 数据. 再转换为 Map
     * @param request   json类型的输入流, not Null
     * @param objectMapper  jackson 实列, not Null
     * @return Map<String, Object>, 当出现错误或没有元素时返回一个 null
     */
    public static Map<String, Object> extractRequestJsonData2Map(HttpServletRequest request, ObjectMapper objectMapper) {
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) request.getAttribute(REQUEST_PARAMETER_MAP);

            if (MapUtils.isNotEmpty(map))
            {
                return map;
            }
            // 获取 表单 字节数据
            byte[] bytes = request.getInputStream().readAllBytes();
            if (bytes.length == 0)
            {
                return null;
            }

            String jsonData = new String(bytes, StandardCharsets.UTF_8);
            if (StringUtils.isBlank(jsonData))
            {
                return null;
            }
            // 转换为 map 类型, 并放入 request 域方便下次调用
            if (StringUtils.startsWith(jsonData, VALIDATE_JSON_PREFIX))
            {
                map = objectMapper.readValue(jsonData, Map.class);
            } else
            {
                map = ConvertUtil.string2JsonMap(jsonData, URL_PARAMETER_SEPARATOR,
                                                 SecurityConstants.KEY_VALUE_SEPARATOR);
            }
            map.put(NATIVE_REQUEST_BODY_AS_STRING, jsonData);
            request.setAttribute(REQUEST_PARAMETER_MAP, map);
            return map;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 提取 request 中的 json 数据. 转换为 T 对象
     * @param request   json类型的输入流, not Null
     * @param objectMapper  jackson 实列, not Null
     * @param clz  Class<T>, not Null
     * @return T, 当出现错误或没有元素时返回一个 null
     */
    public static <T> T extractRequest2Object(HttpServletRequest request, ObjectMapper objectMapper,
                                                            Class<T> clz) {
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) request.getAttribute(REQUEST_PARAMETER_MAP);

            if (MapUtils.isNotEmpty(map))
            {
                String requestBody = (String) map.get(NATIVE_REQUEST_BODY_AS_STRING);
                if (StringUtils.isBlank(requestBody))
                {
                    return null;
                }

                return json2Object(objectMapper, (Class<T>) clz, requestBody);

            }
            // 获取 表单 字节数据
            byte[] bytes = request.getInputStream().readAllBytes();
            if (bytes.length == 0)
            {
                return null;
            }

            String jsonData = new String(bytes, StandardCharsets.UTF_8);
            if (StringUtils.isBlank(jsonData))
            {
                return null;
            }
            // 转换为 map 类型, 并放入 request 域方便下次调用
            return json2Object(objectMapper, clz, jsonData);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 根据提供的 clz 与 requestBody 转换为 clz 实例
     * @param objectMapper  objectMapper
     * @param clz   Class
     * @param requestBody   json 字符串 或 FormData 字符串 或 queryString
     * @return T 返回 clz 的实例
     * @throws com.fasterxml.jackson.core.JsonProcessingException   JsonProcessingException
     */
    private static <T> T json2Object(ObjectMapper objectMapper, Class<T> clz, String requestBody) throws com.fasterxml.jackson.core.JsonProcessingException {

        if (StringUtils.startsWith(requestBody, VALIDATE_JSON_PREFIX))
        {
            return objectMapper.readValue(requestBody, clz);
        }
        else
        {
            Map<String, Object> map = ConvertUtil.string2JsonMap(requestBody, URL_PARAMETER_SEPARATOR,
                                             SecurityConstants.KEY_VALUE_SEPARATOR);
            return objectMapper.readValue(map.toString(), clz);
        }
    }

}

