package top.dcenter.ums.security.core.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zyw
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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