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

package top.dcenter.ums.security.core.auth.session.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.common.config.SecurityCoreAutoConfigurer;
import top.dcenter.ums.security.core.api.authentication.handler.BaseAuthenticationFailureHandler;
import top.dcenter.ums.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.ums.security.core.auth.session.strategy.EnhanceConcurrentControlAuthenticationStrategy;
import top.dcenter.ums.security.core.exception.SessionEnhanceCheckException;
import top.dcenter.ums.security.common.utils.IpUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static top.dcenter.ums.security.common.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.SESSION_ENHANCE_CHECK;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.isPermitUri;

/**
 * session 增强检测, 如对客户端特征码检测, 增强对 session 攻击的防御. <br>
 * 如果要修改检测逻辑, 实现 {@link SessionEnhanceCheckService}, 注入 IOC 容器即可
 * 依赖 {@link EnhanceConcurrentControlAuthenticationStrategy}. <br><br>
 *     属性 authorizeRequestMap 通过 {@link SecurityCoreAutoConfigurer} 方法
 *     groupingAuthorizeRequestUris() 注入
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/2 10:14
 */
@Slf4j
public class SessionEnhanceCheckFilter extends OncePerRequestFilter {

    private final BaseAuthenticationFailureHandler baseAuthenticationFailureHandler;
    private final SessionEnhanceCheckService sessionEnhanceCheckService;
    private final AntPathMatcher pathMatcher;

    public SessionEnhanceCheckFilter(BaseAuthenticationFailureHandler baseAuthenticationFailureHandler,
                                     SessionEnhanceCheckService sessionEnhanceCheckService) {

        this.baseAuthenticationFailureHandler = baseAuthenticationFailureHandler;
        this.sessionEnhanceCheckService = sessionEnhanceCheckService;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (this.sessionEnhanceCheckService != null && session != null && !isPermitUri(request, session, pathMatcher))
        {
            // 用户 client 的特征值
            String checkValue = (String) session.getAttribute(SESSION_ENHANCE_CHECK_KEY);
            // 如果不符合特征值, 则为非法 session, 直接返回登录页面
            if (checkValue != null && !this.sessionEnhanceCheckService.sessionEnhanceCheck(checkValue, request))
            {
                log.warn("session被劫持: ip={}, ua={}, sid={}, uri={}, checkValue={}",
                         IpUtil.getRealIp(request),
                         request.getHeader(HEADER_USER_AGENT),
                         session.getId(),
                         request.getRequestURI(),
                         checkValue);
                this.baseAuthenticationFailureHandler.onAuthenticationFailure(request, response,
                                                                              new SessionEnhanceCheckException(SESSION_ENHANCE_CHECK, session.getId(), checkValue));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}