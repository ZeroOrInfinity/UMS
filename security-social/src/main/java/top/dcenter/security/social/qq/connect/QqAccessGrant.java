package top.dcenter.security.social.qq.connect;

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
public class QqAccessGrant extends AccessGrant {

    private Long expiresIn;

    public QqAccessGrant() {
        super("");
    }

    public QqAccessGrant(String accessToken) {
        super(accessToken);
    }

    public QqAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
        super(accessToken, scope, refreshToken, expiresIn);
    }

    /**
     * 通过 expiresIn 设置 expireTime
     * @param expiresIn 毫秒
     */
    public void setExpiresTime(Long expiresIn) {
        this.expiresIn = expiresIn;
        Class<? extends QqAccessGrant> clz = this.getClass();
        try
        {
            Field expireTimeField = clz.getField("expireTime");
            expireTimeField.setAccessible(true);
            expireTimeField.set(this, expiresIn != null ? System.currentTimeMillis() + expiresIn : null);
        }
        catch (Exception e) { }
    }
}
