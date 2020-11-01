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

package demo.security.session.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.ums.security.core.util.IpUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static top.dcenter.ums.security.common.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.ums.security.common.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;
import static top.dcenter.ums.security.core.util.AuthenticationUtil.extractUserAgent;

/**
 * DemoSessionEnhanceCheckService
 * @see SessionEnhanceCheckService
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/2 14:02
 */
@Component
@Slf4j
public class DemoSessionEnhanceCheckServiceImpl implements SessionEnhanceCheckService {

    @Override
    public void setEnhanceCheckValue(@NonNull HttpSession session, @NonNull HttpServletRequest request) {
        String userAgent = request.getHeader(HEADER_USER_AGENT);
         session.setAttribute(SESSION_ENHANCE_CHECK_KEY, IpUtil.getRealIp(request) + extractUserAgent(userAgent));
    }

    @Override
    public boolean sessionEnhanceCheck(@NonNull String checkValue, @NonNull HttpServletRequest request) {

        String userAgent = request.getHeader(HEADER_USER_AGENT);
        String remoteAddr = IpUtil.getRealIp(request);
        // 验证是否合法的 client 特征码
        if (checkValue.equals(remoteAddr + extractUserAgent(userAgent)))
        {
            return true;
        }
        log.debug("demo =====> {}: sessionId={}, ip={}, User-Agent={}", ErrorCodeEnum.SESSION_ENHANCE_CHECK.getMsg(),
                  request.getSession().getId(), remoteAddr, userAgent);

        return false;
    }
}