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

package demo.permission.controller;

import demo.permission.service.UriPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;
import top.dcenter.ums.security.core.permission.config.EnableUriAuthorize;
import top.dcenter.ums.security.core.permission.config.UriAuthorizeInterceptorAutoConfiguration;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;
import top.dcenter.ums.security.core.vo.ResponseResult;

/**
 * 权限测试控制器: <br>
 *
 * &#64;PreAuthorize 注解需要 @EnableGlobalMethodSecurity(prePostEnabled = true) 支持,
 * 在 @EnableUriAuthorize 中 {@link UriAuthorizeInterceptorAutoConfiguration}已配置, 不需要再次配置. <br>
 * &#64;UriAuthorize 注解需要 @EnableUriAuthorize 支持.<br>
 *
 * 注意: <br>
 *     <pre>
 *         &#64;PreAuthorize("hasPermission('/users', '/users:list')")
 *         // equivalent to
 *         &#64;UriAuthorize("/users:list")
 *     </pre>
 *     也就是说: 直接使用
 *     <pre>
 *         &#64;EnableGlobalMethodSecurity(prePostEnabled = true)
 *     </pre>
 *     即可.<br>
 *     2. 修改与添加权限后一定要更新一下角色的权限:
 *     <pre>
 *         // 修改或添加权限一定要更新 updateRolesAuthorities 缓存, 有两种方式：一种发布事件，另一种是直接调用服务；推荐用发布事件(异步执行)。
 *         // 1. 推荐用发布事件(异步执行)
 *         applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));
 *         // 2. 直接调用服务
 *         abstractUriAuthorizeService.updateRolesAuthorities();
 *     </pre>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/9 22:49
 */
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
@RestController
@Slf4j
@EnableUriAuthorize
public class PermissionController {

    @Autowired
    private UriPermissionService uriPermissionService;
    @Autowired
    private UmsUserDetailsService userDetailsService;


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test/roleHierarchy/{id}")
    public ResponseResult roleHierarchy(@PathVariable String id) {
        return ResponseResult.success(id);
    }
    @PreAuthorize("hasRole('VOTE')")
    @GetMapping("/test/VOTE/{id}")
    public ResponseResult vote(@PathVariable String id) {
        return ResponseResult.success(id);
    }

    /**
     * 用户注册, 默认添加角色(admin,ROLE_USER), 权限放行,不需要登录, 默认密码: admin
     * @param mobile    mobile
     * @return ResponseResult
     */
    @GetMapping("/addUser/{mobile}")
    public ResponseResult addUser(@PathVariable String mobile) {

        try
        {
            UserDetails userDetails = userDetailsService.registerUser(mobile);

            // 测试用例, 会返回密码, 生产上禁用
            return ResponseResult.success(null, userDetails);
        }
        catch (Exception e)
        {
            String msg = String.format("用户注册-失败: 手机号：%s, 注册失败: %s", mobile, e.getMessage());
            log.error(msg, e);
            return ResponseResult.fail(ErrorCodeEnum.USER_REGISTER_FAILURE, mobile);
        }
    }

    /**
     * 添加 role 的 uri 的权限, role 不存在自动创建, resources 不存在自动创建
     * @param role          role
     * @param uri           uri
     * @param restfulMethod request method
     * @return  ResponseResult
     */
    @GetMapping("/addPermissionData/{role}")
    public ResponseResult addPermissionData(@PathVariable String role, @NonNull String uri, @NonNull String restfulMethod) {
        PermissionSuffixType permissionType = PermissionSuffixType.getPermissionType(restfulMethod);
        if (permissionType == null)
        {
            return ResponseResult.fail(ErrorCodeEnum.PARAMETER_ERROR, restfulMethod);
        }
        boolean result = uriPermissionService.addUriPermission(role, uri, permissionType);
        if (!result)
        {
            return ResponseResult.fail(ErrorCodeEnum.ADD_PERMISSION_FAILURE);
        }

        return ResponseResult.success();
    }

