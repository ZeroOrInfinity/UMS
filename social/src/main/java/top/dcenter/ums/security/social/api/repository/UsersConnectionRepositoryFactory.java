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

package top.dcenter.ums.security.social.api.repository;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import top.dcenter.ums.security.social.properties.SocialProperties;

import javax.sql.DataSource;

/**
 * UsersConnectionRepository 工厂
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/13 23:04
 */
public interface UsersConnectionRepositoryFactory {
    /**
     * UsersConnectionRepository 工厂
     * @param dataSource
     * @param connectionFactoryLocator
     * @param textEncryptor     对 key 与 secret 进行加解密。
     * @param socialProperties
     * @param connectionSignUp 用于第三方登录自动注册为用户功能的开关，共有两个条件, 这俩个条件同时满足时才有效（另一个是 autoSignIn）：<br><br>
     *                         当传 null 值时关闭自动注册，当不为 null 且 autoSignIn=true 时开启自动注册功能，需自己实现 ConnectionSignUp，
     *                         {@link ConnectionSignUp#execute(Connection)} 从第三方的 connection 中获取用户唯一标识。<br><br>
     *
     * @param autoSignIn     当传 false 时关闭自动注册，当为 true 且 connectionSignUp 不为 null 时开启自动注册功能，<br><br>
     *                       通过配置 ums.social.{providerId}.autoSignIn=true，默认为 false，可以从socialProperties获取。
     * @return
     */
    UsersConnectionRepository getUsersConnectionRepository(DataSource dataSource,
                                                           ConnectionFactoryLocator connectionFactoryLocator,
                                                           TextEncryptor textEncryptor,
                                                           SocialProperties socialProperties,
                                                           ConnectionSignUp connectionSignUp,
                                                           Boolean autoSignIn);
}