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
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.excception.IllegalAccessUrlException;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_UNAUTHENTICATION_URL;
import static top.dcenter.security.core.consts.SecurityConstants.INTERNAL_SERVER_ERROR_MSG;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 17:43
 */
@RestController
@Slf4j
public class BrowserSecurityController {

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
     * 当需要身份认证时，跳转到这里
     * @author zyw
     * @version V1.0  Created by 2020/5/3 17:43
     * @param request
     * @param response
     */
    @RequestMapping(DEFAULT_UNAUTHENTICATION_URL)
    public void requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            String targetUrl = savedRequest.getRedirectUrl();
            if (savedRequest != null && StringUtils.isNotBlank(targetUrl))
            {
                targetUrl = targetUrl.replaceFirst("^.*://[^/]*(/.*$)", "$1");
                log.info("引发跳转的请求是：{}", targetUrl);
                Iterator<Map.Entry<String, String>> iterator = browserProperties.getAuthJumpSuffixCondition().entrySet().iterator();
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
                redirectStrategy.sendRedirect(request, response, browserProperties.getLoginPage());
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalAccessUrlException(INTERNAL_SERVER_ERROR_MSG);
        }
    }

}
