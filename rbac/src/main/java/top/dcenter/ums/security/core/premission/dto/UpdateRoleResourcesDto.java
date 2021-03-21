package top.dcenter.ums.security.core.premission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.premission.enums.UpdateRolesResourcesType;
import top.dcenter.ums.security.core.premission.event.UpdateRolesResourcesEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 更新权限资源 DTO, 用于 {@link UpdateRolesResourcesEvent}
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.5 12:28
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
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
     * 更新组角色资源 Map(groupId, Set(roleId)),
     * 当 {@link UpdateRolesResourcesType} 为 GROUP 不为 null
     */
    private Map<Long, Set<Long>> groupRoles;
    /**
     * 更新角色资源 Map(roleId, List(resourceId)),
     * 当 {@link UpdateRolesResourcesType} 为 GROUP 为 null
     */
    private Map<Long, List<Long>> roleResources;
    /**
     * 更新的权限资源类
     */
    private Class<T> resourceClass;


    /**
     * Used by Jackson to set the remote class name of the resourceClass implementation. If the
     * implementing class is unknown to this app, throw {@link ClassNotFoundException}.
     * @param resourceClassName the fq class name of the event implementation, not null
     */
    @JsonProperty("resourceClass")
    @SuppressWarnings("unchecked")
    public void setResourceClassName(@NonNull String resourceClassName) throws ClassNotFoundException {
        this.resourceClass = (Class<T>) Class.forName(resourceClassName);
    }

}
