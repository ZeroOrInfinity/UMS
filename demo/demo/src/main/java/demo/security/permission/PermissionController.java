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

package demo.security.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * &#64;PreAuthorize 注解需要 @EnableGlobalMethodSecurity(prePostEnabled = true) 支持. <br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/9 22:49
 */
@RestController
@Slf4j
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class PermissionController {
    /**
     * 此 uri 已经设置 PERMIT_ALL, 不用登录验证
     * {@link EnableGlobalMethodSecurity} 生效时, 测试有 add 权限, 放行
     */
    @PreAuthorize("hasPermission('/test/permission/*','add')")
    @GetMapping("/test/permission/{id}")
    public String testPermission(@PathVariable("id") String id) {
        return "test permission: " + id;
    }


    /**
     * 此 uri 已经设置 PERMIT_ALL, 不用登录验证
     * {@link EnableGlobalMethodSecurity} 生效时, 测试不匹配 add 权限, 禁止访问
     */
    @PreAuthorize("hasPermission('/test/deny/*','add')")
    @GetMapping("/test/deny/{id}")
    public String testDeny(@PathVariable("id") String id) {
        return "test deny: " + id;
    }

    /**
     * 此 uri 已经设置 PERMIT_ALL, 不用登录验证
     * {@link EnableGlobalMethodSecurity} 生效时, 没有注解 @PreAuthorize 直接放行
     */
    @GetMapping("/test/pass/{id}")
    public String testPass(@PathVariable("id") String id) {
        return "test pass: " + id;
    }

    /**
     * 需要登录验证, 用户的 AuthorityList("admin, ROLE_USER")
     * {@link EnableGlobalMethodSecurity} 生效时, 有注解 @PreAuthorize("HAS_ROLE('admin')") 没有 admin role, 禁止访问
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test/role/{id}")
    public String testRole(@PathVariable("id") String id) {
        return "test role: " + id;
    }

    /**
     * 需要登录验证, 用户的 AuthorityList("admin, ROLE_USER")
     * {@link EnableGlobalMethodSecurity} 生效时, 有注解 @PreAuthorize("HAS_ROLE('USER')"), 有 USER role, 直接放行
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test/role2/{id}")
    public String testRole2(@PathVariable("id") String id) {
        return "test role2: " + id;
    }

    /**
     * 需要登录验证, 用户的 AuthorityList("admin, ROLE_USER")
     * {@link EnableGlobalMethodSecurity} 生效时, 有注解 @PreAuthorize("HAS_AUTHORITY('admin')"), 有 admin authority, 直接放行
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test/role3/{id}")
    public String testRole3(@PathVariable("id") String id) {
        return "test role3: " + id;
    }

}