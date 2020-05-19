package top.dcenter.security.social.qq.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import top.dcenter.security.social.qq.api.Qq;

/**
 * qq 登录 ConnectionFactory
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class QqConnectionFactory extends OAuth2ConnectionFactory<Qq> {

    public QqConnectionFactory(String providerId, String appId, String appSecret, ObjectMapper objectMapper) {
        super(providerId, new QqServiceProvider(appId, appSecret, objectMapper), new QqAdapter(providerId));
    }
}
