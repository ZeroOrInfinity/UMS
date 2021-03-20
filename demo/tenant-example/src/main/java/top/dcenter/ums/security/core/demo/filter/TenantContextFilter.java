package top.dcenter.ums.security.core.demo.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.core.demo.tenant.UmsTenantContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理租户 id 过滤器
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2021-03-20 17:09
 */
@RequiredArgsConstructor
@Slf4j
public class TenantContextFilter extends OncePerRequestFilter {

    private final UmsTenantContextHolder tenantContextHolder;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            tenantContextHolder.tenantIdHandle(request, null);
        }
        catch (Exception e) {
            log.error("解析多租户 id 错误", e);
        }
        filterChain.doFilter(request, response);

        tenantContextHolder.removeContext();
    }
}
