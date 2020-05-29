package top.dcenter.security.social.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import top.dcenter.security.core.enums.LoginType;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.ResponseResult;
import top.dcenter.security.social.properties.SocialProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

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

        // 第三方首次授权登录注册时，用户名重名处理
        // todo SocialAuthenticationSignUpToken 传递不过来，被ProviderManager给拦截了
        if (exception instanceof RegisterUserFailureException)
        {
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(status, exception.getMessage())));
            return;
        }

        if (LoginType.JSON.equals(browserProperties.getLoginType()))
        {
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(status, exception.getMessage())));
            return;
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}