package top.dcenter.security.social.qq.connect;

import lombok.Getter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/19 1:45
 */
@Getter
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
        this.expiresIn = expiresIn * 1000L;
    }

}
