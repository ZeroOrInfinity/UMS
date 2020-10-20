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

package top.dcenter.ums.security.social.provider.gitee.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.dcenter.ums.security.social.provider.gitee.adapter.AbstractOAuth2ApiBinding;
import top.dcenter.ums.security.social.provider.gitee.adapter.GiteeTokenStrategy;

import java.io.IOException;

/**
 * 请求返回信息绑定服务实现
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/8 20:13
 */
@Getter
@Setter
@Slf4j
public class GiteeImpl extends AbstractOAuth2ApiBinding implements Gitee {

    /**
     * 获取 Gitee userInfo 链接
     */
    public static final String URL_GET_USER_INFO = "https://gitee.com/api/v5/user?access_token=%s";
    private String accessToken;

    private final ObjectMapper objectMapper;

    public GiteeImpl(String accessToken, ObjectMapper objectMapper) {
        super(accessToken, GiteeTokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.accessToken = accessToken;
        this.objectMapper = objectMapper;

    }

    @Override
    public GiteeUserInfo getUserInfo() throws IOException {
        String url = String.format(URL_GET_USER_INFO, accessToken);
        String response = getRestTemplate().getForObject(url, String.class);
        if (log.isDebugEnabled())
        {
            log.debug("gitee userInfo = {}", response);
        }
        GiteeUserInfo giteeUserInfo = objectMapper.readValue(response, GiteeUserInfo.class);
        return giteeUserInfo;
    }

}