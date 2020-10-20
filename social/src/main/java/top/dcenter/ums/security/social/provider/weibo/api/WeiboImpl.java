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

package top.dcenter.ums.security.social.provider.weibo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;
import top.dcenter.ums.security.core.util.RequestUtil;

import java.io.IOException;

import static top.dcenter.ums.security.core.consts.SecurityConstants.KEY_VALUE_SEPARATOR;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_IDENTIFIER;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SEPARATOR;

/**
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/18 12:04
 */
public class WeiboImpl extends AbstractOAuth2ApiBinding implements Weibo {

    /**
     * 获取 Weibo userInfo 链接
     */
    public static final String URL_GET_USER_INFO = "https://api.weibo.com/2/users/show.json";
    /**
     * 获取 userInfo 时传递的必要参数 uid 的参数名称
     */
    public static final String REQUEST_PARAMETER_UID = "uid";
    /**
     * access_token 参数名称
     */
    public static final String ACCESS_TOKEN = "access_token";

    private String accessToken;

    private final ObjectMapper objectMapper;

    public WeiboImpl(String accessToken, ObjectMapper objectMapper) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.accessToken = accessToken;
        this.objectMapper = objectMapper;
    }


    @Override
    public WeiboUserInfo getUserInfo(String uid) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append(URL_GET_USER_INFO)
            .append(URL_PARAMETER_IDENTIFIER)
            .append(ACCESS_TOKEN)
            .append(KEY_VALUE_SEPARATOR)
            .append(accessToken)
            .append(URL_PARAMETER_SEPARATOR)
            .append(REQUEST_PARAMETER_UID)
            .append(KEY_VALUE_SEPARATOR)
            .append(uid);
        String responseBody = getRestTemplate().getForObject(url.toString(), String.class);
        // 时间格式: Sun May 29 08:37:29 +0800 2011 -> EEE MMM dd HH:mm:ss ZZZ yyyy Locale.US
        return RequestUtil.requestBody2Object(objectMapper, WeiboUserInfo.class, responseBody);
    }
}