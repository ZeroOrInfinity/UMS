package top.dcenter.ums.security.social.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import top.dcenter.ums.security.core.exception.ParameterErrorException;
import top.dcenter.ums.security.social.callback.RedirectUrlHelperServiceImpl;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.ums.security.core.consts.RegexConstants.RFC_6819_CHECK_REGEX;
import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_CODE;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_STATE;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.REDIRECT_URL_PARAMETER_ERROR;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.REDIRECT_URL_PARAMETER_ILLEGAL;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.TAMPER_WITH_REDIRECT_URL_PARAMETER;

/**
 * social 第三方登录控制器
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/12 11:37
 */
@Slf4j
@ResponseBody
public class SocialController {


    private final RedirectUrlHelperServiceImpl redirectUrlHelper;

    public SocialController(RedirectUrlHelperServiceImpl redirectUrlHelper) {
        this.redirectUrlHelper = redirectUrlHelper;
    }


    /**
     * 统一回调地址路由入口
     * @param request   {@link HttpServletRequest}
     * @return {@link RedirectView}
     */
    @GetMapping("/auth/callback")
    @ConditionalOnProperty(prefix = "security.social", name = "filter-processes-url", havingValue = "/auth/callback")
    public RedirectView authCallbackRouter(HttpServletRequest request) {

        String state = request.getParameter(URL_PARAMETER_STATE);
        String queryString = request.getQueryString();
        String ip = request.getRemoteAddr();
        String sid = request.getSession(true).getId();
        String uri = request.getRequestURI();

        if (StringUtils.isNotBlank(state))
        {
            // 解密 state 获取真实的回调地址
            String redirectUrl = redirectUrlHelper.decodeRedirectUrl(state);

            if (StringUtils.isNotBlank(redirectUrl))
            {
                // RFC 6819 安全检查：https://oauth.net/advisories/2014-1-covert-redirect/
                if (redirectUrl.matches(RFC_6819_CHECK_REGEX))
                {
                    log.error("统一回调地址路由-state被篡改: ip={}, sid={}, uri={}, state={}, queryString={}, redirectUrl={}",
                              ip, sid, uri, state, queryString, redirectUrl);
                    throw new ParameterErrorException(REDIRECT_URL_PARAMETER_ILLEGAL, redirectUrl,
                                                      sid);
                }
                if (StringUtils.isNotBlank(redirectUrl))
                {
                    String code = request.getParameter(URL_PARAMETER_CODE);
                    // 重新组装 url 参数, 不带 ServletContextPath
                    redirectUrl = String.format("%s%s%s%s%s%s%s%s%s",
                                                redirectUrl,
                                                URL_PARAMETER_IDENTIFIER,
                                                URL_PARAMETER_CODE,
                                                KEY_VALUE_SEPARATOR,
                                                code,
                                                URL_PARAMETER_SEPARATOR,
                                                URL_PARAMETER_STATE,
                                                KEY_VALUE_SEPARATOR,
                                                state);

                    log.info("统一回调地址路由: ip={}, sid={}, uri={}, state={}, queryString={}, redirectUrl={}",
                             ip, sid, uri, state, queryString, redirectUrl);
                    // 会自动添加 ServletContextPath
                    return new RedirectView(redirectUrl, true);
                }
                log.error("统一回调地址路由-state被篡改: ip={}, sid={}, uri={}, state={}, queryString={}, redirectUrl={}",
                          ip, sid, uri, state, queryString, redirectUrl);
                throw new ParameterErrorException(REDIRECT_URL_PARAMETER_ERROR, redirectUrl, sid);
            }

        }
        log.warn("统一回调地址路由-state为空: ip={}, sid={}, uri={}, state={}, queryString={}",
                  ip, sid, uri, state, queryString);
        throw new ParameterErrorException(TAMPER_WITH_REDIRECT_URL_PARAMETER, state, sid);

    }

}
