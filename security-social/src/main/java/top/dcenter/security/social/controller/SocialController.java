package top.dcenter.security.social.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.vo.SimpleResponse;
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

    public SocialController(ProviderSignInUtils providerSignInUtils) {
        this.providerSignInUtils = providerSignInUtils;
    }

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

    @PostMapping("/social/regist")
    @ConditionalOnProperty(prefix = "security.social", name = "regist-url", havingValue = "/social/regist")
    public SimpleResponse regist(String username, HttpServletRequest request) {
        // 不管是注册用户还是绑定用户，都会拿到一个用户唯一标识，
        if (providerSignInUtils == null)
        {
            return SimpleResponse.fail(500, "服务不存在");
        }
        if (StringUtils.isBlank(username))
        {
            return SimpleResponse.fail(HttpStatus.BAD_REQUEST.value(), "username 不能为空");
        }
        try
        {
            providerSignInUtils.doPostSignUp(username, new ServletWebRequest(request));
        }
        catch (DuplicateConnectionException e)
        {
            log.info("用户注册失败：{}", username);
            return SimpleResponse.fail(HttpStatus.BAD_REQUEST.value(), "username 已被人使用");
        }

        log.info("用户注册成功：{}", username);
        return SimpleResponse.success(username);
    }

}
