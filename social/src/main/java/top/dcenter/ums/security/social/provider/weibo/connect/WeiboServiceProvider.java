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