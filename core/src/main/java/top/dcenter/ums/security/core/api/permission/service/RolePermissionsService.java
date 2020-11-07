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

import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import top.dcenter.ums.security.core.exception.RolePermissionsException;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;

import java.util.List;

/**
 * 角色资源服务接口. 主要用于给角色添加权限的操作.<br>
 * 注意: <br>
 * 1. 另外 {@link #generatePermission(String, PermissionSuffixType)} 用于在添加资源时, 规范权限的格式, 因为支持 resetFul 风格的Api,
 * 在授权时需要对 {@link HttpMethod} 与权限的后缀进行匹配判断<br>
 * 2. 修改或添加角色资源一定要更新 {@link UriAuthorizeService#updateRolesAuthorities()} 缓存, 有两种方式: 一种发布事件, 另一种是直接调用服务;<br>
 * <pre>
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));
 *     // 2. 直接调用服务
 *     abstractUriAuthorizeService.updateRolesAuthorities();
 *     // 3. 实现此接口的方法不需要执行上两种方法的操作, 已通过 AOP 实现发布 UpdateRolesAuthoritiesEvent 事件.
 * </pre>
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/6 23:33
 */
public interface RolePermissionsService<T> {

    /**
     * 更新角色(roleId)所拥有的资源信息.
     * 注意: 此方法既有新增逻辑, 也有更新逻辑, 还有删除逻辑; 逻辑比较复杂, 通用做法是先根据角色 Id 删除角色所拥有的资源, 再根据(resourceIds)新增资源.
     * 建议角色资源关系表的主键为(roleId, resourceId)的组合主键, 不设置自增主键.
     * @param roleId        角色 Id
     * @param resourceIds   资源 Ids
     * @return  是否操作成功
     * @throws RolePermissionsException 更新角色资源信息失败
     */
    boolean updateResourcesOfRole(Long roleId, Long... resourceIds) throws RolePermissionsException;

    /**
     * 获取 角色 ID 所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param roleId  角色 Id
     * @return  返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    List<T> findAllResourcesByRoleId(Long roleId) throws RolePermissionsException;

    /**
     * 根据 角色 获取所有的资源(Resources)信息, 注意实现此方法时注意在逻辑里鉴权, 即只能获取本角色资源的信息列表,
     * 如有角色继承关系{@link RoleHierarchy}, 还可以获取继承角色的角色信息. 例如:
     * <pre>
     * 角色继承: ROLE_A &gt; ROLE_B &gt; ROLE_C.
     * 那么角色: ROLE_B.
     * 可以获取: ROLE_B, ROLE_C 角色信息.
     * </pre>
     * @param role  角色, 例如: ROLE_A
     * @return      返回资源信息列表
     * @throws RolePermissionsException 查询角色资源信息失败
     */
    List<T> findAllResourcesByRole(String role) throws RolePermissionsException;

    /**
     * 根据 uri 与 {@link PermissionSuffixType} 生成 uri 的权限字符串. <br>
     * 注意: 带权限后缀的权限字符串是支持 restFul API 的基础, 为了规范权限格式, 生成权限时必须调用此方法
     * @param uri                       去除 ServletContextPath 的 uri
     * @param permissionSuffixType      PermissionSuffixType
     * @return  uri 的权限字符串
     */
    static String generatePermission(String uri, PermissionSuffixType permissionSuffixType) {
        return String.format("%s%s", uri, permissionSuffixType.getPermissionSuffix());
    }
}
