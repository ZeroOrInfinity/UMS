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

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.common.utils.UrlUtil;
import top.dcenter.ums.security.core.api.mdc.MdcIdGenerator;
import top.dcenter.ums.security.core.mdc.MdcIdType;
import top.dcenter.ums.security.core.mdc.properties.MdcProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcId;

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

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    private MdcIdGenerator mdcIdGenerator;

    private final Set<String> includeUrls;
    private final Set<String> excludeUrls;
    private final AntPathMatcher matcher;
    private final MdcIdType idType;

    public MdcLogFilter(MdcProperties mdcProperties) {
        this.idType = mdcProperties.getType();
        this.matcher = new AntPathMatcher();
        this.includeUrls = new HashSet<>();
        this.excludeUrls = new HashSet<>();

        includeUrls.addAll(mdcProperties.getIncludeUrls());

        final Set<String> excludeUrls = mdcProperties.getExcludeUrls();
        if (null != excludeUrls) {
            this.excludeUrls.addAll(excludeUrls);
        }
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (isEnableMdc(request)) {
            String token = getMdcId(this.idType, this.mdcIdGenerator);
            MDC.put(MDC_KEY, token);
            try {
                filterChain.doFilter(request, response);
            }
            catch (Exception e) {
                request.setAttribute(MDC_KEY, token);
                MDC.clear();
                throw e;
            }
            MDC.clear();
            return;
        }

        filterChain.doFilter(request, response);

    }

    private boolean isEnableMdc(HttpServletRequest request) {
        final String requestUri = UrlUtil.getUrlPathHelper().getPathWithinApplication(request);
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