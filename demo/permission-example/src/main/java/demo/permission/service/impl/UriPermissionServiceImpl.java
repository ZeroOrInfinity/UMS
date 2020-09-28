package demo.permission.service.impl;

import demo.entity.SysResources;
import demo.entity.SysRole;
import demo.entity.SysRoleResources;
import demo.permission.service.UriPermissionService;
import demo.service.SysResourcesService;
import demo.service.SysRoleResourcesService;
import demo.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService;
import top.dcenter.ums.security.core.permission.dto.UriResourcesDTO;
import top.dcenter.ums.security.core.permission.enums.PermissionSuffixType;
import top.dcenter.ums.security.core.util.ConvertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static top.dcenter.ums.security.core.api.permission.service.AbstractUriAuthorizeService.PERMISSION_DELIMITER;

/**
 * uri 权限服务
 * @author zyw
 * @version V1.0  Created by 2020-09-26 22:41
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection"})
public class UriPermissionServiceImpl implements UriPermissionService {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysResourcesService sysResourcesService;
    @Autowired
    private SysRoleResourcesService sysRoleResourcesService;

    @Autowired
    private AbstractUriAuthorizeService abstractUriAuthorizeService;

    /**
     * 给角色添加权限
     * @param role                      角色
     * @param uri                       注意: 此 uri 不包含 servletContextPath .
     * @param permissionSuffixTypeList  权限后缀类型列表
     * @return  是否添加成功
     */
    @Transactional(rollbackFor = {Error.class, Exception.class})
    @Override
    public boolean addUriPermission(@NonNull String role, @NonNull String uri,
                                    @NonNull List<PermissionSuffixType> permissionSuffixTypeList) {

        if (permissionSuffixTypeList.size() < 1)
        {
            return false;
        }

        // 1. 获取角色
        SysRole sysRole = sysRoleService.findByName(role);
        // 角色不存在
        if (sysRole == null)
        {
            SysRole newRole = new SysRole();
            newRole.setAvailable(true);
            newRole.setName(role);
            newRole.setDescription(role);
            // 保存角色
            sysRole = sysRoleService.save(newRole);
        }

        // 2. 获取角色 uri 的对应权限资源,
        List<SysResources> sysResourcesList = sysResourcesService.findByRoleIdAndUrl(sysRole.getId(), uri);

        // 3. 新增权限资源
        if (sysResourcesList.size() < 1)
        {

            SysResources sysResources = new SysResources();
            sysResources.setPermission(String.format("%s%s",
                                                     uri,
                                                     permissionSuffixTypeList.get(0).getPermissionSuffix()));
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
                permissions.add(String.format("%s%s", uri, permissionSuffixTypeList.get(0).getPermissionSuffix()));
                permission = String.join(PERMISSION_DELIMITER, permissions);
                sysResources.setPermission(permission);
            }
            sysResourcesService.batchUpdateBySysResources(sysResourcesList);
        }

        // 5. 修改或添加权限一定要更新 ServletContext 缓存
        abstractUriAuthorizeService.updateRolesAuthorities();

        return true;
    }

    @Transactional(rollbackFor = {Error.class, Exception.class})
    @Override
    public boolean delUriPermission(String role, String uri, List<PermissionSuffixType> permissionSuffixTypeList) {

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
            roleResourcesIds.add(uriResourcesDTO.getRoleResourcesId());
        }

        // 删除角色与资源的关联
        sysRoleResourcesService.batchDeleteByIds(roleResourcesIds);

        // 4. 修改或添加权限一定要更新 ServletContext 缓存
        abstractUriAuthorizeService.updateRolesAuthorities();

        return true;

    }
}