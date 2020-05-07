package top.dcenter.security.core.validate.code;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.security.core.excception.ValidateCodeException;
import top.dcenter.security.core.util.CastUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.GET_METHOD;
import static top.dcenter.security.core.consts.SecurityConstants.URI_SEPARATOR;


/**
 * @author zyw
 * @version V1.0  Created by 2020/5/4 9:29
 */
@Slf4j
@Component
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private final ValidateCodeProperties validateCodeProperties;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final AntPathMatcher pathMatcher;
    /**
     * 校验码处理器 Holder
     */
    private final ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Getter
    @Setter
    private Map<String, ValidateCodeType> authUrlMap = new HashMap<>();

    public ValidateCodeFilter(AuthenticationFailureHandler authenticationFailureHandler,
                              ValidateCodeProcessorHolder validateCodeProcessorHolder,
                              ValidateCodeProperties validateCodeProperties) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        pathMatcher = new AntPathMatcher();
        this.validateCodeProcessorHolder = validateCodeProcessorHolder;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        // 添加短信验证码 urls
        CastUtil.string2Map(validateCodeProperties.getImage().getAuthUrls(), URI_SEPARATOR, ValidateCodeType.IMAGE,
                authUrlMap);
        // 添加图片验证码 urls
        CastUtil.string2Map(validateCodeProperties.getSms().getAuthUrls(), URI_SEPARATOR, ValidateCodeType.SMS, authUrlMap);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // 校验码逻辑，当短信验证码与图片验证码 url 相同时，优先使用短信校验码逻辑。
        ValidateCodeType validateCodeType = getValidateCodeType(request);

        try {
            if (validateCodeType != null)
            {
                validateCodeProcessorHolder.findValidateCodeProcessor(validateCodeType)
                        .validate(new ServletWebRequest(request, response));
                log.info("校验请求({})验证码校验通过", requestURI);
            }

        } catch (Exception e) {
            log.warn("校验请求({}), IP={}: {}", requestURI, request.getRemoteAddr(), e.getMessage(), e);
            authenticationFailureHandler.onAuthenticationFailure(request, response, new ValidateCodeException(e.getMessage(), e));
            return;
        }


        doFilter(request, response, filterChain);
    }

    /**
     * 获取校验码的类型，如果当前请求不需要校验，则返回null
     *
     * @param request
     * @return
     * @exception AuthenticationServiceException
     */
    private ValidateCodeType getValidateCodeType(HttpServletRequest request) {
        ValidateCodeType result;
        String method = request.getMethod();
        if (!StringUtils.equalsIgnoreCase(method, GET_METHOD)) {
            String requestURI = request.getRequestURI();
            result = authUrlMap.getOrDefault(requestURI, null);
            if (result != null)
            {
                return result;
            }

            Iterator<String> iterator = authUrlMap.keySet().iterator();
            while (iterator.hasNext())
            {
                String url = iterator.next();
                if (pathMatcher.match(url, requestURI))
                {
                    return authUrlMap.get(url);
                }
            }
        }
        return null;
    }

}
