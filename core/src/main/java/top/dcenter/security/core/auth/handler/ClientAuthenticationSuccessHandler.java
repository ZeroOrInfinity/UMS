package top.dcenter.security.core.auth.handler;

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
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationSuccessHandler;
import top.dcenter.security.core.enums.LoginProcessType;
import top.dcenter.security.core.properties.ClientProperties;
import top.dcenter.security.core.vo.ResponseResult;
import top.dcenter.security.core.vo.UserInfoJsonVo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.requireNonNullElse;
import static top.dcenter.security.core.consts.SecurityConstants.HEADER_ACCEPT;
import static top.dcenter.security.core.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.security.core.util.AuthenticationUtil.responseWithJson;

/**
 * 客户端认证成功处理器, 默认简单实现，需自己去实现.<br>
 * 继承 {@link BaseAuthenticationSuccessHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @medifiedBy  zyw
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
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 客户端成功处理器,
        log.info("登录成功: user={}, ip={}, ua={}, sid={}",
                 authentication.getName(),
                 request.getRemoteAddr(),
                 request.getHeader(HEADER_USER_AGENT),
                 request.getSession(true).getId());

        UserInfoJsonVo userInfoJsonVo = null;
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;

        try
        {
            userInfoJsonVo = new UserInfoJsonVo(null,
                                                authentication.getName(),
                                                null,
                                                token.getAuthorities());

            // 设置跳转的 url
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null)
            {
                String targetUrl = savedRequest.getRedirectUrl();

                if (StringUtils.isNotBlank(targetUrl))
                {
                    setDefaultTargetUrl(targetUrl);

                }
            }

            // 判断是否返回 json 类型
            userInfoJsonVo.setUrl(requireNonNullElse(getDefaultTargetUrl(), "/"));
            if (LoginProcessType.JSON.equals(clientProperties.getLoginProcessType()))
            {
                responseWithJson(response, HttpStatus.OK.value(),
                                 objectMapper.writeValueAsString(ResponseResult.success(userInfoJsonVo)));
                return;
            }
            // 判断 accept 是否要求返回 json
            String acceptHeader = request.getHeader(HEADER_ACCEPT);
            if (StringUtils.isNotBlank(acceptHeader) && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
            {
                responseWithJson(response, HttpStatus.OK.value(),
                                 objectMapper.writeValueAsString(ResponseResult.success(userInfoJsonVo)));
                return;
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
