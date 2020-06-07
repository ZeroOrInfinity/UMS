package top.dcenter.security.core.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.dcenter.security.core.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 支持 Json 解析
 * @author zyw
 * @version V1.0  Created by 2020/6/7 11:14
 */
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private ObjectMapper objectMapper;

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return (String) RequestUtil.getParameter(request, objectMapper, getUsernameParameter());
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return (String) RequestUtil.getParameter(request, objectMapper, getPasswordParameter());
    }
}
