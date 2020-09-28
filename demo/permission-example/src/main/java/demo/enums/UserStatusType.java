package demo.enums;

/**
 * 用户状态
 * @author zyw
 * @version V1.0  Created by 2020/9/27 15:40
 */
public enum UserStatusType {
    /**
     * 激活状态
     */
    NORMAL,
    /**
     * VIP
     */
    VIP,
    /**
     * SVIP
     */
    SVIP,
    /**
     * 锁定
     */
    LOCKED,
    /**
     * 过期
     */
    EXPIRED,
    /**
     * 删除
     */
    DELETED,
    /**
     * 异常
     */
    ABNORMAL
}
