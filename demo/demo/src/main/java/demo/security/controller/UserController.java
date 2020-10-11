package demo.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/9 13:37
 */
@Controller
@Slf4j
public class UserController {

    private final ProviderSignInUtils providerSignInUtils;

    public UserController(ProviderSignInUtils providerSignInUtils) {
        this.providerSignInUtils = providerSignInUtils;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login2")
    public String login2() {
        return "login2";
    }

    @GetMapping("/banding")
    public String banding() {
        return "banding";
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("roles", userDetails.getAuthorities());
        return "index";
    }

    @GetMapping("/signIn")
    public String signIn() {
        return "signIn";
    }



    @GetMapping("/signUp")
    public String signUp(HttpServletRequest request, Model model) {
        // 获取用户信息逻辑
        // ...
        log.info("Demo =======>: UserController.signUp");
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        if (connection == null)
        {
            return null;
        }
        SocialUserInfo userInfo = new SocialUserInfo();
        userInfo.setProviderId(connection.getKey().getProviderId());
        userInfo.setProviderUserId(connection.getKey().getProviderUserId());
        userInfo.setUserId(connection.getDisplayName());
        userInfo.setAvatarUrl(connection.getImageUrl());
        model.addAttribute("userInfo", userInfo);
        return "signUp";
    }
}
