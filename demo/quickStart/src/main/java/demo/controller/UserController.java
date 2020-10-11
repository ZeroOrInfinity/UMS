package demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.ums.security.core.permission.config.EnableUriAuthorize;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zyw
 * @version V1.0  Created by 2020/9/20 20:04
 */
@Controller
@Slf4j
@EnableUriAuthorize()
public class UserController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("roles", userDetails.getAuthorities());
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

}