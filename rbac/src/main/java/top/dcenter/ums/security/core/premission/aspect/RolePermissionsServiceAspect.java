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
package top.dcenter.ums.security.core.premission.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.api.premission.service.RolePermissionsService;
import top.dcenter.ums.security.core.premission.enums.ResourcesType;
import top.dcenter.ums.security.core.premission.event.UpdateRolesResourcesEvent;

import static top.dcenter.ums.security.common.consts.TransactionalConstants.ONE_PRECEDENCE;

/**
 * 角色权限服务接口切面: 主要功能是当 {@link RolePermissionsService} 角色更新权限时, 发布更新角色权限事件.<br>
 * 注意: 此切面生效前提, 事务的 {@link Order} 的值必须 大于 1, 如果是默认事务(优先级为 Integer.MAX_VALUE)不必关心这个值,
 * 如果是自定义事务, 且设置了 {@link Order} 的值, 那么值必须 大于 1.
 * @author YongWu zheng
 * @version V2.0  Created by 2020/11/7 18:41
 */
@Aspect
@Slf4j
@Order(ONE_PRECEDENCE)
public class RolePermissionsServiceAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @AfterReturning(pointcut = "execution(boolean *..updateResourcesOfRole(..)) && args(roleId, resourceIds)",
            returning = "result", argNames = "jp, result, roleId, resourceIds")
    public void handlerUpdateRolesAuthoritiesMethod(JoinPoint jp, boolean result, Long roleId, Long... resourceIds) {
        if (jp.getTarget() instanceof RolePermissionsService) {
            if (result) {
                applicationContext.publishEvent(new UpdateRolesResourcesEvent(true,
                                                                              ResourcesType.ALL_ROLE,
                                                                              null, null));
            }
        }
    }


    @AfterReturning(pointcut = "execution(boolean *..updateAuthoritiesOfAllTenant(..)) && args(tenantAuthority, roleId, resourceIds)",
                    returning = "result", argNames = "jp, result, tenantAuthority, roleId, resourceIds")
    public void handlerUpdateTenantsAuthoritiesMethod(JoinPoint jp, boolean result, String tenantAuthority,
                                                      Long roleId, Long... resourceIds) {
        if (jp.getTarget() instanceof RolePermissionsService) {
            if (result) {
                applicationContext.publishEvent(new UpdateRolesResourcesEvent(true,
                                                                              ResourcesType.ALL_TENANT,
                                                                              null, null));
            }
        }
    }

    @AfterReturning(pointcut = "execution(boolean *..updateAuthoritiesOfAllScopes(..)) && args(scopeAuthority, roleId, resourceIds)",
                    returning = "result", argNames = "jp, result, scopeAuthority, roleId, resourceIds")
    public void handlerUpdateScopesAuthoritiesMethod(JoinPoint jp, boolean result, String scopeAuthority,
                                                     Long roleId, Long... resourceIds) {
        if (jp.getTarget() instanceof RolePermissionsService) {
            if (result) {
                applicationContext.publishEvent(new UpdateRolesResourcesEvent(true, ResourcesType.ALL_SCOPE,
                                                                              null, null));
            }
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
