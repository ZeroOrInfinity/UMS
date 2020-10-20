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

package top.dcenter.ums.security.core.sign.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import top.dcenter.ums.security.core.api.sign.service.SignService;
import top.dcenter.ums.security.core.auth.config.SecurityAutoConfiguration;
import top.dcenter.ums.security.core.sign.properties.SignProperties;
import top.dcenter.ums.security.core.sign.UserSignServiceImpl;

/**
 * 签到配置类
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/15 12:23
 */
@Configuration
@AutoConfigureAfter({SecurityAutoConfiguration.class})
public class SignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(type = "top.dcenter.ums.security.core.api.sign.service.SignService")
    public SignService signService(RedisConnectionFactory redisConnectionFactory, SignProperties signProperties) {
        return new UserSignServiceImpl(redisConnectionFactory, signProperties);
    }
}