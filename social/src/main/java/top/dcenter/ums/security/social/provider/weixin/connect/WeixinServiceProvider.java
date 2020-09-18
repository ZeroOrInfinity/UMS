/**
 * 
 */
package top.dcenter.ums.security.social.provider.weixin.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import top.dcenter.ums.security.social.provider.weixin.api.Weixin;
import top.dcenter.ums.security.social.provider.weixin.api.WeixinImpl;


/**
 * 
 * 微信的OAuth2流程处理器的提供器，供spring social的connect体系调用
 * 
 * @author zhailiang
 *
 */
public class WeixinServiceProvider extends AbstractOAuth2ServiceProvider<Weixin> {
	
	/**
	 * 微信获取授权码的url
	 */
	private static final String URL_AUTHORIZE = "https://open.weixin.qq.com/connect/qrconnect";
	/**
	 * 微信获取accessToken的url
	 */
	private static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

	private ObjectMapper objectMapper;

	public WeixinServiceProvider(OAuth2Operations oauth2Operations, ObjectMapper objectMapper) {
		super(oauth2Operations);
		this.objectMapper = objectMapper;
	}

	/**
     * @param appId
     * @param appSecret
     * @param objectMapper
     */
	public WeixinServiceProvider(String appId, String appSecret, ObjectMapper objectMapper) {
		this(new WeixinOAuth2Template(appId, appSecret,URL_AUTHORIZE,URL_ACCESS_TOKEN, objectMapper), objectMapper);
	}


	/**
	 * @see org.springframework.social.oauth2.AbstractOAuth2ServiceProvider#getApi(java.lang.String)
	 */
	@Override
	public Weixin getApi(String accessToken) {
		return new WeixinImpl(accessToken, objectMapper);
	}

}
