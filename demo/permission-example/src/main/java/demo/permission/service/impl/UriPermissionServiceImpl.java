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
import demo.entity.SysRole;
import demo.entity.SysRoleResources;
import demo.permission.service.UriPermissionService;
import demo.service.SysResourcesService;
import demo.service.SysRoleResourcesService;
import demo.service.SysRoleService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.dcenter.ums.security.core.api.permission.entity.UriResourcesDTO;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;
import top.dcenter.ums.security.core.permission.event.UpdateRolesAuthoritiesEvent;
import top.dcenter.ums.security.core.util.ConvertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService.PERMISSION_DELIMITER;
import static top.dcenter.ums.security.core.api.permission.service.UriAuthorizeService.getPermission;

/**
 * uri 权限服务.<br><br>
 * 注意：<br>
 * <pre>
 *     // 修改或添加权限一定要更新 updateRolesAuthorities 缓存, 有两种方式：一种发布事件，另一种是直接调用服务；推荐用发布事件(异步执行)。
 *     // 1. 推荐用发布事件(异步执行)
 *     applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));
 *     // 2. 直接调用服务
 *     abstractUriAuthorizeService.updateRolesAuthorities();
 * </pre>
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020-09-26 22:41
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
public class UriPermissionServiceImpl implements UriPermissionService, ApplicationContextAware {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysResourcesService sysResourcesService;
    @Autowired
    private SysRoleResourcesService sysRoleResourcesService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 给角色添加权限
     * @param role                      角色
     * @param uri                       注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixType      权限后缀类型列表
     * @return  是否添加成功
     */
    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public boolean addUriPermission(@NonNull String role, @NonNull String uri,
                                    @NonNull PermissionSuffixType permissionSuffixType) {

        // 1. 获取角色
        SysRole sysRole = sysRoleService.findByName(role);
        // 角色不存在
        if (sysRole == null)
        {
            SysRole newRole = new SysRole();
            newRole.setAvailable(true);
            newRole.setName(role);
            newRole.setDescription(role);
            // 新增角色
            sysRole = sysRoleService.save(newRole);
        }

        // 2. 获取角色 uri 的对应权限资源,
        List<SysResources> sysResourcesList = sysResourcesService.findByRoleIdAndUrl(sysRole.getId(), uri);

        // 3. 新增权限资源
        String newPermission = getPermission(uri, permissionSuffixType);
        if (sysResourcesList.size() < 1)
        {

            SysResources sysResources = new SysResources();
            sysResources.setPermission(newPermission);
            sysResources.setUrl(uri);
            sysResources.setAvailable(true);
            // ...

            // 存入数据库
            sysResources = sysResourcesService.save(sysResources);

            // 创建 SysRoleResources
            SysRoleResources roleResources = new SysRoleResources();
            roleResources.setRoleId(sysRole.getId());
            roleResources.setResourcesId(sysResources.getId());

            // 保存
            sysRoleResourcesService.save(roleResources);


        }
        // 4. 更新权限资源
        else
        {
            for (SysResources sysResources : sysResourcesList)
            {
                String permission = sysResources.getPermission();
                Set<String> permissions = ConvertUtil.string2Set(permission, PERMISSION_DELIMITER);
                permissions.add(newPermission);
                permission = String.join(PERMISSION_DELIMITER, permissions);
                sysResources.setPermission(permission);
            }
            sysResourcesService.batchUpdateBySysResources(sysResourcesList);
        }

        // 5. 修改或添加权限一定要更新 updateRolesAuthorities 缓存
        applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));

        return true;
    }

    /**
     * @param role                     角色
     * @param uri                      注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixType     权限后缀类型列表
     * @return  是否成功
     */
    @Transactional(rollbackFor = {Error.class, Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public boolean delUriPermission(String role, String uri, PermissionSuffixType permissionSuffixType) {

        // 1. 获取角色
        SysRole sysRole = sysRoleService.findByName(role);
        if (sysRole == null)
        {
            return false;
        }

        // 2. 获取角色 uri 的对应资源权限
        List<UriResourcesDTO> uriResourcesDTOList =
                sysResourcesService.findUriResourcesDtoByRoleIdAndUrl(sysRole.getId(), uri);

        if (uriResourcesDTOList.size() < 1)
        {
            return true;
        }

        // 3. 删除权限
        List<Long> roleResourcesIds = new ArrayList<>();
        for (UriResourcesDTO uriResourcesDTO : uriResourcesDTOList)
        {
            if (uriResourcesDTO.getPermission().endsWith(permissionSuffixType.getPermissionSuffix()))
            {
                roleResourcesIds.add(uriResourcesDTO.getRoleResourcesId());
            }
        }
        // 删除角色与资源的关联
        sysRoleResourcesService.batchDeleteByIds(roleResourcesIds);

        // 4. 修改或添加权限一定要更新 updateRolesAuthorities 缓存
        applicationContext.publishEvent(new UpdateRolesAuthoritiesEvent(true));

        return true;

    }
}