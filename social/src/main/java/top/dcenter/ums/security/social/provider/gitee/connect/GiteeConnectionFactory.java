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
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.ums.security.social.properties.SocialProperties;
import top.dcenter.ums.security.social.provider.gitee.api.Gitee;

import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * gitee 登录 ConnectionFactory
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/8 22:14
 */
public class GiteeConnectionFactory extends BaseOAuth2ConnectionFactory<Gitee> {



    public GiteeConnectionFactory(String appId, String appSecret, ObjectMapper objectMapper, SocialProperties socialProperties) {
        super(socialProperties.getGitee().getProviderId(), new GiteeServiceProvider(appId, appSecret, objectMapper),
              new GiteeAdapter(socialProperties.getGitee().getProviderId()),
              socialProperties);
    }

    @Override
    public String generateState() {
        // 这里不带 ServletContextPath callbackUri, 在 SocialController#authCallbackRouter(..) 会自动添加 ServletContextPath
        return generateState(this.socialProperties.getCallbackUrl() + URL_SEPARATOR + getProviderId());
    }

}