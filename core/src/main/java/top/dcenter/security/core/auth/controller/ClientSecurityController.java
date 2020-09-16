package top.dcenter.security.core.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.security.core.api.controller.BaseSecurityController;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.IllegalAccessUrlException;
import top.dcenter.security.core.properties.ClientProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UN_AUTHENTICATION_URL;

/**
 * 客户端 url 认证与授权的路由控制.<br><br>
 * 如果要自定义客户端 url 认证与授权的路由控制，请实现 {@link BaseSecurityController} 接口，并注入 IOC 容器即可
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 17:43
 * @author
 */
@Slf4j
@ResponseBody
public class ClientSecurityController implements BaseSecurityController {

    /**
     * url regex
     */
    public static final String URL_REGEX = "^.*://[^/]*(/.*$)";
    /**
     * 相对于 URL_REGEX 的括号部分
     */
    public static final String URI_$1 = "$1";

    private final RequestCache requestCache;
    private final RedirectStrategy redirectStrategy;
    private final ClientProperties clientProperties;
    private final AntPathMatcher pathMatcher;
    private final Map<String, String> authRedirectUrls;

    public ClientSecurityController(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        this.requestCache = new HttpSessionRequestCache();
        this.redirectStrategy = new DefaultRedirectStrategy();
        authRedirectUrls = clientProperties
                .getAuthRedirectSuffixCondition()
                .stream()
                .map(pair -> pair.split("="))
                .collect(toMap(arr -> arr[0], arr -> arr[1]));
        pathMatcher = new AntPathMatcher();
    }

    @Override
    @RequestMapping(DEFAULT_UN_AUTHENTICATION_URL)
    @ConditionalOnProperty(prefix = "security.client", name = "login_un_authentication_url", havingValue = DEFAULT_UN_AUTHENTICATION_URL)
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
                    Iterator<Map.Entry<String, String>> iterator = authRedirectUrls.entrySet().iterator();
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
            String requestURI = request.getRequestURI();
            String ip = request.getRemoteAddr();
            log.error(String.format("IllegalAccessUrlException: ip={}, uri={}, sid={}, error={}",
                                    ip,
                                    requestURI,
                                    request.getSession(true).getId(),
                                    e.getMessage()), e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR, requestURI, ip);
        }
    }

}
