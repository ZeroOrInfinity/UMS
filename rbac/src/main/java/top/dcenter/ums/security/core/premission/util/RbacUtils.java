package top.dcenter.ums.security.core.premission.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.dcenter.ums.security.core.api.premission.service.UpdateCacheOfRolesResourcesService;
import top.dcenter.ums.security.core.premission.dto.UpdateRoleResourcesDto;

import static java.util.Objects.isNull;

/**
 * RBAC 工具集
 *
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.19 14:51
 */
public class RbacUtils {

    /**
     * 本地缓存权限更新
     *
     * @param updateRoleResourcesDto             {@link UpdateRoleResourcesDto}
     * @param updateCacheOfRolesResourcesService {@link UpdateCacheOfRolesResourcesService}, 支持 null 值.
     */
    public static void localCacheRbacPermissionsUpdate(@NonNull UpdateRoleResourcesDto<Object> updateRoleResourcesDto,
                                                       @Nullable UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {

        // 本地缓存权限更新
        switch (updateRoleResourcesDto.getUpdateType()) {
            case ROLE:
                updateCacheOfRole(updateRoleResourcesDto, updateCacheOfRolesResourcesService);
                break;
            case TENANT:
                updateCacheOfTenant(updateRoleResourcesDto, updateCacheOfRolesResourcesService);
                break;
            case SCOPE:
                updateCacheOfScope(updateRoleResourcesDto, updateCacheOfRolesResourcesService);
                break;
            case GROUP:
                updateCacheOfGroup(updateRoleResourcesDto, updateCacheOfRolesResourcesService);
                break;
            case ALL:
                initAllAuthorities(updateCacheOfRolesResourcesService);
                break;
            default:
                break;
        }
    }

    /**
     * 更新所有权限缓存
     *
     * @param updateCacheOfRolesResourcesService {@link UpdateCacheOfRolesResourcesService}, 支持 null 值.
     */
    public static void initAllAuthorities(@Nullable UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {

        if (null != updateCacheOfRolesResourcesService) {
            updateCacheOfRolesResourcesService.initAllAuthorities();
        }
    }

    /**
     * 基于角色的权限缓存更新
     * @param updateRoleResourcesDto                {@link UpdateRoleResourcesDto}
     * @param updateCacheOfRolesResourcesService    {@link UpdateCacheOfRolesResourcesService}, 支持 null 值.
     */
    public static void updateCacheOfRole(@NonNull UpdateRoleResourcesDto<Object> updateRoleResourcesDto,
                                         @Nullable UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {
        if (null != updateCacheOfRolesResourcesService) {
            updateRoleResourcesDto
                    .getRoleResources()
                    .forEach((roleId, resourceIds) ->
                                     updateCacheOfRolesResourcesService
                                             .updateAuthoritiesByRoleId(roleId,
                                                                        updateRoleResourcesDto.getResourceClass(),
                                                                        resourceIds.toArray(new Long[0])));
        }
    }

    /**
     * 基于多租户的权限缓存更新
     * @param updateRoleResourcesDto                {@link UpdateRoleResourcesDto}
     * @param updateCacheOfRolesResourcesService    {@link UpdateCacheOfRolesResourcesService}, 支持 null 值.
     */
    public static void updateCacheOfTenant(@NonNull UpdateRoleResourcesDto<Object> updateRoleResourcesDto,
                                           @Nullable UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {
        if (null != updateCacheOfRolesResourcesService) {
            updateRoleResourcesDto
                    .getRoleResources()
                    .forEach((roleId, resourceIds) ->
                                     updateCacheOfRolesResourcesService
                                             .updateAuthoritiesByRoleIdOfTenant(updateRoleResourcesDto.getTenantId(),
                                                                                roleId,
                                                                                updateRoleResourcesDto.getResourceClass(),
                                                                                resourceIds.toArray(new Long[0])));
        }

    }

    /**
     * 基于 SCOPE 的权限缓存更新
     * @param updateRoleResourcesDto                {@link UpdateRoleResourcesDto}
     * @param updateCacheOfRolesResourcesService    {@link UpdateCacheOfRolesResourcesService}, 支持 null 值.
     */
    public static void updateCacheOfScope(@NonNull UpdateRoleResourcesDto<Object> updateRoleResourcesDto,
                                          @Nullable UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {
        if (null != updateCacheOfRolesResourcesService) {
            updateRoleResourcesDto
                    .getRoleResources()
                    .forEach((roleId, resourceIds) ->
                                     updateCacheOfRolesResourcesService
                                             .updateAuthoritiesByRoleIdOfScopeId(updateRoleResourcesDto.getScopeId(),
                                                                                 roleId,
                                                                                 updateRoleResourcesDto.getResourceClass(),
                                                                                 resourceIds.toArray(new Long[0])));
        }
    }

    /**
     * 基于 组 的权限缓存更新
     * @param updateRoleResourcesDto                {@link UpdateRoleResourcesDto}
     * @param updateCacheOfRolesResourcesService    {@link UpdateCacheOfRolesResourcesService}, 支持 null 值.
     */
    public static void updateCacheOfGroup(@NonNull UpdateRoleResourcesDto<Object> updateRoleResourcesDto,
                                          @Nullable UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {
        if (null != updateCacheOfRolesResourcesService) {
            final Long tenantId = updateRoleResourcesDto.getTenantId();
            if (isNull(tenantId)) {
                updateRoleResourcesDto
                        .getGroupRoles()
                        .forEach((groupId, roleIds) ->
                                         updateCacheOfRolesResourcesService
                                                 .updateRolesByGroupId(groupId, roleIds.toArray(new Long[0])));
                return;
            }

            updateRoleResourcesDto
                    .getGroupRoles()
                    .forEach((groupId, roleIds) ->
                                     updateCacheOfRolesResourcesService
                                             .updateRolesByGroupIdOfTenant(tenantId, groupId,
                                                                           roleIds.toArray(new Long[0])));
        }

    }
}
