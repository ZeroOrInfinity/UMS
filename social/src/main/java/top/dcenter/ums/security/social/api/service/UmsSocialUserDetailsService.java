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

package top.dcenter.ums.security.social.api.service;

import org.springframework.social.security.SocialUserDetailsService;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;

/**
 * 用户名密码、手机短信、 OAuth 的登录与注册服务：<br><br>
 *     1. 用于用户名密码、手机短信、 OAuth 的登录逻辑。<br><br>
 *     2. 用于用户名密码、手机短信、 OAuth 的注册逻辑。<br><br>
 *     3. 如果要使用缓存, 引入 {@link SocialUserCache}, 用法可以参考
 *     <pre> demo.security.service.LoginSocialUserDetailService </pre>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/16 10:19
 */
@SuppressWarnings("JavadocReference")
public interface UmsSocialUserDetailsService extends UmsUserDetailsService, SocialUserDetailsService, SocialUserDetailsRegisterService {

}