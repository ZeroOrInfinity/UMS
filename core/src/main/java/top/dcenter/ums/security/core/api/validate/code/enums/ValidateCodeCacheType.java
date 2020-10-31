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
package top.dcenter.ums.security.core.api.validate.code.enums;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.ValidateCode;
import top.dcenter.ums.security.core.util.MvcUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 验证码缓存类型
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/30 15:47
 */
@Slf4j
public enum ValidateCodeCacheType {
    /**
     * 验证码缓存在 session
     */
    SESSION() {
        @Override
        public boolean save(@NonNull ServletWebRequest request, @NonNull ValidateCode validateCode,
                            @NonNull ValidateCodeType validateCodeType, @Nullable StringRedisTemplate stringRedisTemplate) {
            HttpServletRequest req = request.getRequest();
            try
            {
                // 移除不必要的属性值(图片等)
                removeUnnecessaryFieldValue(validateCode);

                req.getSession().setAttribute(validateCodeType.getKeyPrefix(), validateCode);
            }
            catch (Exception e)
            {
                String msg = String.format("验证码保存到 Session 失败: error=%s, ip=%s, code=%s",
                                           e.getMessage(),
                                           req.getRemoteAddr(),
                                           validateCode);
                log.error(msg, e);
                return false;
            }
            return true;
        }

        @Override
        public ValidateCode getCodeInCache(@NonNull ServletWebRequest request, @NonNull ValidateCodeType validateCodeType,
                                           @NonNull Class<? extends ValidateCode> clz, @Nullable StringRedisTemplate stringRedisTemplate) {
            return clz.cast(request.getRequest().getSession().getAttribute(validateCodeType.getKeyPrefix()));
        }

        @Override
        public void removeCache(@NonNull ServletWebRequest request, @NonNull ValidateCodeType validateCodeType,
                                @Nullable StringRedisTemplate stringRedisTemplate) {
            request.getRequest().getSession().removeAttribute(validateCodeType.getKeyPrefix());
        }

    },
    /**
     * 验证码缓存在 redis
     */
    REDIS() {
        @Override
        public boolean save(@NonNull ServletWebRequest request, @NonNull ValidateCode validateCode,
                            @NonNull ValidateCodeType validateCodeType, StringRedisTemplate stringRedisTemplate) {

            Objects.requireNonNull(stringRedisTemplate, "stringRedisTemplate cannot be null");
            try {
                removeUnnecessaryFieldValue(validateCode);
                stringRedisTemplate.opsForValue().set(validateCodeType.getKeyPrefix() + request.getSessionId(),
                                                      MvcUtil.toJsonString(validateCode),
                                                      validateCodeType.getExpireIn(),
                                                      TimeUnit.SECONDS);
            }
            catch (Exception e) {
                String msg = String.format("验证码保存到 redis 失败: error=%s, ip=%s, code=%s",
                                           e.getMessage(),
                                           request.getRequest().getRemoteAddr(),
                                           validateCode);
                log.error(msg, e);
                return false;
            }
            return true;
        }

        @Override
        public ValidateCode getCodeInCache(@NonNull ServletWebRequest request, @NonNull ValidateCodeType validateCodeType,
                                           @NonNull Class<? extends ValidateCode> clz,
                                           StringRedisTemplate stringRedisTemplate) {
            final String json = stringRedisTemplate.opsForValue().get(validateCodeType.getKeyPrefix() + request.getSessionId());

            if (!StringUtils.hasText(json)) {
                return null;
            }

            return MvcUtil.json2Object(json, clz);
        }

        @Override
        public void removeCache(@NonNull ServletWebRequest request, @NonNull ValidateCodeType validateCodeType,
                                StringRedisTemplate stringRedisTemplate) {

            stringRedisTemplate.delete(validateCodeType.getKeyPrefix() + request.getSessionId());

        }
    };

    /**
     * 保存验证码到缓存
     * @param request               {@link ServletWebRequest}
     * @param validateCode          验证码
     * @param validateCodeType      验证码类型
     * @param stringRedisTemplate   stringRedisTemplate, 缓存类型不为 redis 时可以为 null
     * @return  返回是否成功保存验证码到缓存
     */
    public abstract boolean save(@NonNull ServletWebRequest request, @NonNull ValidateCode validateCode,
                                 @NonNull ValidateCodeType validateCodeType, @Nullable StringRedisTemplate stringRedisTemplate);

    /**
     * 从缓存中获取验证码
     * @param request               {@link ServletWebRequest}
     * @param validateCodeType      验证码类型
     * @param clz                   验证码的 class
     * @param stringRedisTemplate   stringRedisTemplate, 缓存类型不为 redis 时可以为 null
     * @return  返回缓存中的验证码
     */
    public abstract ValidateCode getCodeInCache(@NonNull ServletWebRequest request,
                                                @NonNull ValidateCodeType validateCodeType,
                                                @NonNull Class<? extends ValidateCode> clz,
                                                @Nullable StringRedisTemplate stringRedisTemplate);

    /**
     * 从缓存中移除验证码缓存
     * @param request               {@link ServletWebRequest}
     * @param validateCodeType      验证码类型
     * @param stringRedisTemplate   stringRedisTemplate, 缓存类型不为 redis 时可以为 null
     */
    public abstract void removeCache(@NonNull ServletWebRequest request, @NonNull ValidateCodeType validateCodeType,
                                     @Nullable StringRedisTemplate stringRedisTemplate);

    /**
     * 移除不必要的属性值
     * @param validateCode  验证码
     * @throws IllegalAccessException   IllegalAccessException
     */
    private static void removeUnnecessaryFieldValue(ValidateCode validateCode) throws IllegalAccessException {

        Field[] fields = validateCode.getClass().getDeclaredFields();

        for (Field field : fields)
        {
            field.setAccessible(true);
            Transient aTransient = field.getDeclaredAnnotation(Transient.class);
            if (aTransient != null)
            {
                field.set(validateCode, null);
            }
        }
    }
}
