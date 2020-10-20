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

package top.dcenter.ums.security.core.api.rememberme.repository;

import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.dcenter.ums.security.core.auth.rememberme.repository.JdbcTokenRepositoryFactory;

/**
 * PersistentTokenRepository 工厂, 将用于 {@link PersistentTokenBasedRememberMeServices} 中，用户 RememberMe 持久化. 默认实现了
 * {@link JdbcTokenRepositoryFactory}, 如需持久化到 redis, 则实现此接口并注入
 * IOC 容器即可, 会替代默认的 jdbc 的持久化.
 *
 * UserTokenRepositoryFactory
 * @author YongWu zheng
 * @version V1.0
 * Created by 2020/5/24 21:15
 */
public interface BasedRememberMeTokenRepositoryFactory {
    /**
     * 获取 UserTokenRepository
     * @return T 实现 {@link PersistentTokenRepository} 实例
     */
    PersistentTokenRepository getPersistentTokenRepository();
}