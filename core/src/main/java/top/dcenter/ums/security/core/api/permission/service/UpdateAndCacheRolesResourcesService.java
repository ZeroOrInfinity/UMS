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

/**
 * 用于更新或缓存基于(角色/多租户/SCOPE)的角色权限服务接口, 每次更新角色的 uri(资源)权限时,需要调用此接口,
 * 推荐实现此 {@link RolePermissionsService} 接口, 会自动通过 AOP 方式实现发布 UpdateRolesResourcesEvent 事件,
 * 从而调用 {@link UpdateAndCacheRolesResourcesService} 对应的方法.<br>
 * 建议: <br>
 * 1. 基于 角色 的权限控制: 实现所有角色 uri(资源) 的权限 Map(role, map(uri, Set(permission))) 的更新与缓存本机内存.
 * 2. 基于 SCOPE 的权限控制: 情况复杂一点, 但 SCOPE 类型比较少, 也还可以像 1 的方式实现缓存本机内存与更新.
 * 3. 基于 多租户 的权限控制: 情况比较复杂, 租户很少的情况下, 也还可以全部缓存在本机内存, 通常情况下全部缓存内存不现实, 只能借助于类似 redis 等的内存缓存.
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/8 21:15
 */
public interface UpdateAndCacheRolesResourcesService {

    /**
     * 用于基于 角色 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口
     */
    default void updateAuthoritiesOfAllRoles() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 角色 的权限控制的更新或缓存所有角色的权限服务");
    }
    /**
     * 用于基于 多租户 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口
     */
    default void updateAuthoritiesOfAllTenant() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 多租户 的权限控制的更新或缓存所有角色的权限服务");
    }
    /**
     * 用于基于 SCOPE 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口
     */
    default void updateAuthoritiesOfAllScopes() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 SCOPE 的权限控制的更新或缓存所有角色的权限服务");
    }

}
