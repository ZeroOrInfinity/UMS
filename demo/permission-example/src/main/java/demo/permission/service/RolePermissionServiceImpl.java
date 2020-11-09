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
package demo.permission.service;

import org.springframework.stereotype.Component;
import top.dcenter.ums.security.core.api.permission.service.RolePermissionsService;
import top.dcenter.ums.security.core.exception.RolePermissionsException;

import java.util.List;

/**
 * 测试角色权限服务接口切面是否生效
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/7 19:25
 */
@Component
public class RolePermissionServiceImpl implements RolePermissionsService<Object> {

    @Override
    public boolean updateResourcesOfRole(Long roleId, Long... resourceIds) throws RolePermissionsException {
        // 测试 RolePermissionServiceAspect  是否生效
        return true;
    }

    @Override
    public List<Object> findAllResourcesByRole(String role) throws RolePermissionsException {
        // do nothing
        return null;
    }
}
