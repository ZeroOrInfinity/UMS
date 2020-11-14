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

import demo.entity.SysResources;
import demo.service.SysResourcesService;
import demo.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.api.permission.service.RolePermissionsService;
import top.dcenter.ums.security.core.api.permission.service.UpdateAndCacheAuthoritiesService;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.core.permission.enums.PermissionType;
import top.dcenter.ums.security.core.vo.ResponseResult;

import java.util.Map;

/**
 * 权限测试控制器: 访问权限逻辑优化迭代中, 有点乱, 可能与描述不符的情况, 目前 UMS 还没开发完成, 等完成再来优化示例 <br>
 *
 * &#64;PreAuthorize 注解需要 @EnableGlobalMethodSecurity(prePostEnabled = true) 支持. <br>
 *
 * 注意: <br>
 * 1. 在添加资源时, 通过{@link PermissionType#getPermission()} 来规范的权限格式, 因为要支持 restful 风格的 Api,
 * 在授权时需要对 {@link HttpMethod} 与对应的权限进行匹配判断<br>
 * 2. 如果实现了 {@link UpdateAndCacheAuthoritiesService} 接口, 未实现 {@link RolePermissionsService} 接口, 修改或添加基于"角色/多租户/SCOPE
 * "的资源权限时一定要调用 {@link UpdateAndCacheAuthoritiesService} 对应的方法, 有两种方式: 一种发布事件, 另一种是直接调用对应服务;<br>
 * <pre>
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true, ResourcesType.ROLE));
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true, ResourcesType.TENANT));
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true, ResourcesType.SCOPE));
 *     // 2. 直接调用服务
 *     // 基于角色
 *     UpdateAndCacheAuthoritiesService.updateAuthoritiesOfAllRoles();
 *     // 基于多租户
 *     UpdateAndCacheAuthoritiesService.updateAuthoritiesOfAllTenant();
 *     // 基于 SCOPE
 *     UpdateAndCacheAuthoritiesService.updateAuthoritiesOfAllScopes();
 * </pre>
 * 3. 实现此 {@link RolePermissionsService} 接口, 不需要执行上两种方法的操作, 已通过 AOP 方式实现发布 UpdateRolesAuthoritiesEvent 事件.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/9 22:49
 */
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
@RestController
@Slf4j
//@EnableGlobalMethodSecurity(prePostEnabled = true, order = Ordered.HIGHEST_PRECEDENCE)
public class PermissionController {

