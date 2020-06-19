package top.dcenter.web.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.api.controller.BaseSecurityController;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.IllegalAccessUrlException;
import top.dcenter.security.core.properties.ClientProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_SESSION_INVALID_URL;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UN_AUTHENTICATION_URL;
import static top.dcenter.security.core.util.AuthenticationUtil.redirectProcessingByLoginProcessType;

/**
 * 客户端认证 controller.<br><br> *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 17:43
 * @author zyw
 */
@Slf4j
@RestController
public class DemoSecurityController implements BaseSecurityController {

    /**
     * url regex
     */
    public static final String URL_REGEX  = "^.*://[^/]*(/.*$)";
    /**
     * 相对于 URL_REGEX 的 挂号部分
     */
    public static final String URI_$1  = "$1";

    private final RequestCache requestCache;
    private final RedirectStrategy redirectStrategy;
    private final ClientProperties clientProperties;
    private final AntPathMatcher pathMatcher;
    private final ObjectMapper objectMapper;


    public DemoSecurityController(ClientProperties clientProperties, ObjectMapper objectMapper) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.requestCache = new HttpSessionRequestCache();
        this.redirectStrategy = new DefaultRedirectStrategy();
        pathMatcher = new AntPathMatcher();
    }

    /**
     * 当需要身份认证时，跳转到这里, 根据不同 uri(支持通配符) 跳转到不同的认证入口
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    @RequestMapping(DEFAULT_UN_AUTHENTICATION_URL)
    public void requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try
        {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null)
            {
                String targetUrl = savedRequest.getRedirectUrl();

                if (StringUtils.isNotBlank(targetUrl))
                {
                    targetUrl = targetUrl.replaceFirst(URL_REGEX, URI_$1);
                    Iterator<Map.Entry<String, String>> iterator = clientProperties.getAuthRedirectSuffixCondition().entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (iterator.hasNext())
                    {
                        entry = iterator.next();
                        if (pathMatcher.match(entry.getKey(), targetUrl))
                        {
                            redirectStrategy.sendRedirect(request, response, entry.getValue());
                            return;
                        }
                    }
                }
            }
            redirectStrategy.sendRedirect(request, response, clientProperties.getLoginPage());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR, request.getRemoteAddr());
        }
    }

    @Override
    @GetMapping(DEFAULT_SESSION_INVALID_URL)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ConditionalOnProperty(prefix = "security.client", name = "invalid-session-url", havingValue = DEFAULT_SESSION_INVALID_URL)
    public void invalidSessionHandler(HttpServletRequest request, HttpServletResponse response) {

        try
        {
            redirectProcessingByLoginProcessType(request, response, clientProperties, objectMapper,
                                                 redirectStrategy, ErrorCodeEnum.INVALID_SESSION,
                                                 clientProperties.getLoginPage());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR, request.getRemoteAddr());
        }
    }


}
