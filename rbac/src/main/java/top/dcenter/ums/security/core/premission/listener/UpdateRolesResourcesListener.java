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

package top.dcenter.ums.security.core.premission.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import top.dcenter.ums.security.core.api.premission.service.UpdateCacheOfRolesResourcesService;
import top.dcenter.ums.security.core.premission.dto.UpdateRoleResourcesDto;
import top.dcenter.ums.security.core.premission.event.UpdateRolesResourcesEvent;

import static java.util.Objects.isNull;

/**
 * uri 权限更新监听器
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/2 19:53
 */
public class UpdateRolesResourcesListener implements ApplicationListener<UpdateRolesResourcesEvent> {

    private final UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService;

    public UpdateRolesResourcesListener(UpdateCacheOfRolesResourcesService updateCacheOfRolesResourcesService) {
        this.updateCacheOfRolesResourcesService = updateCacheOfRolesResourcesService;
    }

    @Async
    @Override
    public void onApplicationEvent(UpdateRolesResourcesEvent event) {
        Object source = event.getSource();
        if (source instanceof Boolean && ((Boolean) source))
        {
            UpdateRoleResourcesDto<Object> updateRoleResourcesDto = event.getUpdateRoleResourcesDto();
            switch(updateRoleResourcesDto.getUpdateType()) {
                case ROLE:
                    updateCacheOfRole(updateRoleResourcesDto);
                    break;
                case TENANT:
                    updateCacheOfTenant(updateRoleResourcesDto);
                    break;
                case SCOPE:
                    updateCacheOfScope(updateRoleResourcesDto);
                    break;
                case GROUP:
                    updateCacheOfGroup(updateRoleResourcesDto);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateCacheOfRole(UpdateRoleResourcesDto<Object> updateRoleResourcesDto) {
        updateRoleResourcesDto
                .getRoleResources()
                .forEach((roleId, resourceIds) ->
                                 this.updateCacheOfRolesResourcesService
                                         .updateAuthoritiesByRoleId(roleId,
                                                                    updateRoleResourcesDto.getResourceClass(),
                                                                    resourceIds.toArray(new Long[0])));
    }

    private void updateCacheOfTenant(UpdateRoleResourcesDto<Object> updateRoleResourcesDto) {
        updateRoleResourcesDto
                .getRoleResources()
                .forEach((roleId, resourceIds) ->
                                 this.updateCacheOfRolesResourcesService
                                         .updateAuthoritiesByRoleIdOfTenant(updateRoleResourcesDto.getTenantId(),
                                                                            roleId,
                                                                            updateRoleResourcesDto.getResourceClass(),
                                                                            resourceIds.toArray(new Long[0])));

    }

    private void updateCacheOfScope(UpdateRoleResourcesDto<Object> updateRoleResourcesDto) {
        updateRoleResourcesDto
                .getRoleResources()
                .forEach((roleId, resourceIds) ->
                                 this.updateCacheOfRolesResourcesService
                                         .updateAuthoritiesByRoleIdOfScopeId(updateRoleResourcesDto.getScopeId(),
                                                                             roleId,
                                                                             updateRoleResourcesDto.getResourceClass(),
                                                                             resourceIds.toArray(new Long[0])));
    }

    private void updateCacheOfGroup(UpdateRoleResourcesDto<Object> updateRoleResourcesDto) {
        final Long tenantId = updateRoleResourcesDto.getTenantId();
        if (isNull(tenantId)) {
            updateRoleResourcesDto
                    .getGroupRoles()
                    .forEach((groupId, roleIds) ->
                                     this.updateCacheOfRolesResourcesService
                                             .updateRolesByGroupId(groupId, roleIds.toArray(new Long[0])));
            return;
        }
        updateRoleResourcesDto
                .getGroupRoles()
                .forEach((groupId, roleIds) ->
                                 this.updateCacheOfRolesResourcesService
                                         .updateRolesByGroupIdOfTenant(tenantId, groupId,
                                                                       roleIds.toArray(new Long[0])));

    }
}