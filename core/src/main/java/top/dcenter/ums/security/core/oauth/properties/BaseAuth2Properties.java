package top.dcenter.ums.security.core.oauth.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * OAuth2 基本属性
 * @author zyw
 * @version V1.0  Created by 2020/5/17 14:08
 */
@Getter
@Setter
public abstract class BaseAuth2Properties {

    private String clientId;
    private String clientSecret;
}