    /**
     * 删除 role 的 uri 的权限, role 不存在自动创建, resources 不存在自动创建.<br>
     * @param role          role
     * @param uri           uri
     * @param restfulMethod request method
     * @return  ResponseResult
     */
    @GetMapping("/delPermissionData/{role}")
    public ResponseResult delPermissionData(@PathVariable String role, @NonNull String uri,
                                          @NonNull String restfulMethod) {
        PermissionSuffixType permissionType = PermissionSuffixType.getPermissionType(restfulMethod);
        if (permissionType == null)
        {
            return ResponseResult.fail(ErrorCodeEnum.PARAMETER_ERROR, restfulMethod);
        }
        boolean result = uriPermissionService.delUriPermission(role, uri, permissionType);
        if (!result)
        {
            return ResponseResult.fail(ErrorCodeEnum.DEL_PERMISSION_FAILURE);
        }

        return ResponseResult.success();
    }

    /**
     * 测试有 /test/permission:add 权限, 放行. <br>
     */
    @UriAuthorize("/test/permission/**:add")
    @GetMapping("/test/permission/{id}")
    public String testPermission(@PathVariable("id") String id) {
        return "test permission: " + id;
    }


    /**
     * 测试不匹配 /test/deny:add 权限, 禁止访问. <br>
     * <pre>
     *      // 取消 @EnableUriAuthorize, 设置ClientProperties.accessExp = "hasPermission(request, authentication)"
     *      // 等效于下面代码
     *      String accessExp = "hasPermission(request, authentication)";
     *      httpSecurity.authorizeRequests().anyRequest().access(accessExp);
     *      // 直接放行, 因为 ROLE_USER 添加了 uri="/test/deny/**" 的 "/test/deny/**:list" 权限
     * </pre>
     */
    @UriAuthorize("/test/deny/**:add")
    @GetMapping("/test/deny/{id}")
    public String testDeny(@PathVariable("id") String id) {
        return "test deny: " + id;
    }

    /**
     * 此 uri 已经设置 PERMIT_ALL, 不用登录验证.<br>
     * <pre>
     *      // 取消 @EnableUriAuthorize, ClientProperties.accessExp = "hasPermission(request, authentication)"
     *      String accessExp = "hasPermission(request, authentication)";
     *      httpSecurity.authorizeRequests().anyRequest().access(accessExp);
     *      // 直接放行 因为设置了 PERMIT_ALL,
     * </pre>
     *
     * 没有注解 @UriAuthorize 直接放行. <br>
     * 没有注解 @PreAuthorize("hasPermission('/test/pass/**', '/test/pass/**:list')") 直接访问. <br>
     * 有注解时, 禁止访问:<br>
     *     <pre>
     *         &#64;PreAuthorize("hasPermission('/test/pass/**', '/test/pass/**:list')")
     *         // equivalent to
     *         &#64;UriAuthorize("/test/pass/**:list")
     *     </pre>
     */
    @PreAuthorize("hasPermission('/test/pass/**', '/test/pass/**:list')")
    @GetMapping("/test/pass/{id}")
    public String testPass(@PathVariable("id") String id) {
        return "test pass: " + id;
    }

    /**
     * 用户的 AuthorityList("admin, ROLE_USER"),
     * 有注解 @PreAuthorize("HAS_ROLE('admin')") 没有 admin role, 禁止访问. <br>
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test/role/admin/{id}")
    public String testRoleAdmin(@PathVariable("id") String id) {
        return "test role admin: " + id;
    }

    /**
     * 用户的 AuthorityList("admin, ROLE_USER"),
     * 有注解 @PreAuthorize("HAS_ROLE('USER')"), 有 USER role, 直接放行. <br>
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test/role/user/{id}")
    public String testRoleUser(@PathVariable("id") String id) {
        return "test role user: " + id;
    }

    /**
     * 用户的 AuthorityList("admin, ROLE_USER"),
     * 有注解 @PreAuthorize("HAS_AUTHORITY('admin')"), 有 admin authority, 直接放行. <br>
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test/auth/admin/{id}")
    public String testRoleAuthAdmin(@PathVariable("id") String id) {
        return "test role admin: " + id;
    }

}