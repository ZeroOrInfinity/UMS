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

package top.dcenter.ums.security.core.permission.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

/**
 * 针对在方法上注解有 {@link UriAuthorize} 的访问权限拦截器,
 * 注意: <br>
 *     1. 拦截器模式也可以使用
 *     <pre>
 *         &#64;PreAuthorize("hasPermission('/users', '/users:list')")
 *         // equivalent to
 *         &#64;UriAuthorize("/users:list")
 *     </pre>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/9 20:52
 */
@Slf4j
public class UriAuthorizationAnnotationInterceptor implements HandlerInterceptor {

    private final UriAuthorizeService uriAuthorizeService;

    public UriAuthorizationAnnotationInterceptor(UriAuthorizeService uriAuthorizeService) {
        this.uriAuthorizeService = uriAuthorizeService;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

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