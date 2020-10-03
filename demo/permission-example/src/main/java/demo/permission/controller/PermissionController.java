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
import top.dcenter.ums.security.core.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.permission.annotation.UriAuthorize;
import top.dcenter.ums.security.core.permission.config.EnableUriAuthorize;
import top.dcenter.ums.security.core.permission.config.UriAuthorizeInterceptorAutoConfiguration;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;
import top.dcenter.ums.security.core.vo.ResponseResult;

import java.util.List;

/**
 * 权限测试控制器:
 *
 * &#64;EnableUriAuthorize(filterOrInterceptor = true) 为过滤器模式; 添加角色权限即可实现权限控制, <br>
 * &#64;EnableUriAuthorize(filterOrInterceptor = false) 默认为拦截器模式(注解模式);<br>
 *
 * &#64;PreAuthorize 注解需要 @EnableGlobalMethodSecurity(prePostEnabled = true) 支持,
 * 在 @EnableUriAuthorize 中 {@link UriAuthorizeInterceptorAutoConfiguration}已配置, 不需要再次配置. <br>
 * &#64;UriAuthorize 注解需要 @EnableUriAuthorize(filterOrInterceptor = false) 支持.<br>
 *
 * 注意点: <br>
 * 1. 过滤器模式需要验证的 url 必须有一条角色(任何角色都可以)权限记录. <br>
 * 2. 修改与添加权限后更新一下角色的权限:<br>
 * <pre>
 *     // 修改或添加权限一定要更新 updateRolesAuthorities 缓存, 有两种方式：一种发布事件，另一种是直接调用服务；推荐用发布事件(异步执行)。
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));
 *     // 2. 直接调用服务
 *     abstractUriAuthorizeService.updateRolesAuthorities();
 * </pre>
 * @author zyw
 * @version V1.0  Created by 2020/9/9 22:49
 */
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "DefaultAnnotationParam"})
@RestController
@Slf4j
@EnableUriAuthorize(filterOrInterceptor = false)
public class PermissionController {

    @Autowired
    private UriPermissionService uriPermissionService;
    @Autowired
    private UmsUserDetailsService userDetailsService;


    /**
     * 用户注册, 默认添加角色(admin,ROLE_USER), 权限放行,不需要登录, 默认密码: admin
     * @param mobile    mobile
     * @return  ResponseResult
     */
    @GetMapping("/addUser/{mobile}")
    public ResponseResult addUser(@PathVariable String mobile) {
        try {
            UserDetails userDetails = userDetailsService.registerUser(mobile);

            // 测试用例, 会返回密码, 生产上禁用
            return ResponseResult.success(userDetails);
        }
        catch (Exception e) {
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
        boolean result = uriPermissionService.addUriPermission(role, uri, List.of(permissionType));
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
        boolean result = uriPermissionService.delUriPermission(role, uri, List.of(permissionType));
        if (!result)
        {
            return ResponseResult.fail(ErrorCodeEnum.DEL_PERMISSION_FAILURE);
        }

        return ResponseResult.success();
    }

    /**
     * 测试有 /test/permission:add 权限, 放行. <br>
     * 过滤器模式时, 注解是失效的, 但不影响权限过滤器的权限控制, 有权限 /test/permission/:add, 过滤器模式 可以访问.
     */
    @UriAuthorize("/test/permission/**:add")
    @GetMapping("/test/permission/{id}")
    public String testPermission(@PathVariable("id") String id) {
        return "test permission: " + id;
    }


    /**
     * 测试不匹配 /test/deny:add 权限, 禁止访问. <br>
     * 过滤器模式时, 注解是失效的, 但不影响权限过滤器的权限控制, 过滤器模式, 禁止访问
     */
    @UriAuthorize("/test/deny/**:add")
    @GetMapping("/test/deny/{id}")
    public String testDeny(@PathVariable("id") String id) {
        return "test deny: " + id;
    }

    /**
     * 此 uri 已经设置 PERMIT_ALL, 不用登录验证,
     * 没有注解 @UriAuthorize 直接放行. <br>
     * 过滤器模式时, 直接放行
     */
    @GetMapping("/test/pass/{id}")
    public String testPass(@PathVariable("id") String id) {
        return "test pass: " + id;
    }

    /**
     * 用户的 AuthorityList("admin, ROLE_USER"),
     * 有注解 @PreAuthorize("HAS_ROLE('admin')") 没有 admin role, 禁止访问. <br>
     * 过滤器模式时, 注解是失效的, 但不影响权限过滤器的权限控制, 有权限 /test/role/admin/:list, 过滤器模式 可以访问.
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test/role/admin/{id}")
    public String testRoleAdmin(@PathVariable("id") String id) {
        return "test role admin: " + id;
    }

    /**
     * 用户的 AuthorityList("admin, ROLE_USER"),
     * 有注解 @PreAuthorize("HAS_ROLE('USER')"), 有 USER role, 直接放行. <br>
     * 过滤器模式时, 注解是失效的, 但不影响权限过滤器的权限控制, 有权限 /test/role/user/:list, 过滤器模式 可以访问.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test/role/user/{id}")
    public String testRoleUser(@PathVariable("id") String id) {
        return "test role user: " + id;
    }

    /**
     * 用户的 AuthorityList("admin, ROLE_USER"),
     * 有注解 @PreAuthorize("HAS_AUTHORITY('admin')"), 有 admin authority, 直接放行. <br>
     * 过滤器模式时, 注解是失效的, 但不影响权限过滤器的权限控制, 没有设置权限, 过滤器模式 可以禁止访问.
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test/auth/admin/{id}")
    public String testRoleAuthAdmin(@PathVariable("id") String id) {
        return "test role admin: " + id;
    }

}
