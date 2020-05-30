package top.dcenter.security.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.AJAX_JSON;

/**
 * request 工具
 * @author zyw
 * @version V1.0  Created by 2020/5/30 16:19
 */
public class RequestUtil {

    /**
     * 提取 request 中指定 paramName 的值. <br>
     *     首先通过 {@link ServletWebRequest#getParameter(String)} 提取, 如果获取不到相应的值, 在判断 Header 中是否有自定义的
     *     {@link top.dcenter.security.core.consts.SecurityConstants#AJAX_JSON}, 如果有则通过
     *     {@link ServletWebRequest#getParameterMap()} 获取再通过 {@link ObjectMapper} 转化为 Map, 最后提取对应的 paramName 的值
     * @param inputStream   json类型的输入流
     * @param objectMapper  jackson 实列
     * @param paramName 参数名称
     * @return String, 当出现错误或没有元素时返回一个 null
     */
    @SuppressWarnings("JavadocReference")
    public static String extractRequestDataWithParamName(ServletWebRequest request,
                                            ObjectMapper objectMapper, String paramName) {
        try
        {
            String data = ServletRequestUtils.getStringParameter(request.getRequest(), paramName);
            String json = request.getHeader(AJAX_JSON);
            if (!StringUtils.isNotBlank(data) && AJAX_JSON.equalsIgnoreCase(json))
            {
                Map<String, String[]> parameterMap = request.getParameterMap();
                //noinspection unchecked
                Map<String, Object> map = objectMapper.readValue(parameterMap.keySet().iterator().next(), Map.class);
                data = (String) map.get(paramName);
            }
            return data;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
