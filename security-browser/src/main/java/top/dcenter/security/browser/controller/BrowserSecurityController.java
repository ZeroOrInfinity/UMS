package top.dcenter.security.browser.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.SimpleResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


    public BrowserSecurityController(BrowserProperties browserProperties) {
        this.browserProperties = browserProperties;
        this.requestCache = new HttpSessionRequestCache();
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    /**
     * 当需要身份认证时，跳转到这里
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/authentication/require")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null)
            {
                String targetUrl = savedRequest.getRedirectUrl();
                log.info("引发跳转的请求是：{}", targetUrl);
                if (StringUtils.endsWithIgnoreCase(targetUrl, browserProperties.getAuthJumpSuffixCondition()))
                {
                    redirectStrategy.sendRedirect(request, response, browserProperties.getLoginPage());
                    return null;
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return SimpleResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value() , "服务器开小差，请重试");
        }
        return SimpleResponse.fail(HttpStatus.BAD_REQUEST.value(), "访问的服务需要身份认证，请引导用户到登录页");
    }
}
