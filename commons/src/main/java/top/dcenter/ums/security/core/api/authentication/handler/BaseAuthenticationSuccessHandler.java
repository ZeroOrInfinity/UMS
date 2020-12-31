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

package top.dcenter.ums.security.core.api.authentication.handler;

import org.springframework.lang.NonNull;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * 认证成功处理器, 继承此类后，再向 IOC 容器注册自己来实现自定义功能。默认为 {@code ClientAuthenticationSuccessHandler}
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/29 12:32
 */
public abstract class BaseAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD", "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    protected boolean useReferer = false;
    /**
     * 使用 userReferer 时, 如果 referer 是属于 ignoreUrls, 则跳转到 defaultTargetUrl
     */
    protected Set<String> ignoreUrls = new HashSet<>();

    /**
     * 第三方登录回调 URL, 不包含 ServletContextPath
     */
    protected String auth2RedirectUrl;

    /**
     * 添加 ignoreUrl
     * @param ignoreUrl  ignoreUrl
     */
    public void addIgnoreUrl(@NonNull String ignoreUrl) {
        ignoreUrls.add(ignoreUrl);
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public void setAuth2RedirectUrl(String auth2RedirectUrl) {
        this.auth2RedirectUrl = auth2RedirectUrl;
    }
}