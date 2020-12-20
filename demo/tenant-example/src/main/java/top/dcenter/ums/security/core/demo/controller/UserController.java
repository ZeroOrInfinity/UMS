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

package top.dcenter.ums.security.core.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.utils.JsonUtil;
import top.dcenter.ums.security.common.vo.ResponseResult;
import top.dcenter.ums.security.core.api.service.UserDetailsRegisterService;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 多组户注册示例
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/20 20:04
 */
@Controller
@Slf4j
public class UserController {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private TenantContextHolder tenantContextHolder;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private UserDetailsRegisterService userDetailsRegisterService;

    /**
     * 注册页面.<br>
     * 注意: 新增的注册页面后需要配置一些属性<br>
     * <pre>
     * // application.yml,
     * # 需要添加放行注册页面权限属性
     * ums:
     *   client:
     *     permit-urls:
     *       - /user/signUp/*:POST
     * # 如果需要验证码, 还需要配置验证码属性
     * ums:
     *   codes:
     *     slider:
     *       auth-urls:
     *         - /user/signUp/*
     * </pre>
     *
     * @param tenantId  租户 ID
     * @param request   request
     * @return  注册成功后跳转登录页
     */
    @PostMapping("/user/signUp/{tenantId}")
    @ResponseBody
    public String signUp(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
        // 生产上可以通过 aop 切面实现此功能
        tenantContextHolder.tenantIdHandle(request, tenantId);

        UserDetails userDetails = userDetailsRegisterService.registerUser(new ServletWebRequest(request));

        return JsonUtil.toJsonString(ResponseResult.success("uri", "login"));
    }


    @GetMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null)
        {
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("roles", userDetails.getAuthorities());
        }
        else
        {
            model.addAttribute("username", "anonymous");
            model.addAttribute("roles", "ROLE_VISIT");
        }
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