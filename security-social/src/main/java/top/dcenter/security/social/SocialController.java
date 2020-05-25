package top.dcenter.security.social;

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
import top.dcenter.security.social.api.callback.RedirectUrlHelper;
import top.dcenter.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;

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
    @GetMapping("/social/user")
    @ConditionalOnProperty(prefix = "security.social", name = "social-user-info", havingValue = "/social/user")
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
    public RedirectView authCallbackRouter(HttpServletRequest request) {

        // todo 优化， social 添加跳转 type 与 path，参考 BandingConnectSupport#buildOAuth2Url()
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if (StringUtils.isNotBlank(state))
        {
            String redirectUrl = redirectUrlHelper.decodeRedirectUrl(state);

            // RFC 6819 安全检查：https://oauth.net/advisories/2014-1-covert-redirect/
            if (redirectUrl.matches("^(([a-zA-z]+://)?[^/]+)+/.*$"))
            {
                log.error("非法的回调地址: {}", redirectUrl);
                throw new ParameterErrorException(String.format("非法的回调地址: %s", redirectUrl));
            }
            if (redirectUrl != null && StringUtils.isNotBlank(redirectUrl))
            {
                return new RedirectView(redirectUrl + "?code=" + code + "&state=" + state, true);
            }
            log.warn("回调地址不正确: {}", redirectUrl);
            throw new ParameterErrorException(String.format("回调地址不正确: %s", redirectUrl));

        }
        log.warn("回调参数 {} 被篡改", state);
        throw new ParameterErrorException(String.format("回调参数 {} 被篡改", state));

    }

}
