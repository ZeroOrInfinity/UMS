package demo.security.session.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.security.core.enums.ErrorCodeEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;
import static top.dcenter.security.core.consts.SecurityConstants.HEADER_USER_AGENT;
import static top.dcenter.security.core.util.AuthenticationUtil.extractUserAgent;

/**
 * DemoSessionEnhanceCheckService
 * @see SessionEnhanceCheckService
 * @author zyw
 * @version V1.0  Created by 2020/6/2 14:02
 */
@Component
@Slf4j
public class DemoSessionEnhanceCheckService implements SessionEnhanceCheckService {

    @Override
    public void setEnhanceCheckValue(@NonNull HttpSession session, @NonNull HttpServletRequest request) {
        String userAgent = request.getHeader(HEADER_USER_AGENT);
         session.setAttribute(SESSION_ENHANCE_CHECK_KEY, request.getRemoteAddr() + extractUserAgent(userAgent));
    }

    @Override
    public boolean sessionEnhanceCheck(@NonNull String checkValue, @NonNull HttpServletRequest request) {

        String userAgent = request.getHeader(HEADER_USER_AGENT);
        String remoteAddr = request.getRemoteAddr();
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
