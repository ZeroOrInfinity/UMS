package demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.social.vo.SocialUserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zyw
 * @version V1.0  Created by 2020/9/20 20:04
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

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/me")
    @ResponseBody
    public Object getCurrentUser(@AuthenticationPrincipal UserDetails userDetails, Authentication authentication) {

        Map<String, Object> map = new HashMap<>(16);
        map.put("authenticationHolder", SecurityContextHolder.getContext().getAuthentication());
        map.put("userDetails", userDetails);
        map.put("authentication", authentication);
        return map;
    }


    @GetMapping("/banding")
    public String banding() {
        return "banding";
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
