/**
 * 
 */
package top.dcenter.security.social.weixin.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.security.social.weixin.api.Weixin;


/**
 * 微信连接工厂
 * 
 * @author zhailiang
 *
 */
public class WeixinConnectionFactory extends BaseOAuth2ConnectionFactory<Weixin> {
	
	/**
     * @param appId
     * @param appSecret
     * @param objectMapper
     */
	public WeixinConnectionFactory(String providerId, String appId, String appSecret, ObjectMapper objectMapper) {
		super(providerId, new WeixinServiceProvider(appId, appSecret, objectMapper), new WeixinAdapter());
	}
	
	/**
	 * 由于微信的openId是和accessToken一起返回的，所以在这里直接根据accessToken设置providerUserId即可，不用像QQ那样通过QQAdapter来获取
	 */
	@Override
	protected String extractProviderUserId(AccessGrant accessGrant) {
		if(accessGrant instanceof WeixinAccessGrant) {
			return ((WeixinAccessGrant)accessGrant).getOpenId();
		}
		return null;
	}
	
	/**
	 * @see org.springframework.social.connect.support.OAuth2ConnectionFactory#createConnection(org.springframework.social.oauth2.AccessGrant)
	 */
	@Override
	public Connection<Weixin> createConnection(AccessGrant accessGrant) {
		return new OAuth2Connection<Weixin>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
				accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getOAuth2ServiceProvider(), getApiAdapter(extractProviderUserId(accessGrant)));
	}

	/**
	 * @see org.springframework.social.connect.support.OAuth2ConnectionFactory#createConnection(org.springframework.social.connect.ConnectionData)
	 */
	@Override
	public Connection<Weixin> createConnection(ConnectionData data) {
		return new OAuth2Connection<Weixin>(data, getOAuth2ServiceProvider(), getApiAdapter(data.getProviderUserId()));
	}
	
	private ApiAdapter<Weixin> getApiAdapter(String providerUserId) {
		return new WeixinAdapter(providerUserId);
	}
	
	private OAuth2ServiceProvider<Weixin> getOAuth2ServiceProvider() {
		return (OAuth2ServiceProvider<Weixin>) getServiceProvider();
	}

	@Override
	public String generateState() {
		return generateState("/auth/callback/"+ getProviderId());
	}

}
