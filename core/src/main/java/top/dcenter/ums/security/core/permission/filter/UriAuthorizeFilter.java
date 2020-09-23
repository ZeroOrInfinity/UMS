package top.dcenter.ums.security.core.permission.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * uri 访问权限过滤器
 * @author zyw
 * @version V1.0  Created by 2020/9/16 20:37
 */
@Slf4j
public class UriAuthorizeFilter extends OncePerRequestFilter {


    private final UriAuthorizeService uriAuthorizeService;

    /**
     * 需要进行授权的 uri 集合
      */
    private final Set<String> uriOfPermissionSet;

    private final AntPathMatcher matcher;


    public UriAuthorizeFilter(UriAuthorizeService uriAuthorizeService) {

        this.uriAuthorizeService = uriAuthorizeService;

        // uri 权限 Map(uri, Set(authority))
        uriOfPermissionSet = uriAuthorizeService.getUriAuthoritiesOfAllRole().orElse(new HashMap<>(0)).keySet();

        matcher = uriAuthorizeService.getAntPathMatcher();
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        String requestUri = MvcUtil.getUrlPathHelper().getPathWithinApplication(request);

        // 是否属于要认证的 uri
        if (uriAuthorizeService.isUriContainsInUriSet(uriOfPermissionSet, requestUri))
        {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 基于用户 role 的 Map(uri, Set(authority))
            Map<String, Set<String>> uriAuthorityOfUserRoleMap =
                    uriAuthorizeService.getUriAuthoritiesOfUserRole(authentication).orElse(new HashMap<>(0));

            // 日志参数
            Object principal = authentication.getPrincipal();
            String sid = request.getSession(true).getId();
            String ip = request.getRemoteAddr();
            String uri = request.getRequestURI();
            long now = Instant.now().toEpochMilli();
            String referer = request.getHeader("referer");
            String userAgent = request.getHeader("User-Agent");
            final String method = request.getMethod();

            // 用户是否有此 uri 的权限
            if (uriAuthorizeService.isUriContainsInUriSet(uriAuthorityOfUserRoleMap.keySet(), requestUri))
            {

                // 有访问权限
                log.info("URI权限控制-放行: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                         sid, principal, ip, MvcUtil.getServletContextPath() + uri, method, now, referer, userAgent);

                filterChain.doFilter(request, response);
                return;

            }

            // 没有访问权限
            log.warn("URI权限控制-禁止: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                     sid, principal, ip, MvcUtil.getServletContextPath() + uri, method, now, referer, userAgent);
            uriAuthorizeService.handlerError(HttpStatus.FORBIDDEN.value(), response);
            return;
        }

        filterChain.doFilter(request, response);

    }

}
