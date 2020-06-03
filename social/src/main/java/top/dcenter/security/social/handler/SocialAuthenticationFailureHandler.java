package top.dcenter.security.social.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.social.properties.SocialProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.util.AuthenticationUtil.authenticationFailureProcessing;

/**
 * 第三方授权登录错误处理器.<br>
 * 当用户注册异常 {@link RegisterUserFailureException} 时返回 JSON 数据
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

        if (authenticationFailureProcessing(response, exception, objectMapper, browserProperties))
        {
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}