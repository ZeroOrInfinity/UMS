package top.dcenter.security.browser.logout;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.properties.BrowserProperties;

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
    private final BrowserProperties browserProperties;
    private final ObjectMapper objectMapper;

    public DefaultLogoutSuccessHandler(BrowserProperties browserProperties, ObjectMapper objectMapper) {
        this.browserProperties = browserProperties;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();

        if (log.isInfoEnabled())
        {
            log.info("{} 登出成功, 时间={}, ip={}, sessionId={}, checkValue={}",
                     authentication.getPrincipal(),
                     Instant.now().toEpochMilli(),
                     request.getRemoteAddr(),
                     session.getId(),
                     session.getAttribute(SESSION_ENHANCE_CHECK_KEY));
        }

        session.removeAttribute(SESSION_ENHANCE_CHECK_KEY);

        redirectProcessingByLoginProcessType(request, response, browserProperties, objectMapper,
                                             redirectStrategy, ErrorCodeEnum.CONCURRENT_SESSION,
                                             browserProperties.getLogoutSuccessUrl());
    }
}
