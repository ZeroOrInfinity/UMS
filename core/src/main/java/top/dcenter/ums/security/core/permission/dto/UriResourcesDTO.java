package top.dcenter.ums.security.core.permission.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zyw
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UriResourcesDTO {

    /**
     * uri
     */
    private String url;
    /**
     * 权限, 多个权限用逗号分隔
     */
    private String permission;

}