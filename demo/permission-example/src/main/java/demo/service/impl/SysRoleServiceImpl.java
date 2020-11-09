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

package demo.service.impl;

import demo.dao.SysRoleJpaRepository;
import demo.entity.SysRole;
import demo.entity.UriResourcesDTO;
import demo.service.SysRoleService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源服务
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/26 16:56
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole, Long> implements SysRoleService {

    private final SysRoleJpaRepository repository;

    public SysRoleServiceImpl(SysRoleJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }


    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public SysRole findByName(@NonNull String roleName) {

        return repository.findByName(roleName);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @Override
    public Map<String, Map<String, UriResourcesDTO>> getRolesAuthorities() {
        // 获取所有角色的 uri 的权限
        List<String[]> authoritiesByRoles = repository.findAuthoritiesByRoles();

        int size = authoritiesByRoles.size();

        Map<String, Map<String, UriResourcesDTO>> result = new HashMap<>(size);
        if (size < 1)
        {
            return result;
        }

        // r.name, s.url, s.permission
        authoritiesByRoles.forEach(arr -> {
            final UriResourcesDTO uriResourcesDO = new UriResourcesDTO(arr[1], arr[2]);
            result.compute(arr[0], (k, v) -> {
                if (v == null)
                {
                    v = new HashMap<>(1);
                }
                v.put(uriResourcesDO.getUrl(), uriResourcesDO);
                return v;
            });
        });

        return result;
    }
}