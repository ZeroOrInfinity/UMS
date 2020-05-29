package top.dcenter.security.browser.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.security.browser.api.controller.BaseBrowserSecurityController;
import top.dcenter.security.core.exception.IllegalAccessUrlException;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UNAUTHENTICATION_URL;
import static top.dcenter.security.core.consts.SecurityConstants.INTERNAL_SERVER_ERROR_MSG;

/**
 * 网页端认证 controller.<br>
 * 如果要自定义网页端 url 认证与授权的路由控制，请实现 {@link BaseBrowserSecurityController} 接口，并注入 IOC 容器即可
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 17:43
 * @medifiedBy zyw
 */
@Slf4j
@ResponseBody
public class BrowserSecurityController implements BaseBrowserSecurityController {

    private final RequestCache requestCache;
    private final RedirectStrategy redirectStrategy;
    private final BrowserProperties browserProperties;
    private final AntPathMatcher pathMatcher;


    public BrowserSecurityController(BrowserProperties browserProperties) {
        this.browserProperties = browserProperties;
        this.requestCache = new HttpSessionRequestCache();
        this.redirectStrategy = new DefaultRedirectStrategy();
        pathMatcher = new AntPathMatcher();
    }

    /**
     * 当需要身份认证时，跳转到这里, 根据不同 uri(支持通配符) 跳转到不同的认证入口
     *
     * @param request
     * @param response
     * @author zhailiang
     * @medifiedBy zyw
     * @version V1.0  Created by 2020/5/3 17:43
     */
    @Override
    @RequestMapping(DEFAULT_UNAUTHENTICATION_URL)
    public void requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try
        {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null)
            {
                String targetUrl = savedRequest.getRedirectUrl();
                if (log.isInfoEnabled())
                {
                    log.info("引发跳转的请求是：{}", targetUrl);
                }
                if (StringUtils.isNotBlank(targetUrl))
                {
                    targetUrl = targetUrl.replaceFirst("^.*://[^/]*(/.*$)", "$1");
                    Iterator<Map.Entry<String, String>> iterator = browserProperties.getAuthRedirectSuffixCondition().entrySet().iterator();
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
            redirectStrategy.sendRedirect(request, response, browserProperties.getLoginPage());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new IllegalAccessUrlException(INTERNAL_SERVER_ERROR_MSG);
        }
    }

}
