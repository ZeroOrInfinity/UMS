package top.dcenter.authentication.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.authentication.handler.BrowserAuthenticationFailureHandler;
import top.dcenter.security.core.enums.LoginPostProcessType;
import top.dcenter.security.core.exception.RegisterUserFailureException;
import top.dcenter.security.core.exception.ValidateCodeException;
import top.dcenter.security.core.properties.BrowserProperties;
import top.dcenter.security.core.vo.ResponseResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * 网页端认证失败处理器.<br>
 *     当用户注册异常 {@link RegisterUserFailureException} 或验证码异常 {@link ValidateCodeException} 时返回 JSON 数据
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Component
@Slf4j
public class DemoAuthenticationFailureHandler extends BrowserAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final BrowserProperties browserProperties;

    public DemoAuthenticationFailureHandler(ObjectMapper objectMapper, BrowserProperties browserProperties) {
        super(objectMapper,browserProperties);
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.browserProperties = browserProperties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("demo ===> 登录失败: {}", request.getRemoteAddr());

        // 第三方首次授权登录注册时，用户名重名处理
        if (exception instanceof RegisterUserFailureException)
        {
            RegisterUserFailureException e = (RegisterUserFailureException) exception;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(e.getErrorCodeEnum().getCode(),
                                                                                           e.getMessage())));
            return;
        }

        // 验证码异常时，通过 JSON 返回
        if (exception instanceof ValidateCodeException)
        {
            ValidateCodeException e = (ValidateCodeException) exception;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(ResponseResult.fail(e.getErrorCodeEnum().getCode(),
                                                                                           e.getMessage())));
            return;
        }

        if (LoginPostProcessType.JSON.equals(browserProperties.getLoginPostProcessType()))
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
