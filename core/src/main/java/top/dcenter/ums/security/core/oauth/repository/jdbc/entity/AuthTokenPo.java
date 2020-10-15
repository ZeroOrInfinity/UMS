package top.dcenter.ums.security.core.oauth.repository.jdbc.entity;

import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.model.AuthToken;
import top.dcenter.ums.security.core.oauth.enums.EnableRefresh;

/**
 * {@link AuthToken} 持久化 PO
 * @author zyw
 * @version V2.0  Created by 2020/10/10 14:10
 */
@SuppressWarnings("jol")
@Setter
@Getter
public class AuthTokenPo extends AuthToken {
    private static final long serialVersionUID = -295423281641462728L;

    /**
     * tokenId
     */
    private Long id;
    /**
     * 第三方服务商(如: qq,github)
     */
    private String providerId;

    /**
     * 过期日期, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1
     */
    private Long expireTime;
    /**
     * 是否支持 refreshToken, 默认: {@code EnableRefresh.YES}. 数据库存储 int 值:1 表示支持, 0 表示不支持
     */
    private EnableRefresh enableRefresh = EnableRefresh.YES;
}
