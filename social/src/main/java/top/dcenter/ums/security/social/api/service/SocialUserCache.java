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

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.security.SocialUserDetails;

/**
 * 从缓存中查询用户信息, 把用户信息存入缓存, 移除缓存.
 * 只是对 {@link UserCache} 的 copy, 防止 IOC 容器中的其他 UserCache 注入本应用
 * 用法可以参考 demo 模块的 <pre> demo.security.service.LoginSocialUserDetailsService</pre>
 * @see UserCache
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/31 15:30
 */
@SuppressWarnings("JavadocReference")
public interface SocialUserCache extends UserCache {

    /**
     * 只是对 {@link #getUserFromCache(String)} 返回结果转换为 {@link SocialUserDetails}
     * Obtains a {@link UserDetails} from the cache.
     *
     * @param userId the {@link User#getUsername()} used to place the user in the cache
     *
     * @return the populated <codes>UserDetails</codes> or <codes>null</codes> if the user
     * could not be found or if the cache entry has expired
     */
    SocialUserDetails getSocialUserFromCache(String userId);
}