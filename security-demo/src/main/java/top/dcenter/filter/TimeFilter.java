package top.dcenter.filter;

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
 * @author zyw
 * @version V1.0  Created by 2020/5/2 15:53
 */
@Slf4j
//@Component
public class TimeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("TimeFilter.init");
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
        log.debug("TimeFilter.destroy");
    }
}
