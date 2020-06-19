package top.dcenter.security.social.provider.weibo.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.properties.SocialProperties;
import top.dcenter.security.social.provider.weibo.api.Weibo;

import java.util.Date;

import static top.dcenter.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * WeiboConnectionFactory that creates connections that expose the Weibo API
 * binding.
 * 
 * @author edva8332
 */
public class WeiboConnectionFactory extends BaseOAuth2ConnectionFactory<Weibo> {

	public WeiboConnectionFactory(String appKey, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
		super(socialProperties.getWeibo().getProviderId(), new WeiboServiceProvider(appKey, appSecret, objectMapper),
		      new WeiboAdapter(), socialProperties);
	}

	@Override
	public Connection<Weibo> createConnection(ConnectionData data) {
		Connection<Weibo> result = null;
		if (data.getExpireTime() == null
				|| new Date(data.getExpireTime()).after(new Date())) {
			result = super.createConnection(data);
		}
		return result;
	}

	@Override
	public String generateState() {
		return generateState(this.socialProperties.getCallbackUrl() + URL_SEPARATOR + getProviderId());
	}

	@Override
	public Connection<Weibo> createConnection(AccessGrant accessGrant) {
		String uid = ((WeiboAccessGrant) accessGrant).getUid();
		((WeiboAdapter) getApiAdapter()).setUid(uid);
 		return new OAuth2Connection<>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
		                              accessGrant.getRefreshToken(), accessGrant.getExpireTime(),
		                              (OAuth2ServiceProvider<Weibo>) getServiceProvider(), getApiAdapter());
	}

}