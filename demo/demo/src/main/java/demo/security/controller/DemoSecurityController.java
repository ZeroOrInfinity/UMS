/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package demo.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.controller.BaseSecurityController;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.IllegalAccessUrlException;
import top.dcenter.ums.security.core.util.IpUtil;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static top.dcenter.ums.security.common.consts.SecurityConstants.DEFAULT_UN_AUTHENTICATION_ROUTING_URL;

/**
 * 客户端认证 controller.<br><br> *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/3 17:43
 * @author YongWu zheng
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
    private final Map<String, String> authRedirectUrls;


    public DemoSecurityController(ClientProperties clientProperties) {
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

    /**
     * 当需要身份认证时，跳转到这里, 根据不同 uri(支持通配符) 跳转到不同的认证入口
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    @RequestMapping(DEFAULT_UN_AUTHENTICATION_ROUTING_URL)
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

                    String contextPath = request.getServletContext().getContextPath();
                    targetUrl = StringUtils.substringAfter(targetUrl, contextPath);

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
            String requestUri = request.getRequestURI();
            String ip = IpUtil.getRealIp(request);
            log.error(String.format("IllegalAccessUrlException: ip=%s, uri=%s, sid=%s, error=%s",
                                    ip,
                                    MvcUtil.getServletContextPath() + requestUri,
                                    request.getSession(true).getId(),
                                    e.getMessage()), e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR, requestUri, ip);
        }
    }


}