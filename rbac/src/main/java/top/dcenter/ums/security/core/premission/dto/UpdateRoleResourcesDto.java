package top.dcenter.ums.security.core.premission.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.dcenter.ums.security.core.premission.enums.UpdateRolesResourcesType;
import top.dcenter.ums.security.core.premission.event.UpdateRolesResourcesEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 更新权限资源 DTO, 用于 {@link UpdateRolesResourcesEvent}
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.5 12:28
 */
@Data
@NoArgsConstructor
@Builder
public class UpdateRoleResourcesDto<T> implements Serializable {

    private static final long serialVersionUID = -8135205986821450358L;

    /**
     * 更新权限资源的类型
     */
    private UpdateRolesResourcesType updateType;
    /**
     * 多租户 ID,
     * 当多租户系统时不为 null
     */
    private Long tenantId;
    /**
     * scope ID,
     * 当 {@link UpdateRolesResourcesType} 为 SCOPE 不为 null
     */
    private Long scopeId;
    /**
     * 更新角色资源 Map(roleId, List(resourceId))
     */
    private Map<Long, List<Long>> roleResources;
    /**
     * 更新的权限资源类
     */
    private Class<T> resourceClass;
}
