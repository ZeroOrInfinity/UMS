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
package demo.permission.service.impl;

import demo.entity.SysResources;
import demo.entity.SysRoleResourcesKey;
import demo.permission.service.UriPermissionService;
import demo.service.SysRoleResourcesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import top.dcenter.ums.security.core.api.permission.service.RolePermissionsService;

import java.util.ArrayList;
import java.util.List;

/**
 * 为什么不支持 yml 配置文件 ???
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/6 20:12
 */
//@SpringBootTest
//@EnableAutoConfiguration
public class UriPermissionServiceImplTest {

    @Autowired
    UriPermissionService<SysResources> uriPermissionService;
    @Autowired
    SysRoleResourcesService sysRoleResourcesService;
    @Autowired
    RolePermissionsService rolePermissionsService;

    @Test
    public void TestRolePermissionServiceAspect(){
        final boolean b = rolePermissionsService.updateResourcesOfRole(1L, 1L, 2L);
        Assertions.assertTrue(b);
    }

    @Test
    public void TestSysRoleResourcesService() {
        List<SysRoleResourcesKey> keys = new ArrayList<>();
        final SysRoleResourcesKey key = new SysRoleResourcesKey(3L, 3L);
        final SysRoleResourcesKey key1 = new SysRoleResourcesKey(3L, 4L);
        final SysRoleResourcesKey key2 = new SysRoleResourcesKey(3L, 6L);
        final SysRoleResourcesKey key3 = new SysRoleResourcesKey(3L, 7L);
        final SysRoleResourcesKey key4 = new SysRoleResourcesKey(3L, 23L);
        final SysRoleResourcesKey key5 = new SysRoleResourcesKey(3L, 24L);
        keys.add(key);
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        keys.add(key4);
        keys.add(key5);
        final int number = sysRoleResourcesService.batchDeleteByIds(keys);
        Assertions.assertTrue(number > 0);
    }

    @Test
    public void findAllUriPermissionsByRole() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return;
        }
        final List<SysResources> sysResources = uriPermissionService.findAllResourcesByRole("ROLE_USER");
        Assertions.assertTrue(sysResources.size() > 0);
    }
}