package top.dcenter.security.social.qq.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.qq.api.Qq;

import static top.dcenter.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * qq 登录 ConnectionFactory
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class QqConnectionFactory extends BaseOAuth2ConnectionFactory<Qq> {

    public QqConnectionFactory(String providerId, String appId, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
        super(providerId, new QqServiceProvider(appId, appSecret, objectMapper), new QqAdapter(providerId), socialProperties);
    }

    @Override
    public String generateState() {
        return generateState(this.socialProperties.getCallbackUrl() + URL_SEPARATOR + getProviderId());
    }

}
