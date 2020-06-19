package top.dcenter.security.social.provider.weibo.connect;

import lombok.Getter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/18 14:34
 */
@Getter
public class WeiboAccessGrant extends AccessGrant {

    private static final long serialVersionUID = 1360503645657443112L;

    /**
     * provider 的 uid
     */
    private String uid;
    /**
     * 提醒: 秒
     */
    private Long remindIn;
    /**
     * 秒
     */
    private Long expiresIn;
    /**
     * 是否实名认证
     */
    private Boolean isRealName;

    public WeiboAccessGrant() {
        super("");
    }

    public WeiboAccessGrant(String accessToken) {
        super(accessToken);
    }

    public WeiboAccessGrant(String accessToken, String scope, String refreshToken,
                            Long expiresIn, Long remindIn, String uid, Boolean isRealName) {
        super(accessToken, scope, refreshToken, expiresIn);
        this.uid = uid;
        this.remindIn = remindIn;
        this.expiresIn = expiresIn;
        this.isRealName = isRealName;
    }
}
