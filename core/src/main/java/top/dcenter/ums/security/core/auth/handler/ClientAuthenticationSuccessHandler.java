package top.dcenter.ums.security.core.auth.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.ums.security.core.enums.LoginProcessType;
import top.dcenter.ums.security.core.properties.ClientProperties;
import top.dcenter.ums.security.core.vo.ResponseResult;
import top.dcenter.ums.security.core.vo.UserInfoJsonVo;
import top.dcenter.ums.security.core.consts.SecurityConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

import static java.util.Objects.requireNonNullElse;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.responseWithJson;

/**
 * 客户端认证成功处理器, 默认简单实现，需自己去实现.<br><br>
 * 继承 {@link BaseAuthenticationSuccessHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class ClientAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {

    protected final ClientProperties clientProperties;
    protected final ObjectMapper objectMapper;
    protected final RequestCache requestCache;

    public ClientAuthenticationSuccessHandler(ObjectMapper objectMapper, ClientProperties clientProperties) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.requestCache = new HttpSessionRequestCache();
        this.clientProperties = clientProperties;
        setTargetUrlParameter(clientProperties.getTargetUrlParameter());
        setUseReferer(clientProperties.getUseReferer());
        loginUrls = new HashSet<>();
        loginUrls.add(clientProperties.getLoginPage());
        loginUrls.add(clientProperties.getLogoutUrl());

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 客户端成功处理器,
        String username = authentication.getName();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader(SecurityConstants.HEADER_USER_AGENT);
        String sid = request.getSession(true).getId();

        log.info("登录成功: user={}, ip={}, ua={}, sid={}",
                 username, ip, userAgent, sid);

        UserInfoJsonVo userInfoJsonVo = null;
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;

        try
        {
            userInfoJsonVo = new UserInfoJsonVo(null,
                                                username,
                                                null,
                                                token.getAuthorities());

            // 设置跳转的 url
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            String targetUrl;
            if (savedRequest != null)
            {
                targetUrl = requireNonNullElse(savedRequest.getRedirectUrl(), getDefaultTargetUrl());
            } else
            {
                targetUrl = getDefaultTargetUrl();
            }

            // 判断是否返回 json 类型
            userInfoJsonVo.setUrl(targetUrl);
            if (LoginProcessType.JSON.equals(clientProperties.getLoginProcessType()))
            {
                responseWithJson(response, HttpStatus.OK.value(),
                                 objectMapper.writeValueAsString(ResponseResult.success(userInfoJsonVo)));
                return;
            }

            // 判断 accept 是否要求返回 json
            String acceptHeader = request.getHeader(SecurityConstants.HEADER_ACCEPT);
            if (StringUtils.isNotBlank(acceptHeader) && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
            {
                responseWithJson(response, HttpStatus.OK.value(),
                                 objectMapper.writeValueAsString(ResponseResult.success(userInfoJsonVo)));
                return;
            }
        }
        catch (Exception e)
        {
            log.error(String.format("设置登录成功后跳转的URL失败: error={}, user={}, ip={}, ua={}, sid={}",
                                    e.getMessage(), username, ip, userAgent, sid), e);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }


    /**
     * Builds the target URL according to the logic defined in the main class Javadoc.
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response) {
        String defaultTargetUrl = getDefaultTargetUrl();

        if (isAlwaysUseDefaultTargetUrl()) {
            return defaultTargetUrl;
        }

        // Check for the parameter and use that if available
        String targetUrl = null;

        String targetUrlParameter = getTargetUrlParameter();
        if (targetUrlParameter != null) {
            targetUrl = request.getParameter(targetUrlParameter);

            if (org.springframework.util.StringUtils.hasText(targetUrl)) {
                return targetUrl;
            }
        }


        if (useReferer && !org.springframework.util.StringUtils.hasLength(targetUrl)) {
            targetUrl = request.getHeader("Referer");
            // 当 targetUrl 为 登录 url 时, 设置为 defaultTargetUrl
            if (StringUtils.isNotBlank(targetUrl) && isLoginUrl(targetUrl))
            {
                targetUrl = defaultTargetUrl;
            }
        }

        if (!org.springframework.util.StringUtils.hasText(targetUrl)) {
            targetUrl = defaultTargetUrl;
        }

        return targetUrl;
    }

    @Override
    public void setUseReferer(boolean useReferer) {
        super.setUseReferer(useReferer);
        this.useReferer = useReferer;
    }

    /**
     * 判断 loginUrls 中是否包含 targetUrl
     * @param targetUrl 不能为 null
     * @return boolean
     */
    private boolean isLoginUrl(final String targetUrl) {
        return loginUrls.stream().anyMatch(url -> targetUrl.contains(url));
    }
}
