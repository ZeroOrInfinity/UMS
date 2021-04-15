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
package top.dcenter.ums.security.core.permission.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.access.PermissionEvaluator;
import top.dcenter.ums.security.core.permission.evaluator.UriAuthoritiesPermissionEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限属性
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.17 16:58
 */
@ConfigurationProperties("ums.rbac")
@Getter
@Setter
public class PermissionProperties {

    /**
     * 用户角色层级配置，默认为 空.<br>
     * 分隔符为:" > ". 例如: ROLE_ADMIN 拥有 ROLE_USER 权限则表示为: {@code ROLE_ADMIN > ROLE_USER > ROLE_EMPLOYEE}<br>
     * 注意:
     * <pre>
     * // ROLE_ADMIN 拥有 ROLE_USER 与 ROLE_EMPLOYEE 权限, ROLE_USER 拥有 ROLE_EMPLOYEE 权限.
     * ROLE_ADMIN > ROLE_USER > ROLE_EMPLOYEE
     * // 等价于
     * ROLE_ADMIN > ROLE_USER
     * ROLE_USER > ROLE_EMPLOYEE
     * </pre>
     */
    private List<String> roleHierarchy = new ArrayList<>();

    /**
     * 403 页面, 默认 空
     */
    private String accessDenyPage;

    /**
     * 权限表达式, 当 {@code enableRestfulApi=false} 或者有 @EnableGlobalMethodSecurity 注释时生效, 默认为 isAuthenticated(). <br>
     * <pre>
     * String accessExp = "isAuthenticated()";
     * // 配置等效与
     * httpSecurity.authorizeRequests().anyRequest().access(isAuthenticated());
     * </pre>
     */
    private String accessExp = "isAuthenticated()";

    /**
     * 权限表达式, 当 {@code enableRestfulApi=true} 且没有 @EnableGlobalMethodSecurity 注释时生效, 默认为 hasPermission(request, authentication).
     * hasPermission 表达式默认实现为 {@link UriAuthoritiesPermissionEvaluator}, 想自定义逻辑, 实现 {@link PermissionEvaluator} 即可替换.<br>
     * <pre>
     * String accessExp = "hasPermission(request, authentication)";
     * // 配置等效与
     * httpSecurity.authorizeRequests().anyRequest().access(hasPermission(request, authentication));
     * </pre>
     */
    private String restfulAccessExp = "hasPermission(request, authentication)";

    /**
     * 是否支持 restful Api (前后端交互接口的风格; 如: 查询(GET),添加(POST),修改(PUT),删除(DELETE)), 默认: true.<br>
     * 当 {@code enableRestfulApi=false} 时 {@code accessExp} 权限表达式生效,
     * 当 {@code enableRestfulApi=true} 时 {@code restfulAccessExp} 权限表达式生效.
     */
    private Boolean enableRestfulApi = true;
}
