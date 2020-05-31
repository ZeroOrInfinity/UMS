package top.dcenter.security.core.authentication.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.enums.LoginPostProcessType;
import top.dcenter.security.core.properties.BrowserProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.CHARSET_UTF8;

/**
 * 网页端认证成功处理器, 默认简单实现，需自己去实现.<br>
 * 继承 {@link BaseAuthenticationSuccessHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class BrowserAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {

    protected final BrowserProperties browserProperties;
    protected final ObjectMapper objectMapper;
    protected final RequestCache requestCache;

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
        if (LoginPostProcessType.JSON.equals(browserProperties.getLoginPostProcessType()))
        {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.getWriter().write(objectMapper.writeValueAsString(authentication));
            return;
        }

        try
        {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null)
            {
                String targetUrl = savedRequest.getRedirectUrl();
                if (log.isDebugEnabled())
                {
                    log.debug("引发跳转的请求是：{}", targetUrl);
                }
                if (StringUtils.isNotBlank(targetUrl))
                {
                    super.setDefaultTargetUrl(targetUrl);
                }
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
