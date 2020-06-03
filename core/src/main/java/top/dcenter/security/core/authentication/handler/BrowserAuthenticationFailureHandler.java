package top.dcenter.security.core.authentication.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.core.exception.ValidateCodeException;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.util.AuthenticationUtil.authenticationFailureProcessing;

/**
 * 网页端认证失败处理器, 默认简单实现，需自己去实现.<br>
 *     当用户注册异常 {@link RegisterUserFailureException} 或验证码异常 {@link ValidateCodeException} 时返回 JSON 数据.<br>
 * 继承 {@link BaseAuthenticationFailureHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class BrowserAuthenticationFailureHandler extends BaseAuthenticationFailureHandler {
    protected final ObjectMapper objectMapper;
    protected final BrowserProperties browserProperties;

    public BrowserAuthenticationFailureHandler(ObjectMapper objectMapper, BrowserProperties browserProperties) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.browserProperties = browserProperties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (log.isInfoEnabled())
        {
            log.info("登录失败: {}", request.getRemoteAddr());
        }

        if (authenticationFailureProcessing(response, exception, objectMapper, browserProperties))
        {
            return;
        }

        setDefaultFailureUrl(browserProperties.getFailureUrl());
        super.onAuthenticationFailure(request, response, exception);
    }

}
