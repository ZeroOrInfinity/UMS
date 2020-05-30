package top.dcenter.security.core.validate.code;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.security.core.enums.ValidateCodeType;
import top.dcenter.security.core.exception.ValidateCodeException;
import top.dcenter.security.core.properties.ValidateCodeProperties;
import top.dcenter.security.core.util.ConvertUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.GET_METHOD;
import static top.dcenter.security.core.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;


/**
 * 验证码过滤器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 9:29
 */
@Slf4j
@Component
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private final ValidateCodeProperties validateCodeProperties;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final AntPathMatcher pathMatcher;
    /**
     * 验证码处理器 Holder
     */
    private final ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Getter
    @Setter
    private Map<String, ValidateCodeType> authUrlMap = new HashMap<>();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ValidateCodeFilter(ValidateCodeProcessorHolder validateCodeProcessorHolder,
                              AuthenticationFailureHandler authenticationFailureHandler,
                              ValidateCodeProperties validateCodeProperties) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        pathMatcher = new AntPathMatcher();
        this.validateCodeProcessorHolder = validateCodeProcessorHolder;
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        // 添加图片验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getImage().getAuthUrls(), ValidateCodeType.IMAGE,
                             authUrlMap);
        // 添加短信验证码 urls
        ConvertUtil.list2Map(validateCodeProperties.getSms().getAuthUrls(), ValidateCodeType.SMS, authUrlMap);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        // 验证码逻辑，当短信验证码与图片验证码 url 相同时，优先使用短信验证码逻辑。
        ValidateCodeType validateCodeType = getValidateCodeType(request);

        try {
            if (validateCodeType != null)
            {
                ValidateCodeProcessor validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(validateCodeType);
                if (validateCodeProcessor != null)
                {
                    validateCodeProcessor.validate(new ServletWebRequest(request, response));
                    if (log.isInfoEnabled())
                    {
                        log.info("校验请求({})验证码校验通过", requestURI);
                    }
                } else
                {
                    throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE);
                }
            }

        } catch (Exception e) {
            log.warn("校验请求({}), IP={}: {}", requestURI, request.getRemoteAddr(), e.getMessage());

            AuthenticationException ex;
            if (e instanceof AuthenticationException)
            {
                ex = (AuthenticationException) e;
            }
            else
            {
                ex = new AuthenticationServiceException(e.getMessage(), e);
            }
            authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
            return;
        }

        doFilter(request, response, filterChain);
    }

    /**
     * 获取验证码的类型，如果当前请求不需要校验，则返回null
     *
     * @param request   HttpServletRequest
     * @return  ValidateCodeType
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

            for (Map.Entry<String, ValidateCodeType> next : authUrlMap.entrySet())
            {
                if (pathMatcher.match(next.getKey(), requestURI))
                {
                    return next.getValue();
                }
            }
        }
        return null;
    }

}
