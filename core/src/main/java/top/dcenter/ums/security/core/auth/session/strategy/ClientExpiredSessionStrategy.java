package top.dcenter.ums.security.core.auth.session.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.util.AntPathMatcher;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.ExpiredSessionDetectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.determineRedirectUrl;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.redirectProcessingByLoginProcessType;

/**
 * Performs a redirect to a fixed URL when an expired session is detected by the
 * {@code ConcurrentSessionFilter}.
 * @author zyw
 * @version V1.0  Created by 2020/6/2 23:21
 */
@Slf4j
public class ClientExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    private final RedirectStrategy redirectStrategy;
    private ClientProperties clientProperties;
    private RequestCache requestCache;
    private final AntPathMatcher matcher;

    public ClientExpiredSessionStrategy(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        this.matcher = new AntPathMatcher();
        this.redirectStrategy = new DefaultRedirectStrategy();
        this.requestCache = new HttpSessionRequestCache();
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {

        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();
        HttpSession session = request.getSession(true);

        try
        {
            // 清除缓存
            session.removeAttribute(SESSION_ENHANCE_CHECK_KEY);

            String redirectUrl = determineRedirectUrl(request, response, clientProperties.getLoginPage(), matcher, requestCache);
            if (log.isDebugEnabled())
            {
                log.debug("Session expired, starting new session and redirecting to '{}'", redirectUrl);
            }

            redirectProcessingByLoginProcessType(request, response, clientProperties,
                                                 redirectStrategy, ErrorCodeEnum.EXPIRED_SESSION,
                                                 redirectUrl);
        }
        catch (Exception e)
        {
            log.error(String.format("SESSION过期处理失败: error=%s, ip=%s, sid=%s, uri=%s",
                                    e.getMessage(), request.getRemoteAddr(), session.getId(), request.getRequestURI()), e);
            throw new ExpiredSessionDetectedException(ErrorCodeEnum.SERVER_ERROR, session.getId());
        }
    }
}
