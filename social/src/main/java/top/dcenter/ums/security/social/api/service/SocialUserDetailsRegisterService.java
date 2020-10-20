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

import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;

/**
 * OAuth 登录的用户注册接口.<br><br>
 * 推荐通过继承来实现 {@link UmsSocialUserDetailsService} 此接口的功能
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/16 10:48
 */
public interface SocialUserDetailsRegisterService {

    /**
     * 第三方登录的用户注册
     * @param request   request
     * @param providerSignInUtils   requested
     * @return  注册后的 UserDetails 信息
     * @throws  RegisterUserFailureException  RegisterUserFailureException
     */
    SocialUserDetails registerUser(ServletWebRequest request, ProviderSignInUtils providerSignInUtils) throws RegisterUserFailureException;

}