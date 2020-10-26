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
package demo.controller;

import me.zhyd.oauth.model.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.ums.security.core.oauth.userdetails.TemporaryUser;
import top.dcenter.ums.security.core.util.MvcUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 展示用户第一次第三方授权登录时, 不支持自动注册, 获取临时用户信息(含第三方的用户信息)的方式, 这里展示两种方式:
 * 1. 注解方式 @AuthenticationPrincipal UserDetails userDetails .<br>
 * 2. 通过 SecurityContextHolder 获取
 * <pre>
 *     final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
 *     final Object principal = authentication.getPrincipal();
 *     if (principal instanceof UserDetails)
 *     {
 *         UserDetails details = ((UserDetails) principal);
 *     }
 * </pre>
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/26 13:08
 */
@Controller
public class SignUpController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/user/me")
    @ResponseBody
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("userDetails", userDetails);
        map.put("securityContextHolder", SecurityContextHolder.getContext().getAuthentication());

        log.info(MvcUtil.toJsonString(userDetails));

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Object principal = authentication.getPrincipal();
        if (principal instanceof TemporaryUser)
        {
            TemporaryUser temporaryUser = ((TemporaryUser) principal);
            log.info(MvcUtil.toJsonString(temporaryUser));
            final AuthUser authUser = temporaryUser.getAuthUser();
            log.info(MvcUtil.toJsonString(authUser));
        }

        // principal 等价于 userDetails
        log.info("userDetails.equals(principal) = {}", userDetails.equals(principal));

        return map;
    }
}
