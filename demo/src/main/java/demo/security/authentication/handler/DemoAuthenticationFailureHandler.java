package demo.security.authentication.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.security.core.properties.ClientProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.HEADER_ACCEPT;
import static top.dcenter.security.core.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.security.core.util.AuthenticationUtil.authenticationFailureProcessing;
import static top.dcenter.security.core.util.AuthenticationUtil.getAbstractResponseJsonAuthenticationException;

/**
 * 客户端认证失败处理器.<br><br>
 *     当发生异常 {@link AbstractResponseJsonAuthenticationException} 时返回 JSON 数据
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Component
@Slf4j
public class DemoAuthenticationFailureHandler extends BaseAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final ClientProperties clientProperties;

    public DemoAuthenticationFailureHandler(ObjectMapper objectMapper, ClientProperties clientProperties) {
        this.objectMapper = objectMapper;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.clientProperties = clientProperties;
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

        log.info("demo ========> 登录失败: {}, user={}, ip={}, ua={}, sid={}",
                 exception.getMessage(),
                 e == null ? null : e.getUid(),
                 request.getRemoteAddr(),
                 request.getHeader(HEADER_USER_AGENT),
                 request.getSession(true).getId());

        // 进行必要的缓存清理

        // 检测是否接收 json 格式
        String acceptHeader = request.getHeader(HEADER_ACCEPT);

        if (authenticationFailureProcessing(response, exception, e, acceptHeader, objectMapper, clientProperties))
        {
            // 进行必要的清理缓存
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
