package top.dcenter.ums.security.social.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/17 14:08
 */
@Getter
@Setter
public abstract class AbstractSocialBaseProperties {

    private String appId;
    private String appSecret;
}
