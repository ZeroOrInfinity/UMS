package top.dcenter.ums.security.common.enums;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/8 13:30
 */
public enum  CsrfTokenRepositoryType {
    /**
     * 把 csrfToken 保存在 session
     */
    SESSION,
    /**
     *  把 csrfToken 保存在 redis
     */
    REDIS
}
