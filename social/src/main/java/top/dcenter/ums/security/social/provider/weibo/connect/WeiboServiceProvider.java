package top.dcenter.ums.security.social.provider.weibo.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import top.dcenter.ums.security.social.provider.weibo.api.Weibo;
import top.dcenter.ums.security.social.provider.weibo.api.WeiboImpl;

/**
 * Twitter ServiceProvider implementation that exposes the Twitter 4j API
 * binding.
 * 
 * @author Craig Walls
 */
public final class WeiboServiceProvider extends
		AbstractOAuth2ServiceProvider<Weibo> {

	/**
	 * 获取 Weibo openid 链接
	 */
	public static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize";
	/**
	 * 获取 Weibo userInfo 链接
	 */
	public static final String ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";

	private ObjectMapper objectMapper;

	public WeiboServiceProvider(String consumerKey, String consumerSecret, ObjectMapper objectMapper) {
		super(new WeiboOAuth2Template(consumerKey, consumerSecret,
		                              AUTHORIZE_URL,
		                              ACCESS_TOKEN_URL,
		                              objectMapper));
		this.objectMapper = objectMapper;
	}

	@Override
	public Weibo getApi(String accessToken) {
		return new WeiboImpl(accessToken, objectMapper);
	}

}