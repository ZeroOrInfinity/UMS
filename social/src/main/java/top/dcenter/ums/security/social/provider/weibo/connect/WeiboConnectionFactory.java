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

package top.dcenter.ums.security.social.provider.weibo.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.provider.weibo.api.Weibo;

import java.util.Date;

import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;

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
		// 这里不带 ServletContextPath callbackUri, 在 SocialController#authCallbackRouter(..) 会自动添加 ServletContextPath
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