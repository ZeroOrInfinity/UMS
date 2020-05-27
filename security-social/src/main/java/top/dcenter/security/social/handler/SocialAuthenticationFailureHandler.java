package top.dcenter.security.social.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import top.dcenter.security.core.enums.LoginType;
import top.dcenter.security.core.excception.RegisterUserFailureException;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.SimpleResponse;
import top.dcenter.security.social.properties.SocialProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 第三方授权登录错误处理器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class SocialAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    private final SocialProperties socialProperties;
    private final BrowserProperties browserProperties;

    public SocialAuthenticationFailureHandler(ObjectMapper objectMapper, SocialProperties socialProperties, BrowserProperties browserProperties) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.socialProperties = socialProperties;
        this.browserProperties = browserProperties;
        setDefaultFailureUrl(socialProperties.getFailureUrl());
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof RegisterUserFailureException)
        {
            log.info(exception.getMessage() + " IP={}", request.getRemoteAddr());
            request.setAttribute("error", exception.getMessage());
            response.sendRedirect(socialProperties.getSignUpUrl() + "?error=" + exception.getMessage());
            return;
        }

        if (LoginType.JSON.equals(browserProperties.getLoginType()))
        {
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(SimpleResponse.fail(status, exception.getMessage())));
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}