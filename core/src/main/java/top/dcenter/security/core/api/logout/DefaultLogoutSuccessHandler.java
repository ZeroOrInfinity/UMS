package top.dcenter.security.core.api.logout;

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

import static top.dcenter.security.core.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.security.core.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;
import static top.dcenter.security.core.util.AuthenticationUtil.redirectProcessingLogoutByLoginProcessType;

/**
 * 登出成功处理器, 如要替换此类, 继承后注入 IOC 容器即可
 * @author zyw
 * @version V1.0  Created by 2020/6/4 23:20
 */
@Slf4j
public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    protected final RedirectStrategy redirectStrategy;
    protected final ClientProperties clientProperties;
    protected final ObjectMapper objectMapper;
    protected CacheUserDetailsService cacheUserDetailsService;

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
                 authentication != null ? authentication.getPrincipal() : "",
                 request.getRemoteAddr(),
                 request.getHeader(HEADER_USER_AGENT),
                 session.getId(),
                 session.getAttribute(SESSION_ENHANCE_CHECK_KEY));

        // 清理缓存
        session.removeAttribute(SESSION_ENHANCE_CHECK_KEY);
        if (cacheUserDetailsService != null && authentication != null)
        {
            cacheUserDetailsService.removeUserFromCache(authentication.getName());
        }

        redirectProcessingLogoutByLoginProcessType(request, response, clientProperties, objectMapper,
                                                   redirectStrategy, ErrorCodeEnum.CONCURRENT_SESSION);
    }
}
