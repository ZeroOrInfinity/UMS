package top.dcenter.ums.security.core.permission.dto;

import lombok.Data;

/**
 * uri 权限资源实体
 * @author zyw
 */
@Data
public class UriResourcesDTO {


    public UriResourcesDTO() {
    }

    public UriResourcesDTO(String url, String permission) {
        this.url = url;
        this.permission = permission;
    }

    public UriResourcesDTO(Long roleId, Long resourcesId, Long roleResourcesId, String url, String permission) {
        this.roleId = roleId;
        this.resourcesId = resourcesId;
        this.roleResourcesId = roleResourcesId;
        this.url = url;
        this.permission = permission;
    }

    /**
     * 角色 Id
     */
    private Long roleId;
    /**
     * 资源 Id
     */
    private Long resourcesId;

    /**
     * 角色资源 Id
     */
    private Long roleResourcesId;

    /**
     * uri
     */
    private String url;
    /**
     * 权限, 多个权限用逗号分隔
     */
    private String permission;

}