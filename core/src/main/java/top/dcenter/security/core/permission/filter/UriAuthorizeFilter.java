package top.dcenter.security.core.permission.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.security.core.api.permission.service.UriAuthorizeService;
import top.dcenter.security.core.permission.config.Repeat;
import top.dcenter.security.core.permission.config.RestfulAPI;
import top.dcenter.security.core.permission.enums.PermissionSuffixType;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static top.dcenter.security.core.api.permission.service.AbstractUriAuthorizeService.PERMISSION_DELIMITER;

/**
 * uri 访问权限过滤器
 * @author zyw
 * @version V1.0  Created by 2020/9/16 20:37
 */
@Slf4j
public class UriAuthorizeFilter extends OncePerRequestFilter {

    private final UriAuthorizeService uriAuthorizeService;

    /**
     * restfulAPI 不为 null 表示为 restful API, 否则不是.
     */
    @Autowired(required = false)
    private RestfulAPI restfulAPI;
    /**
     * Repeat 不为 null 表示有需要验证权限的多个不同的 uri 对同一个 uri 都匹配的情况
     */
    @Autowired(required = false)
    private Repeat repeat;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 是否属于要认证的 uri
        if (uriAuthorizeService.isUriContainsInUriSet(uriOfPermissionSet, requestURI))
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
            if (uriAuthorizeService.isUriContainsInUriSet(uriAuthorityOfUserRoleMap.keySet(), requestURI))
            {
                // 用户的 uri 权限集合
                Set<String> uriAuthoritySet = new HashSet<>();

                Iterator<Map.Entry<String, Set<String>>> it = uriAuthorityOfUserRoleMap.entrySet().iterator();
                while (it.hasNext())
                {
                    Map.Entry<String, Set<String>> next = it.next();
                    if (matcher.match(next.getKey(), requestURI))
                    {
                        // 添加基于用户 role 的 uri 权限集合
                        uriAuthoritySet.addAll(next.getValue());
                        if (repeat == null)
                        {
                            // 只添加第一次匹配 uri 权限集合
                            break;
                        }
                        // 添加所有匹配的 uri 权限集合(耗时)
                    }
                }

                // 基于用户 authentication 的 uri 的权限集合
                Set<String> userAuthoritySet =
                        authentication.getAuthorities()
                                .stream()
                                .map(grantedAuth -> grantedAuth.getAuthority())
                                .flatMap(authorities -> Arrays.stream(StringUtils.splitByWholeSeparator(authorities, PERMISSION_DELIMITER)))
                                .collect(Collectors.toSet());

                // 两个集合的交集
                uriAuthoritySet.retainAll(userAuthoritySet);


                // 用户有 uri 的权限
                if (uriAuthoritySet.size() > 0)
                {
                    boolean hasPermission = true;

                    // 如果是 restful API 则判断权限后缀是否与 request method 是否
                    if (restfulAPI != null)
                    {
                        hasPermission = uriAuthoritySet.stream()
                                        .anyMatch(
                                                authority -> authority.endsWith(PermissionSuffixType.getPermissionSuffix(method))
                                        );
                    }

                    if (hasPermission)
                    {
                        // 有访问权限
                        log.info("URI权限控制-放行: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                                 sid, principal, ip, uri, method, now, referer, userAgent);
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

            }

            // 没有访问权限
            log.warn("URI权限控制-禁止: sid={}, user={}, ip={}, uri={}, method={}, time={}, referer={}, agent={}",
                     sid, principal, ip, uri, method, now, referer, userAgent);
            uriAuthorizeService.handlerError(HttpStatus.FORBIDDEN.value(), response);
            return;
        }

        filterChain.doFilter(request, response);

    }
}
