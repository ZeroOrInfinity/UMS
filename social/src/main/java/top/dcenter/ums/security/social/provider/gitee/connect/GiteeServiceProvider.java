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

package top.dcenter.ums.security.social.provider.gitee.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import top.dcenter.ums.security.social.provider.gitee.api.Gitee;
import top.dcenter.ums.security.social.provider.gitee.api.GiteeImpl;

/**
 * gitee 服务提供商
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/8 21:31
 */
public class GiteeServiceProvider extends AbstractOAuth2ServiceProvider<Gitee> {
    /**
     * 获取 Gitee openid 链接
     */
    public static final String ACCESS_TOKEN_URL = "https://gitee.com/oauth/token";
    /**
     * 获取 Gitee userInfo 链接
     */
    public static final String AUTHORIZE_URL = "https://gitee.com/oauth/authorize";

    private ObjectMapper objectMapper;

    /**
     * Create a new {@link OAuth2ServiceProvider}.
     *
     * @param oauth2Operations the OAuth2Operations template for conducting the OAuth 2 flow with the provider.
     * @param objectMapper
     */
    public GiteeServiceProvider(OAuth2Operations oauth2Operations, ObjectMapper objectMapper) {
        super(oauth2Operations);
        this.objectMapper = objectMapper;
    }

    public GiteeServiceProvider(String appId, String appSecret, ObjectMapper objectMapper) {
        this(new GiteeOauth2Template(appId, appSecret, AUTHORIZE_URL, ACCESS_TOKEN_URL, objectMapper), objectMapper);
    }

    @Override
    public Gitee getApi(String accessToken) {
        return new GiteeImpl(accessToken, objectMapper);
    }
}