package top.dcenter.security.social.gitee.connect;

import lombok.Getter;
import lombok.Setter;
import org.springframework.social.oauth2.AccessGrant;

import java.lang.reflect.Field;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/19 1:45
 */
@Getter
@Setter
public class GiteeAccessGrant extends AccessGrant {

    private String tokenType;
    /**
     * 毫秒
     */
    private Long createdAt;
    /**
     * 毫秒
     */
    private Long expiresIn;

    public GiteeAccessGrant() {
        super("");
    }

    public GiteeAccessGrant(String accessToken) {
        super(accessToken);
    }

    public GiteeAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, Long createdAt,
                            String tokenType) {
        super(accessToken, scope, refreshToken, expiresIn);
        this.createdAt = createdAt * 1000L;
        this.tokenType = tokenType;
    }

    /**
     * 通过 expiresIn 设置 expireTime
     * @param expiresIn 毫秒
     */
    public void setExpiresTime(Long expiresIn) {
        this.expiresIn = expiresIn;
        Class<? extends GiteeAccessGrant> clz = this.getClass();
        try
        {
            Field expireTimeField = clz.getField("expireTime");
            expireTimeField.setAccessible(true);
            expireTimeField.set(this, expiresIn != null ? this.createdAt + expiresIn : null);
        }
        catch (Exception e) { }
    }
}
