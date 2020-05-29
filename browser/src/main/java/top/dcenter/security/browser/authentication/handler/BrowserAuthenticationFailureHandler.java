package top.dcenter.security.browser.authentication.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.enums.LoginType;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.ResponseResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * 网页端认证失败处理器, 默认简单实现，需自己去实现.<br>
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
        log.info("登录失败: {}", request.getRemoteAddr());

        if (LoginType.JSON.equals(browserProperties.getLoginType()))
        {
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(status, exception.getMessage())));
            return;
        }
        setDefaultFailureUrl(browserProperties.getFailureUrl());
        super.onAuthenticationFailure(request, response, exception);
    }
}
