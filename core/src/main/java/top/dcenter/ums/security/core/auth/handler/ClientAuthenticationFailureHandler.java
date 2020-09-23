package top.dcenter.ums.security.core.auth.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.auth.filter.AjaxOrFormRequestFilter;
import top.dcenter.ums.security.core.consts.SecurityConstants;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.ums.security.core.properties.ClientProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.authenticationFailureProcessing;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.getAbstractResponseJsonAuthenticationException;

/**
 * 客户端认证失败处理器, 默认简单实现，需自己去实现.<br><br>
 * 继承 {@link BaseAuthenticationFailureHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class ClientAuthenticationFailureHandler extends BaseAuthenticationFailureHandler {
    protected final ObjectMapper objectMapper;
    protected final ClientProperties clientProperties;

    public ClientAuthenticationFailureHandler(ObjectMapper objectMapper, ClientProperties clientProperties) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.clientProperties = clientProperties;
        setDefaultFailureUrl(clientProperties.getFailureUrl());
    }

    /**
     * {@link AbstractResponseJsonAuthenticationException} 类的异常, Response 会返回 Json 数据.
     * @param request request
     * @param response  response
     * @param exception {@link AbstractResponseJsonAuthenticationException} 类的异常, Response 会返回 Json 数据.
     * @throws IOException  IOException
     * @throws ServletException ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        AbstractResponseJsonAuthenticationException e = getAbstractResponseJsonAuthenticationException(exception);

        String reqData;
        if (request instanceof AjaxOrFormRequestFilter.AjaxOrFormRequest)
        {
            AjaxOrFormRequestFilter.AjaxOrFormRequest ajaxOrFormRequest =
                    ((AjaxOrFormRequestFilter.AjaxOrFormRequest) request);
            reqData = new String(ajaxOrFormRequest.getBody(), StandardCharsets.UTF_8);
        }
        else
        {
            reqData = request.getParameterMap().toString();
        }
        log.info("登录失败: user={}, ip={}, ua={}, sid={}, reqData={}",
                 e == null ? null : e.getUid(),
                 request.getRemoteAddr(),
                 request.getHeader(SecurityConstants.HEADER_USER_AGENT),
                 request.getSession(true).getId(),
                 reqData);


        // 检测是否接收 json 格式
        String acceptHeader = request.getHeader(SecurityConstants.HEADER_ACCEPT);

        // 返回 json 格式
        if (authenticationFailureProcessing(response, exception, e, acceptHeader, objectMapper, clientProperties))
        {
            // 进行必要的清理
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }

}
