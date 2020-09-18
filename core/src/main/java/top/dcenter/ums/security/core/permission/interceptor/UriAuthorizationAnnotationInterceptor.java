package top.dcenter.ums.security.core.permission.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

/**
 * 针对在方法上注解有 {@link UriAuthorize} 的访问权限拦截器,
 * @author zyw
 * @version V1.0  Created by 2020/9/9 20:52
 */
@Slf4j
public class UriAuthorizationAnnotationInterceptor implements HandlerInterceptor {

    private AbstractUriAuthorizeService uriAuthorizeService;

    public UriAuthorizationAnnotationInterceptor(AbstractUriAuthorizeService uriAuthorizeService) {
        this.uriAuthorizeService = uriAuthorizeService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }

        UriAuthorize methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(UriAuthorize.class);

        // 不需要权限控制
        if (methodAnnotation == null)
        {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = uriAuthorizeService.hasPermission(request, authentication, methodAnnotation.value());

        String sid = request.getSession(true).getId();
        Object principal = authentication.getPrincipal();
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        long now = Instant.now().toEpochMilli();
        String referer = request.getHeader("referer");
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();

        // 有访问权限
        if (hasPermission)
        {
            log.info("URI权限控制-放行: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                     sid, principal, ip, uri, method, now, referer, userAgent);
            return true;
        }

        // 没有访问权限
        log.warn("URI权限控制-禁止: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                 sid, principal, ip, uri, method, now, referer, userAgent);
        uriAuthorizeService.handlerError(HttpStatus.FORBIDDEN.value(), response);

        return false;
    }


}
