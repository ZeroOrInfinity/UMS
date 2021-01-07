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
package top.dcenter.ums.security.jwt.api.cache.service;

import org.springframework.security.core.Authentication;
import top.dcenter.ums.security.jwt.config.RedisSerializerAutoConfiguration;

/**
 * 如果是 jwt + session 模式, 如需缓存自定义对象, 则必须实现此接口, 用于转换为缓存的对象.
 *
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.7 19:54
 */
public interface JwtCacheTransformService<T> {

    /**
     * 转换为需要保存到 cache 的对象, 如果缓存到 redis, 转换类 T 必须支持 Jackson2 反序列化, 具体看 {@link RedisSerializerAutoConfiguration}.
     * @param authentication    {@link Authentication}
     * @return 需要缓存的对象.
     */
    T transform(Authentication authentication);

    /**
     * 保存在缓存中的对象类型
     * @return  {@code Class<T>}
     */
    Class<T> getClazz();

}
