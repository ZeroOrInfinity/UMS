package top.dcenter.security.oauth2.enums;

/**
 * OAuth2 的授权类型
 * @author zyw
 * @version V1.0  Created by 2020/6/27 17:31
 */
public enum AuthorizedGrantType {
    REFRESH_TOKEN,
    AUTHORIZATION_CODE,
    PASSWORD,
    CLIENT_CREDENTIALS,
    IMPLICIT;
}
