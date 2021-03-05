package top.dcenter.ums.security.core.api.premission.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.core.annotation.Order;

/**
 * 角色权限服务接口切面接口: 主要功能是当 {@link RolePermissionsService} 角色更新权限时, 发布更新角色权限事件.<br>
 * 注意: 此切面生效前提, 事务的 {@link Order} 的值必须 大于 1, 如果是默认事务(优先级为 Integer.MAX_VALUE)不必关心这个值,
 * 如果是自定义事务, 且设置了 {@link Order} 的值, 那么值必须 大于 1.
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.5 22:16
 */
public interface RolePermissionsServiceAspect {

    /**
     * 更新角色(roleId)所拥有的 resourceIds 资源信息的切面方法
     * @param jp            {@link JoinPoint}
     * @param result        目标方法返回值
     * @param roleId        目标方法参数: 角色 id
     * @param resourceIds   目标方法参数: 资源 ids
     */
    @AfterReturning(pointcut = "execution(boolean *..updateResourcesByRoleId(..)) && args(roleId, resourceIds)",
            returning = "result", argNames = "jp, result, roleId, resourceIds")
    void handlerUpdateResourcesByRoleIdMethod(JoinPoint jp, boolean result, Long roleId, Long... resourceIds);

    /**
     * 更新多租户的角色(roleId)所拥有的 resourceIds 资源信息的切面方法
     *
     * @param jp          {@link JoinPoint}
     * @param result      目标方法返回值
     * @param tenantId    目标方法参数: 租户 id
     * @param roleId      目标方法参数: 角色 id
     * @param resourceIds 目标方法参数: 资源 ids
     */
    @AfterReturning(pointcut = "execution(boolean *..updateResourcesByRoleIdOfTenant(..)) && args(tenantId, roleId, resourceIds)",
            returning = "result", argNames = "jp, result, tenantId, roleId, resourceIds")
    void handlerUpdateResourcesByRoleIdOfTenantMethod(JoinPoint jp, boolean result, Long tenantId,
                                                      Long roleId, Long... resourceIds);

    /**
     * 更新 scopeId 的角色(roleId)所拥有的资源信息的切面方法
     *
     * @param jp          {@link JoinPoint}
     * @param result      目标方法返回值
     * @param scopeId     目标方法参数: scope id
     * @param roleId      目标方法参数: 角色 id
     * @param resourceIds 目标方法参数: 资源 ids
     */
    @AfterReturning(pointcut = "execution(boolean *..updateResourcesByScopeId(..)) && args(scopeId, roleId, resourceIds)",
            returning = "result", argNames = "jp, result, scopeId, roleId, resourceIds")
    void handlerUpdateResourcesByScopeIdMethod(JoinPoint jp, boolean result, Long scopeId,
                                               Long roleId, Long... resourceIds);

    /**
     * 更新多租户的 scopeId 的角色(roleId)所拥有的资源信息的切面方法
     * @param jp            {@link JoinPoint}
     * @param result        目标方法返回值
     * @param tenantId      目标方法参数: 租户 id
     * @param scopeId       目标方法参数: scope id
     * @param roleId        目标方法参数: 角色 id
     * @param resourceIds   目标方法参数: 资源 ids
     */
    @AfterReturning(pointcut = "execution(boolean *..updateResourcesByScopeIdOfTenant(..)) && args(tenantId, scopeId, roleId, resourceIds)",
            returning = "result", argNames = "jp, result, tenantId, scopeId, roleId, resourceIds")
    void handlerUpdateResourcesByScopeIdOfTenantMethod(JoinPoint jp, boolean result, Long tenantId,
                                                       Long scopeId, Long roleId, Long... resourceIds);
}
