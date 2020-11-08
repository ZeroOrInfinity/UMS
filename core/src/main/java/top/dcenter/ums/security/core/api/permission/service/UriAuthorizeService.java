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

package top.dcenter.ums.security.core.api.permission.service;

import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import top.dcenter.ums.security.core.api.permission.entity.UriResourcesDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * request 的 uri 访问权限控制服务.<br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/8/28 16:09
 */
public interface UriAuthorizeService {

    /**
     * 通过 {@link AntPathMatcher#match(String, String)} 检查 requestUri 是否与 pattern 匹配.
     * @param pattern       pattern
     * @param requestUri    requestUri
     * @return  是否匹配
     */
    boolean match(String pattern, String requestUri);

    /**
     * 根据 authentication 来判断是否有 uriAuthority 访问权限, 用于 {@code @PerAuthorize("hasPermission('/users', 'list')")} 判断
     * @param authentication    authentication
     * @param requestUri        不包含 ServletContextPath 的 requestUri
     * @param uriAuthority      uri 权限
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    boolean hasPermission(Authentication authentication, String requestUri, String uriAuthority);

    /**
     * 根据 authentication 来判断是否有 uriAuthority 访问权限, <br>
     * 用于 {@code httpSecurity.authorizeRequests().anyRequest().access("hasPermission(request, authentication)")} 判断
     * @param authentication    authentication
     * @param request           HttpServletRequest
     * @return  有访问权限则返回 true, 否则返回 false.
     */
    boolean hasPermission(Authentication authentication, HttpServletRequest request);

    /**
     * 获取用户角色的 uri 权限 Map
     * @param authentication    authentication
     * @return 用户角色的 uri 权限 Map(uri, Set(authority))
     */
    Optional<Map<String, Set<String>>> getUriAuthoritiesOfUserRole(Authentication authentication);

    /**
     * 获取所有角色的 uri 权限 Map
     * @return 所有角色的 uri 权限 Map(uri, Set(authority))
     */
    Optional<Map<String, Set<String>>> getUriAuthoritiesOfAllRole();


    /**
     * 当没有访问权限时的处理方式
     * @param status    返回状态
     * @param response  response
     */
    void handlerError(int status, HttpServletResponse response);

    /**
     * 获取所有角色的 uri 的权限 map.<br>
     *     返回值为: Map(role, Map(uri, UriResourcesDTO))
     * @return Map(String, Map(String, String)) 的 key 为必须包含"ROLE_"前缀的角色名称(如: ROLE_ADMIN), value 为 map
     * (key 为 uri, 此 uri 可以为 antPath 通配符路径,如 /user/**; value 为 UriResourcesDTO).
     */
    Optional<Map<String, Map<String, UriResourcesDTO>>> getRolesAuthorities();

    /**
     * 每次修改角色的权限时, 都要更新角色的 uri 的权限 map
     */
    void updateRolesAuthorities();

    /**
     * 获取 AntPathMatcher
     * @return AntPathMatcher
     */
    AntPathMatcher getAntPathMatcher();

}