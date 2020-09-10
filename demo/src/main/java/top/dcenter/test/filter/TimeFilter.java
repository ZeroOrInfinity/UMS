package top.dcenter.test.filter;

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
 * @author  zyw
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
