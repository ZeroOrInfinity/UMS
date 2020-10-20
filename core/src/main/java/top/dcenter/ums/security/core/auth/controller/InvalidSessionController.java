package top.dcenter.ums.security.core.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.IllegalAccessUrlException;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.getOriginalUrl;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.redirectProcessingByLoginProcessType;

/**
 * session 失效后处理的控制器.<br><br>
 * 如果要自定义控制器，请继承 {@link InvalidSessionController} 类，并注入 IOC 容器即可
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/3 17:43
 */
@Slf4j
@ResponseBody
public class InvalidSessionController implements InitializingBean {

    private final RedirectStrategy redirectStrategy;
    private final ClientProperties clientProperties;
    private final RequestCache requestCache;

    @Autowired
    private GenericApplicationContext applicationContext;

    public InvalidSessionController(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        this.requestCache = new HttpSessionRequestCache();
        this.redirectStrategy = new DefaultRedirectStrategy();
    }


    @RequestMapping(value = "${ums.client.invalidSessionUrl}")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public void invalidSessionHandler(HttpServletRequest request, HttpServletResponse response) {

        try
        {
            // 设置跳转的 url
            String redirectUrl = getOriginalUrl(requestCache, request, response, clientProperties.getLoginPage());

            redirectProcessingByLoginProcessType(request, response, clientProperties,
                                                 redirectStrategy, ErrorCodeEnum.INVALID_SESSION,
                                                 redirectUrl);
        }
        catch (Exception e)
        {
            String requestUri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            String msg = String.format("IllegalAccessUrlException: ip=%s, uri=%s, sid=%s, error=%s",
                                       ip, requestUri, request.getSession(true).getId(), e.getMessage());
            log.error(msg, e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR, requestUri, ip);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // 1. 动态注入 invalidSessionHandler() requestMapping 的映射 uri
        String methodName = "invalidSessionHandler";
        MvcUtil.setRequestMappingUri(methodName,
                                     clientProperties.getSession().getInvalidSessionUrl(),
                                     this.getClass(),
                                     HttpServletRequest.class, HttpServletResponse.class);

        // 2. 在 mvc 中做 Uri 映射等动作
        MvcUtil.registerController("invalidSessionController", applicationContext, InvalidSessionController.class);

    }
}
