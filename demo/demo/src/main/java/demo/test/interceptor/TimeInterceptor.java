package demo.test.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

/**
 * 拦截器方式的针对方法的时间统计
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/2 16:20
 */
@Component
@Slf4j
public class TimeInterceptor extends HandlerInterceptorAdapter {

    private ThreadLocal<Long> timeThreadLocal;

    public TimeInterceptor() {
        super();
        timeThreadLocal = new ThreadLocal<>();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("TimeInterceptor.preHandle");
        timeThreadLocal.set(Instant.now().toEpochMilli());
        log.info("-----------------------> {}", ((HandlerMethod) handler).getMethod().getName());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("TimeInterceptor.postHandle");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("TimeInterceptor.afterCompletion");
        if (ex != null)
        {
            log.info("error: {}, msg: {}", ex.getClass().getName(), ex.getMessage());
        }
        log.info("request duration: {}", (Instant.now().toEpochMilli() - timeThreadLocal.get().longValue()));
        timeThreadLocal.remove();
    }

}
