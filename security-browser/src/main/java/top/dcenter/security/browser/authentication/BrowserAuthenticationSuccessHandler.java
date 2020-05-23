package top.dcenter.security.browser.authentication;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.enums.LoginType;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 网页端认证成功处理器, 默认无实现，需自己去实现
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Component
@Slf4j
public class BrowserAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final BrowserProperties browserProperties;
    private final ObjectMapper objectMapper;
    private final RequestCache requestCache;
    public BrowserAuthenticationSuccessHandler(ObjectMapper objectMapper, BrowserProperties browserProperties) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.requestCache = new HttpSessionRequestCache();
        this.browserProperties = browserProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 网页端成功处理器, 默认无实现，需自己去实现
        log.info("登录成功: {}", authentication.getName());
        if (LoginType.JSON.equals(browserProperties.getLoginType()))
        {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(authentication));
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
