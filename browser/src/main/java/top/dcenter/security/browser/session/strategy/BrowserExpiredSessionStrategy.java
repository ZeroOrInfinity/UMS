package top.dcenter.security.browser.session.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.IllegalAccessUrlException;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_SESSION_INVALID_URL;
import static top.dcenter.security.core.util.AuthenticationUtil.redirectProcessingByLoginProcessType;

/**
 * Performs a redirect to a fixed URL when an expired session is detected by the
 * {@code ConcurrentSessionFilter}.
 * @author zyw
 * @version V1.0  Created by 2020/6/2 23:21
 */
@Slf4j
public class BrowserExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    private final RedirectStrategy redirectStrategy;
    private BrowserProperties browserProperties;
    private ObjectMapper objectMapper;

    public BrowserExpiredSessionStrategy(BrowserProperties browserProperties, ObjectMapper objectMapper) {
        this.browserProperties = browserProperties;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {

        if (log.isDebugEnabled())
        {
            log.debug("concurrent login session expired, Redirecting to {}", DEFAULT_SESSION_INVALID_URL);
        }

        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        try
        {
            redirectProcessingByLoginProcessType(request, response, browserProperties, objectMapper,
                                                 redirectStrategy, ErrorCodeEnum.CONCURRENT_SESSION,
                                                 browserProperties.getSession().getInvalidSessionOfConcurrentUrl());
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR);
        }
    }
}
