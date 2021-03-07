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
package top.dcenter.ums.security.core.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.premission.service.AbstractUriAuthorizeService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/11 17:09
 */
@Component
@Slf4j
public class DemoUriAuthorizeServiceImpl extends AbstractUriAuthorizeService {

    @Override
    @NonNull
    public Map<String, Map<String, Set<String>>> getRolesAuthorities() {
        // do nothing 具体看 permission-example 的 demo.permission.service.impl.UriAuthorizeServiceImpl
        return new HashMap<>(0);
    }

    @Override
    public void updateAuthoritiesOfAllRoles() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现 更新角色(roleId)所拥有的 resourceIds 资源信息缓存的接口逻辑");
    }

    @Override
    protected void updateAuthoritiesOfAllTenant() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 多租户 的权限控制的更新或缓存所有角色的权限服务");
    }

    @Override
    protected void updateAuthoritiesOfAllScopes() {
        // do nothing, 需要时实现此逻辑
        throw new RuntimeException("未实现基于 SCOPE 的权限控制的更新或缓存所有角色的权限服务");
    }

    @NonNull
    @Override
    protected Set<String> getRolesByGroup(@NonNull String groupAuthority) {
        return Collections.emptySet();
    }

    @NonNull
    @Override
    protected Set<String> getRolesByGroupOfTenant(@NonNull String tenantAuthority,
                                                  @NonNull String groupAuthority) {
        return Collections.emptySet();
    }
}
