/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.social.provider.weixin.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.provider.weixin.api.Weixin;

import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;


/**
 * 微信连接工厂
 * 
 * @author zhailiang
 *
 */
@SuppressWarnings("JavaDoc")
public class WeixinConnectionFactory extends BaseOAuth2ConnectionFactory<Weixin> {
	
	/**
     * @param appId
     * @param appSecret
     * @param objectMapper
     */
	public WeixinConnectionFactory(String appId, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
		super(socialProperties.getWeixin().getProviderId(), new WeixinServiceProvider(appId, appSecret, objectMapper),
		      new WeixinAdapter(),
		      socialProperties);
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
		return new OAuth2Connection<>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
		                              accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getOAuth2ServiceProvider(), getApiAdapter(extractProviderUserId(accessGrant)));
	}

	/**
	 * @see org.springframework.social.connect.support.OAuth2ConnectionFactory#createConnection(org.springframework.social.connect.ConnectionData)
	 */
	@Override
	public Connection<Weixin> createConnection(ConnectionData data) {
		return new OAuth2Connection<>(data, getOAuth2ServiceProvider(), getApiAdapter(data.getProviderUserId()));
	}
	
	private ApiAdapter<Weixin> getApiAdapter(String providerUserId) {
		return new WeixinAdapter(providerUserId);
	}
	
	@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
	private OAuth2ServiceProvider<Weixin> getOAuth2ServiceProvider() {
		return (OAuth2ServiceProvider<Weixin>) getServiceProvider();
	}

	@Override
	public String generateState() {
		// 这里不带 ServletContextPath callbackUri, 在 SocialController#authCallbackRouter(..) 会自动添加 ServletContextPath
		return generateState(this.socialProperties.getCallbackUrl() + URL_SEPARATOR + getProviderId());
	}

}