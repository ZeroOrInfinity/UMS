package top.dcenter.security.social.qq.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.qq.api.Qq;

/**
 * qq 登录 ConnectionFactory
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class QqConnectionFactory extends BaseOAuth2ConnectionFactory<Qq> {

    public QqConnectionFactory(String providerId, String appId, String appSecret, ObjectMapper objectMapper) {
        super(providerId, new QqServiceProvider(appId, appSecret, objectMapper), new QqAdapter(providerId));
    }

    @Override
    public String generateState() {
        return generateState("/auth/callback/"+ getProviderId());
    }

}