    @Autowired
    private RolePermissionsService<SysResources> rolePermissionService;
    @Autowired
    private UmsUserDetailsService userDetailsService;
    @Autowired
    private SysResourcesService sysResourcesService;
    @Autowired
    private SysRoleService sysRoleService;


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
     * <pre>
     * // 添加用户, 默认密码: admin
     * http://localhost:9090/demo/addUser/ROLE_USER/13322221111
     * http://localhost:9090/demo/addUser/ROLE_ADMIN/13322221112
     * http://localhost:9090/demo/addUser/ROLE_VOTE/13322221113
     *
     * // 登录 13322221111 用户
     * </pre>
     * @param mobile    mobile
     * @return ResponseResult
     */
    @GetMapping("/addUser/{role}/{mobile}")
    public ResponseResult addUser(@PathVariable("mobile") String mobile, @PathVariable("role") String role) {

        try
        {
            // 为了测试方便直接用 role_mobile 传递参数
            UserDetails userDetails = userDetailsService.registerUser(role + "," + mobile);

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
     * 为了测试方便,直接用 GET 方法接收添加权限资源
     * <pre>
     *     HttpMethod       permission
     *     get              list
     *     post             add
     *     put              edit
     *     delete           delete
     *
     * // 测试 @EnableGlobalMethodSecurity(prePostEnabled = true) 访问权限控制需要的资源
     * http://localhost:9090/demo/addResource?uri=/test/permission/*&restfulMethod=post
     *
     * // 测试 restful 风格的 API restfulAccessExp 表达式访问权限控制需要的资源
     * http://localhost:9090/demo/addResource?uri=/test/restful/*&restfulMethod=get
     * http://localhost:9090/demo/addResource?uri=/test/restful/*&restfulMethod=post
     * http://localhost:9090/demo/addResource?uri=/test/restful/*&restfulMethod=put
     * http://localhost:9090/demo/addResource?uri=/test/restful/*&restfulMethod=delete
     * </pre>
     * @param uri               资源
     * @param restfulMethod     HttpMethod 方法名称
     * @return  ResponseResult
     */
    @GetMapping("/addResource")
    public ResponseResult addResources(String uri, String restfulMethod) {
        try {
            // 新增权限资源
            final HttpMethod method = HttpMethod.resolve(restfulMethod.toUpperCase());

            if (method == null) {
                return ResponseResult.fail(ErrorCodeEnum.ADD_RESOURCE_METHOD_FORMAT_ERROR, restfulMethod);
            }

            String newPermission = PermissionType.getPermission(method);
            SysResources sysResources = new SysResources();
            sysResources.setPermission(newPermission);
            sysResources.setUrl(uri);
            sysResources.setAvailable(true);
            // ...

            // 存入数据库
            sysResources = sysResourcesService.save(sysResources);

            return ResponseResult.success("添加资源成功: 资源 id=" + sysResources.getId());

        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.fail(ErrorCodeEnum.ADD_RESOURCE_FAILURE, restfulMethod);
        }
    }

    /**
     * 添加 roleId 的权限资源.<br>
     * <pre>
     * // 测试 @EnableGlobalMethodSecurity(prePostEnabled = true) 访问权限控制需要执行的 url
     * // 假设: ROLE_USER 的 角色 ID=1, 权限资源 ID: 1,2,3,4,5 . 注意测试的时候根据实际 id 值替换
     * http://localhost:9090/demo/addPermissionData/1?resourceIds=1,2,3,4,5
     *
     * </pre>
     *
     * @param roleId            角色 ID
     * @param resourceIds       权限资源的 ids
     * @return  ResponseResult
     */
    @GetMapping("/addPermissionData/{roleId}")
    public ResponseResult updateResourcesOfRole(@PathVariable("roleId") Long roleId, Long... resourceIds) {
        try {
            final boolean isUpdated = rolePermissionService.updateResourcesOfRole(roleId, resourceIds);
            if (!isUpdated)
            {
                return ResponseResult.fail(ErrorCodeEnum.ADD_ROLE_PERMISSION_FAILURE);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.fail(ErrorCodeEnum.ADD_ROLE_PERMISSION_FAILURE);
        }

        return ResponseResult.success();
    }



    /**
     * 测试 restful 风格的 API 表达式访问权限控制 , 禁止 <br>
     * 开启 @EnableGlobalMethodSecurity(prePostEnabled = true) 注释, restfulAccessExp 表达式访问权限控制失效,
     */
    @GetMapping("/test/restful/deny/{id}")
    public String testGetRestfulDeny(@PathVariable("id") String id) {
        return "test restful Api: " + id;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     * 测试 restful 风格的 API 表达式访问权限控制 , 放行 <br>
     * 开启 @EnableGlobalMethodSecurity(prePostEnabled = true) 注释, restfulAccessExp 表达式访问权限控制失效,
     */
    @GetMapping("/test/restful/{id}")
    public String testGetRestful(@PathVariable("id") String id) {
        return "test restful Api: " + id;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     * 测试 restful 风格的 API 表达式访问权限控制 , 放行 <br>
     *
     * 开启 @EnableGlobalMethodSecurity(prePostEnabled = true) 注释, restfulAccessExp 表达式访问权限控制失效,
     */
    @PostMapping("/test/restful/{id}")
    public String testPostRestful(@PathVariable("id") String id,
                                  @RequestParam Map<String, String> something) {
        return "test restful Api: " + id + " POST: " + something;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     * 测试 restful 风格的 API 表达式访问权限控制 , 放行 <br>
     *
     * 开启 @EnableGlobalMethodSecurity(prePostEnabled = true) 注释, restfulAccessExp 表达式访问权限控制失效,
     */
    @PutMapping("/test/restful/{id}")
    public String testPutRestful(@PathVariable("id") String id, String something) {
        return "test restful Api: " + id+ " PUT: " + something;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     * 测试 restful 风格的 API 表达式访问权限控制 , 放行 <br>
     *
     * 开启 @EnableGlobalMethodSecurity(prePostEnabled = true) 注释, restfulAccessExp 表达式访问权限控制失效,
     */
    @DeleteMapping("/test/restful/{id}")
    public String testPostRestful(@PathVariable("id") String id) {
        return "test restful Api: DELETE id = " + id;
    }


    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     * 需要打开 @EnableGlobalMethodSecurity(prePostEnabled = true)<br>
     * 测试有 add({@link PermissionType#getPermissions()} 可查看所有的权限) 权限, 放行. <br>
     */
    @PreAuthorize("hasPermission('/test/permission/*', 'add')")
    @GetMapping("/test/permission/{id}")
    public String testPermission(@PathVariable("id") String id) {
        return "test permission: " + id;
    }


    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     * 需要打开 @EnableGlobalMethodSecurity(prePostEnabled = true)<br>
     * 有 @PreAuthorize 注解, 测试不匹配 add 权限, 禁止访问. <br>
     */
    @PreAuthorize("hasPermission('/test/deny/*', 'add')")
    @GetMapping("/test/deny/{id}")
    public String testDeny(@PathVariable("id") String id) {
        return "test deny: " + id;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     *
     * 此 uri 已经设置 PERMIT_ALL, 不用登录验证.<br>
     *
     */
    @GetMapping("/test/pass/{id}")
    public String testPass(@PathVariable("id") String id) {
        return "test pass: " + id;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     *     需要打开 @EnableGlobalMethodSecurity(prePostEnabled = true)<br>
     *
     * 有注解 @PreAuthorize("HAS_ROLE('admin')") 没有 ROLE_admin , 禁止访问. <br>
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test/role/admin/{id}")
    public String testRoleAdmin(@PathVariable("id") String id) {
        return "test role admin: " + id;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     *     需要打开 @EnableGlobalMethodSecurity(prePostEnabled = true)<br>
     *
     * 有注解 @PreAuthorize("HAS_ROLE('USER')"), 有 ROLE_USER, 直接放行. <br>
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test/role/user/{id}")
    public String testRoleUser(@PathVariable("id") String id) {
        return "test role user: " + id;
    }

    /**
     * 先执行 {@link #addUser(String, String)} {@link #addResources(String, String)} 与 {@link #updateResourcesOfRole(Long, Long...)} 注释上的连接. 并登录<br>
     *     需要打开 @EnableGlobalMethodSecurity(prePostEnabled = true)<br>
     *
     * 有注解 @PreAuthorize("HAS_AUTHORITY('admin')"), 有 admin authority, 直接放行. <br>
     */
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/test/auth/admin/{id}")
    public String testRoleAuthAdmin(@PathVariable("id") String id) {
        return "test role admin: " + id;
    }

}