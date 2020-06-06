package top.dcenter.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.consts.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.AJAX_JSON;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * request 工具
 * @author zyw
 * @version V1.0  Created by 2020/5/30 16:19
 */
public class RequestUtil {

    /**
     * 把 ajax/form/queryString 请求中的数据(Map)存储在 request scope 的属性 key,
     */
    public static final String REQUEST_PARAMETER_MAP  = "REQUEST_PARAMETER_MAP";
    /**
     * 验证 request 中参数是否是 json 的字符串的前缀,
     */
    public static final String VALIDATE_JSON_PREFIX  = "{";

    /**
     * 提取 request 中指定 paramName 的值. <br>
     *     首先通过 {@link ServletWebRequest#getParameter(String)} 提取, 如果获取不到相应的值, 通过
     *     {@link ServletWebRequest#getParameterMap()} 获取再通过 {@link ObjectMapper} 转化为 Map, 最后提取对应的 paramName 的值
     * @param request   {@link HttpServletRequest}
     * @param objectMapper  jackson 实列
     * @param paramName 参数名称
     * @return Object, 当出现错误或没有元素时返回一个 null
     */
    public static Object extractRequestDataWithParamName(HttpServletRequest request, ObjectMapper objectMapper,
                                                    String paramName) {

        try
        {
            Object data = null;
            Map<String, Object> dataMap = extractRequestJsonData(request, objectMapper);

            if (MapUtils.isNotEmpty(dataMap))
            {
                data = dataMap.get(paramName);
            }
            return data;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 提取 request 中指定 paramName 的值. <br>
     *     首先通过 {@link ServletWebRequest#getParameter(String)} 提取, 如果获取不到相应的值, 在判断 Header 中是否有自定义的
     *     {@link top.dcenter.security.core.consts.SecurityConstants#AJAX_JSON}, 如果有则通过
     *     {@link ServletWebRequest#getParameterMap()} 获取再通过 {@link ObjectMapper} 转化为 Map, 最后提取对应的 paramName 的值
     * @param request   {@link ServletWebRequest}
     * @param objectMapper  jackson 实列
     * @param paramName 参数名称
     * @return String, 当出现错误或没有元素时返回一个 null
     */
    public static Object extractRequestDataWithParamNameByHeader(ServletWebRequest request,
                                            ObjectMapper objectMapper, String paramName) {

        HttpServletRequest req = request.getRequest();
        try
        {
            Object data = null;
            String json = request.getHeader(AJAX_JSON);
            if (AJAX_JSON.equalsIgnoreCase(json))
            {
                Map<String, Object> dataMap = extractRequestJsonData(req, objectMapper);
                dataMap.toString();
                if (MapUtils.isNotEmpty(dataMap))
                {
                    data = dataMap.get(paramName);
                }
            } else
            {
                data = ServletRequestUtils.getStringParameter(req, paramName);
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
     * @param request   json类型的输入流
     * @param objectMapper  jackson 实列
     * @return Map<String, Object>, 当出现错误或没有元素时返回一个 null
     */
    public static Map<String, Object> extractRequestJsonData(HttpServletRequest request, ObjectMapper objectMapper) {
        try
        {
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
                map = ConvertUtil.string2MapOfObj(jsonData, URL_PARAMETER_SEPARATOR,
                                                            SecurityConstants.KEY_VALUE_SEPARATOR);
            }
            request.setAttribute(REQUEST_PARAMETER_MAP, map);
            return map;
        }
        catch (Exception e)
        {
            return null;
        }
    }

}

