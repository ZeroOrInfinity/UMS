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
import top.dcenter.security.core.util.CastUtil;
import top.dcenter.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

import static top.dcenter.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

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
    private final SocialProperties socialProperties;

    public SocialController(ProviderSignInUtils providerSignInUtils, SocialProperties socialProperties) {
        this.providerSignInUtils = providerSignInUtils;
        this.socialProperties = socialProperties;
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
        String state = request.getParameter("state");
        String code = request.getParameter("code");

        byte[] router = Base64.getDecoder().decode(state.substring(state.indexOf("-") + 1));
        Map<String, String> routerMap = CastUtil.string2Map(new String(router), URL_PARAMETER_SEPARATOR,
                                                       KEY_VALUE_SEPARATOR);
        String path = routerMap.get("path");
        // RFC 6819 安全检查：https://oauth.net/advisories/2014-1-covert-redirect/
        if (path.matches("^(([a-zA-z]+://)?[^/]+)+/.*$"))
        {
            log.error("非法的回调地址: {}", path);
            throw new ParameterErrorException("非法的回调地址");
        }
        if (path != null && StringUtils.isNotBlank(path))
        {
            return new RedirectView(path + "?code=" + code + "&state=" + state, true);
        }
        log.warn("回调参数不正确: {}", path);
        throw new ParameterErrorException("回调参数不正确");

    }

}
