package top.dcenter.ums.security.core.oauth.enums;

import lombok.Getter;

/**
 * 第三方服务商是否支持 refresh token 枚举
 * @author zyw
 * @version V2.0  Created by 2020/10/16 21:47
 */
public enum EnableRefresh {
    /**
     * 不支持 refresh token
     */
    NO(0, "不支持"),
    /**
     * 支持 refresh token
     */
    YES(1, "支持");

    @Getter
    private Integer code;
    @Getter
    private String msg;
    EnableRefresh(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
