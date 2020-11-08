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
 * 用于更新或缓存所有角色的权限的服务接口, 每次更新 uri(资源)权限时,需要调用此接口
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/8 21:15
 */
public interface UpdateAuthoritiesService {

    /**
     * 用于基于 角色 的权限控制的更新或缓存所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口
     */
    default void updateAuthoritiesOfAllRoles() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 角色 的权限控制的更新或缓存所有角色的权限服务");
    }
    /**
     * 用于基于 多租户 的权限控制的更新或缓存所有多租户的所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口
     */
    default void updateAuthoritiesOfAllTenant() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 多租户 的权限控制的更新或缓存所有角色的权限服务");
    }
    /**
     * 用于基于 SCOPE 的权限控制的更新或缓存所有 SCOPE 的所有角色的权限服务, 每次更新 uri(资源)权限时,需要调用此接口
     */
    default void updateAuthoritiesOfAllScopes() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 SCOPE 的权限控制的更新或缓存所有角色的权限服务");
    }

}
