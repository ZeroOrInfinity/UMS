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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.dcenter.ums.security.common.consts.SecurityConstants;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.auth.properties.ClientProperties;
import top.dcenter.ums.security.common.utils.IpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.redirectProcessingLogoutByLoginProcessType;

/**
 * 登出成功处理器, 如要替换此类, 继承后注入 IOC 容器即可
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/4 23:20
 */
@Slf4j
public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {

    protected final RedirectStrategy redirectStrategy;
    protected final ClientProperties clientProperties;
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    protected UserCache userCache;

    public DefaultLogoutSuccessHandler(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        HttpSession session = request.getSession(true);

        log.info("登出成功: user={}, ip={}, ua={}, sid={}, sck={}",
                 authentication != null ? authentication.getPrincipal() : "",
                 IpUtil.getRealIp(request),
                 request.getHeader(SecurityConstants.HEADER_USER_AGENT),
                 session.getId(),
                 session.getAttribute(SecurityConstants.SESSION_ENHANCE_CHECK_KEY));

        // 清理缓存
        session.removeAttribute(SecurityConstants.SESSION_ENHANCE_CHECK_KEY);
        if (userCache != null && authentication != null)
        {
            userCache.removeUserFromCache(authentication.getName());
        }

        redirectProcessingLogoutByLoginProcessType(request, response, clientProperties,
                                                   redirectStrategy, ErrorCodeEnum.LOGOUT_SUCCESS);
    }
}