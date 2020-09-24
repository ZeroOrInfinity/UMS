package top.dcenter.ums.security.social.provider.qq.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.provider.qq.api.Qq;

import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * qq 登录 ConnectionFactory
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class QqConnectionFactory extends BaseOAuth2ConnectionFactory<Qq> {

    public QqConnectionFactory(String appId, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
        super(socialProperties.getQq().getProviderId(), new QqServiceProvider(appId, appSecret, objectMapper),
              new QqAdapter(socialProperties.getQq().getProviderId()),
              socialProperties);
    }

    @Override
    public String generateState() {
        // 这里不带 ServletContextPath callbackUri, 在 SocialController#authCallbackRouter(..) 会自动添加 ServletContextPath
        return generateState(this.socialProperties.getCallbackUrl() + URL_SEPARATOR + getProviderId());
    }

}
