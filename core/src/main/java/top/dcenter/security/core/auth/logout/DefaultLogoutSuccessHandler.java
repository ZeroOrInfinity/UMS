package top.dcenter.security.core.auth.logout;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.dcenter.security.core.api.service.CacheUserDetailsService;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.properties.ClientProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;
import static top.dcenter.security.core.util.AuthenticationUtil.redirectProcessingByLoginProcessType;

/**
 * 登出成功处理器
 * @author zyw
 * @version V1.0  Created by 2020/6/4 23:20
 */
@Slf4j
public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedirectStrategy redirectStrategy;
    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;
    private CacheUserDetailsService cacheUserDetailsService;

    public DefaultLogoutSuccessHandler(ClientProperties clientProperties, ObjectMapper objectMapper, CacheUserDetailsService cacheUserDetailsService) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.cacheUserDetailsService = cacheUserDetailsService;
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();

        log.info("登出成功: user={}, ip={}, ua={}, sid={}",
                 authentication.getPrincipal(),
                 Instant.now().toEpochMilli(),
                 request.getRemoteAddr(),
                 session.getId(),
                 session.getAttribute(SESSION_ENHANCE_CHECK_KEY));

        // 清楚缓存
        session.removeAttribute(SESSION_ENHANCE_CHECK_KEY);
        if (cacheUserDetailsService != null)
        {
            cacheUserDetailsService.removeUserFromCache(authentication.getName());
        }

        redirectProcessingByLoginProcessType(request, response, clientProperties, objectMapper,
                                             redirectStrategy, ErrorCodeEnum.CONCURRENT_SESSION,
                                             clientProperties.getLogoutSuccessUrl());
    }
}
