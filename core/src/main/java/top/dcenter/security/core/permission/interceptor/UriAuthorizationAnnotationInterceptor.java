package top.dcenter.security.core.permission.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.security.core.permission.annotation.UriAuthorize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

/**
 * 针对在方法上注释有 {@link UriAuthorize} 的访问权限拦截器,
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

        // 有访问权限
        if (hasPermission)
        {
            log.info("sid={}, user={}, ip={}, uri={}, time={}, referer={}, agent={}",
                     request.getSession(), authentication.getPrincipal(), request.getRemoteAddr(),
                     request.getRequestURI(), Instant.now().toEpochMilli(), request.getHeader("referer"),
                     request.getHeader("User-Agent"));
            return true;
        }

        // 没有访问权限
        log.warn("sid={}, user={}, ip={}, uri={}, time={}, referer={}, agent={}",
                 request.getSession(), authentication.getPrincipal(), request.getRemoteAddr(),
                 request.getRequestURI(), Instant.now().toEpochMilli(), request.getHeader("referer"),
                 request.getHeader("User-Agent"));
        uriAuthorizeService.handlerError(HttpStatus.FORBIDDEN.value(), response);

        return false;
    }


}
