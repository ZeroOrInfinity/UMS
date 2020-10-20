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

package demo.test.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;

/**
 * 对 Servlet 类的方法运行时间的统计
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 15:53
 */
@Slf4j
public class TimeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (log.isDebugEnabled())
        {
            log.debug("TimeFilter.init");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("TimeFilter.doFilter start");
        log.info("\n\tservletRequest = {}\n\tservletResponse = {}\n\tfilterChain = {}", servletRequest, servletResponse, filterChain);
        Instant start = Instant.now();
        filterChain.doFilter(servletRequest, servletResponse);
        String ip = servletRequest.getRemoteAddr();
        log.info("TimeFilter.doFilter end。{} request {} duration = {}", ip,
                 ((HttpServletRequest) servletRequest).getRequestURI(),
                 (Instant.now().toEpochMilli() - start.toEpochMilli()));
    }

    @Override
    public void destroy() {
        if (log.isDebugEnabled())
        {
            log.debug("TimeFilter.destroy");
        }
    }
}