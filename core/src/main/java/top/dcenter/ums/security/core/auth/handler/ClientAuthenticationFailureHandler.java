/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.auth.filter.JsonRequestFilter;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.ums.security.core.util.IpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.authenticationFailureProcessing;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.getAbstractResponseJsonAuthenticationException;

/**
 * 客户端认证失败处理器, 默认简单实现，需自己去实现.<br><br>
 * 继承 {@link BaseAuthenticationFailureHandler } 后，再向 IOC 容器注册自己来实现自定义功能。
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Slf4j
public class ClientAuthenticationFailureHandler extends BaseAuthenticationFailureHandler {
    protected final ClientProperties clientProperties;

    public ClientAuthenticationFailureHandler(ClientProperties clientProperties) {
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
        if (request instanceof JsonRequestFilter.JsonRequest)
        {
            JsonRequestFilter.JsonRequest jsonRequest = ((JsonRequestFilter.JsonRequest) request);
            Map<String, Object> formMap = jsonRequest.getFormMap();
            if (formMap != null)
            {
                formMap.computeIfPresent(clientProperties.passwordParameter, (k, v) -> v = "PROTECTED");
                reqData = formMap.toString();
            }
            else
            {
                reqData = request.getQueryString();
            }
        }
        else
        {
            Map<String, String[]> parameterMap = request.getParameterMap();
            parameterMap.computeIfPresent(clientProperties.passwordParameter, (k, v) -> v = new String[]{"PROTECTED"});
            reqData = parameterMap.toString();
        }

        log.info("登录失败: user={}, ip={}, ua={}, sid={}, reqData={}, errorMsg={}",
                 e == null ? null : e.getUid(),
                 IpUtil.getRealIp(request),
                 request.getHeader(SecurityConstants.HEADER_USER_AGENT),
                 request.getSession(true).getId(),
                 reqData,
                 exception.getMessage());


        // 检测是否接收 json 格式
        String acceptHeader = request.getHeader(SecurityConstants.HEADER_ACCEPT);

        // 返回 json 格式
        if (authenticationFailureProcessing(response, exception, e, acceptHeader, clientProperties))
        {
            // 进行必要的清理
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }

}