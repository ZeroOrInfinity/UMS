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
package top.dcenter.ums.security.core.mdc.filter;

import me.zhyd.oauth.utils.UuidUtils;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.mdc.properties.MdcProperties;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * MDC 机制实现日志的链路追踪: 在输出日志中加上 mdcKey
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/31 18:19
 */
public class MdcLogFilter extends OncePerRequestFilter {

    /**
     * 在输出日志中加上指定的 MDC_TRACE_ID
     */
    public static final String MDC_KEY = "MDC_TRACE_ID";

    private final Set<String> includeUrls;
    private final Set<String> excludeUrls;
    private final AntPathMatcher matcher;

    public MdcLogFilter(MdcProperties mdcProperties, ClientProperties clientProperties) {
        this.matcher = new AntPathMatcher();
        this.includeUrls = new HashSet<>();
        this.excludeUrls = new HashSet<>();

        includeUrls.addAll(mdcProperties.getIncludeUrls());

        final String[] ignoringUrls = clientProperties.getIgnoringUrls();
        if (null != ignoringUrls) {
            this.excludeUrls.addAll(Arrays.asList(ignoringUrls));
        }
        final Set<String> excludeUrls = mdcProperties.getExcludeUrls();
        if (null != excludeUrls) {
            this.excludeUrls.addAll(excludeUrls);
        }
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isMdc(request)) {
            String token = UuidUtils.getUUID();
            MDC.put(MDC_KEY, token);
            filterChain.doFilter(request, response);
            MDC.remove(MDC_KEY);
            return;
        }

        filterChain.doFilter(request, response);

    }

    private boolean isMdc(HttpServletRequest request) {
        final String requestUri = MvcUtil.getUrlPathHelper().getPathWithinApplication(request);
        for (String excludeUrl : excludeUrls) {
            if (this.matcher.match(excludeUrl, requestUri)) {
                return false;
            }
        }
        for (String includeUrl : includeUrls) {
            if (this.matcher.match(includeUrl, requestUri)) {
                return true;
            }
        }
        return false;
    }
}