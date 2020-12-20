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

package demo.security.authentication.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.core.exception.AbstractResponseJsonAuthenticationException;
import top.dcenter.ums.security.common.utils.IpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.ums.security.common.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.authenticationFailureProcessing;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.getAbstractResponseJsonAuthenticationException;

/**
 * 客户端认证失败处理器.<br><br>
 *     当发生异常 {@link AbstractResponseJsonAuthenticationException} 时返回 JSON 数据
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/4 13:46
 */
@Component
@Slf4j
public class DemoAuthenticationFailureHandler extends BaseAuthenticationFailureHandler {

    protected final ClientProperties clientProperties;

    public DemoAuthenticationFailureHandler(ClientProperties clientProperties) {
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

        log.info("demo ========> 登录失败: {}, user={}, ip={}, ua={}, sid={}",
                 exception.getMessage(),
                 e == null ? null : e.getUid(),
                 IpUtil.getRealIp(request),
                 request.getHeader(HEADER_USER_AGENT),
                 request.getSession(true).getId());

        // 进行必要的缓存清理

        // 检测是否接收 json 格式
        if (authenticationFailureProcessing(response, request, exception, e,
                                            this.clientProperties.getLoginProcessType()))
        {
            // 进行必要的清理缓存
            return;
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}