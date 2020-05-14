package top.dcenter.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.vo.SimpleResponse;
import top.dcenter.security.core.enums.LoginType;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Component
@Slf4j
public class BrowserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    private final BrowserProperties browserProperties;

    public BrowserAuthenticationFailureHandler(ObjectMapper objectMapper, BrowserProperties browserProperties) {
        this.objectMapper = objectMapper;
        this.browserProperties = browserProperties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("登录失败: {}", request.getRemoteAddr());

        if (LoginType.JSON.equals(browserProperties.getLoginType()))
        {
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(status);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(SimpleResponse.fail(status, exception.getMessage())));
            return;
        }
        setDefaultFailureUrl(browserProperties.getFailureUrl());
        super.onAuthenticationFailure(request, response, exception);
    }
}
