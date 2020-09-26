package top.dcenter.ums.security.core.permission.dto;

import lombok.Data;

/**
 * @author zyw
 */
@Data
public class UriResourcesDO {

    /**
     * uri
     */
    private String url;
    /**
     * 权限, 多个权限用逗号分隔
     */
    private String permission;

}