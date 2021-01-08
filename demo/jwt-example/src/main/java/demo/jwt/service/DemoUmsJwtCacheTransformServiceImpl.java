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
package demo.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import top.dcenter.ums.security.jwt.api.cache.service.JwtCacheTransformService;
import top.dcenter.ums.security.jwt.cache.service.UmsJwtCacheTransformServiceImpl;

import static java.util.Objects.nonNull;

/**
 * 示例: 只是对 {@link UmsJwtCacheTransformServiceImpl} 的拷贝.
 * 如果是 jwt + session 模式, 如需缓存自定义对象, 则必须实现此接口, 用于转换为缓存的对象.
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.1.7 20:18
 */
@RequiredArgsConstructor
@Service
public class DemoUmsJwtCacheTransformServiceImpl implements JwtCacheTransformService<JwtAuthenticationToken> {

    @Qualifier("jwtTokenRedisSerializer")
    private final RedisSerializer<JwtAuthenticationToken> redisSerializer;

    @Override
    @NonNull
    public byte[] serialize(@NonNull Authentication authentication) throws SerializationException {
        if (authentication instanceof JwtAuthenticationToken)
        {
            byte[] result = redisSerializer.serialize(((JwtAuthenticationToken) authentication));
            if (nonNull(result)) {
                return result;
            }
        }
        throw new SerializationException("序列化错误");
    }

    @Override
    @NonNull
    public JwtAuthenticationToken deserialize(@NonNull byte[] bytes) throws SerializationException {
        JwtAuthenticationToken deserialize = redisSerializer.deserialize(bytes);
        if (nonNull(deserialize)) {
            return deserialize;
        }
        throw new SerializationException("反序列化错误");
    }

    @Override
    @NonNull
    public Class<JwtAuthenticationToken> getClazz() {
        return JwtAuthenticationToken.class;
    }
}