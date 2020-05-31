package top.dcenter.security.social.gitee.connect;

import lombok.Getter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/19 1:45
 */
@Getter
public class GiteeAccessGrant extends AccessGrant {

    private String tokenType;
    /**
     * 秒
     */
    private Long createdAt;
    /**
     * 秒
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
        this.createdAt = createdAt;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;

    }

}
