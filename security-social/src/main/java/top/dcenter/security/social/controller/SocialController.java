package top.dcenter.security.social.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.view.RedirectView;
import top.dcenter.security.core.excception.ParameterErrorException;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.api.callback.RedirectUrlHelper;
import top.dcenter.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.RFC_6819_CHECK_REGEX;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_CODE;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_STATE;

/**
 * social 第三方登录控制器
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/12 11:37
 */
@RestController
@Slf4j
public class SocialController {


    private final ProviderSignInUtils providerSignInUtils;
    private final RedirectUrlHelper redirectUrlHelper;
    private final SocialProperties socialProperties;

    public SocialController(ProviderSignInUtils providerSignInUtils, SocialProperties socialProperties, RedirectUrlHelper redirectUrlHelper) {
        this.providerSignInUtils = providerSignInUtils;
        this.socialProperties = socialProperties;
        this.redirectUrlHelper = redirectUrlHelper;
    }

    /**
     * 当前用户的信息
     * @param request
     * @return
     */
    @GetMapping("/social/user/info")
    @ConditionalOnProperty(prefix = "security.social", name = "social-user-info", havingValue = "/social/user/info")
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        if (connection == null)
        {
            return null;
        }

        SocialUserInfo userInfo = new SocialUserInfo();
        userInfo.setProviderId(connection.getKey().getProviderId());
        userInfo.setProviderUserId(connection.getKey().getProviderUserId());
        userInfo.setNickname(connection.getDisplayName());
        userInfo.setHeadImg(connection.getImageUrl());
        return userInfo;
    }

    /**
     * 统一回调地址路由入口
     * @param request
     * @return
     */
    @GetMapping("/auth/callback")
    @ConditionalOnProperty(prefix = "security.social", name = "filter-processes-url", havingValue = "/auth/callback")
    public RedirectView authCallbackRouter(HttpServletRequest request) {

        String code = request.getParameter(URL_PARAMETER_CODE);
        String state = request.getParameter(URL_PARAMETER_STATE);
        if (StringUtils.isNotBlank(state))
        {
            // 解密 state 获取真实的回调地址
            String redirectUrl = redirectUrlHelper.decodeRedirectUrl(state);

            if (StringUtils.isNotBlank(redirectUrl))
            {
                // RFC 6819 安全检查：https://oauth.net/advisories/2014-1-covert-redirect/
                if (redirectUrl.matches(RFC_6819_CHECK_REGEX))
                {
                    log.error("非法的回调地址: {}", redirectUrl);
                    throw new ParameterErrorException(String.format("非法的回调地址: %s", redirectUrl));
                }
                if (redirectUrl != null && StringUtils.isNotBlank(redirectUrl))
                {
                    // 重新组装 url 参数
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
                    return new RedirectView(redirectUrl, true);
                }
                log.warn("回调地址不正确: {}", redirectUrl);
                throw new ParameterErrorException(String.format("回调地址不正确: %s", redirectUrl));
            }

        }
        log.warn("回调参数 {} 被篡改", state);
        throw new ParameterErrorException(String.format("回调参数 {} 被篡改", state));

    }

}
