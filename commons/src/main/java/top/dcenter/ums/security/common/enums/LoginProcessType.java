package top.dcenter.ums.security.common.enums;

/**
 * 登录后的处理方式：跳转或返回 JSON
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/4 15:29
 */
public enum LoginProcessType {
    /**
     * 登录后跳转
     */
    REDIRECT,
    /**
     * 登录后返回 JSON
     */
    JSON
}
