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

package top.dcenter.ums.security.core.api.validate.code;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeCacheType;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.common.utils.IpUtil;

import javax.servlet.http.HttpServletRequest;

import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_ERROR;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EXISTS_IN_CACHE;

/**
 * 验证码处理逻辑接口
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0
 * Created by 2020/5/6 10:00
 */
public interface ValidateCodeProcessor {
    /**
     * 处理验证码逻辑：产生，缓存到session，发送
     * @param request   ServletWebRequest
     * @return  是否成功的状态
     * @throws ValidateCodeException 验证码异常
     */
    boolean produce(ServletWebRequest request) throws ValidateCodeException;

    /**
     * 获取验证码类型
     * @return  {@link ValidateCodeType}
     */
    ValidateCodeType getValidateCodeType();

    /**
     * 产生验证码
     * @param request ServletWebRequest
     * @return  验证码对象
     */
    ValidateCode generate(ServletWebRequest request);

    /**
     * 缓存验证码
     * @param request   ServletWebRequest
     * @param validateCode  验证码对象
     * @return  是否成功的状态
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean save(ServletWebRequest request, ValidateCode validateCode);

    /**
     * 发送验证码
     * @param request   ServletWebRequest
     * @param validateCode  验证码对象
     * @return  是否成功的状态
     */
    boolean sent(ServletWebRequest request, ValidateCode validateCode);

    /**
     * 校验验证码
     * @param request   {@link ServletWebRequest}
     * @throws ValidateCodeException 验证码异常
     */
    void validate(ServletWebRequest request) throws ValidateCodeException;

    /**
     * 默认的验证码校验方式, 只适合通过 request 传递一个参数 与 session 中的验证码且通过 {@link String#equals(Object)} 方法判断的方式.<br><br>
     * 不抛出异常就表示校验通过.<br>
     * 注意: 不要覆盖此方法. 想修改自定义校验逻辑, 实现 {@link #validate(ServletWebRequest)} 即可.
     * @param request                       servletWebRequest
     * @param requestParamValidateCodeName  验证码的请求参数名称
     * @param validateCodeClass             验证码 class
     * @param validateCodeCacheType         验证码缓存类型
     * @param redisConnectionFactory        缓存类型不为 redis 时可以为 null
     * @throws ValidateCodeException    ValidateCodeException
     */
    default void defaultValidate(ServletWebRequest request, String requestParamValidateCodeName,
                                 Class<? extends ValidateCode> validateCodeClass,
                                 ValidateCodeCacheType validateCodeCacheType,
                                 RedisConnectionFactory redisConnectionFactory) throws ValidateCodeException {
        // 获取 session 中的验证码
        HttpServletRequest req = request.getRequest();
        ValidateCodeType validateCodeType = getValidateCodeType();

        ValidateCode codeInCache = validateCodeCacheType.getCodeInCache(request, validateCodeType,
                                                                          validateCodeClass, redisConnectionFactory);
        // 获取 request 中的验证码
        String codeInRequest = request.getParameter(requestParamValidateCodeName);

        // 检查 session 是否有值
        if (codeInCache == null)
        {
            throw new ValidateCodeException(VALIDATE_CODE_NOT_EXISTS_IN_CACHE, IpUtil.getRealIp(req), codeInRequest);
        }

        // 校验参数是否有效
        if (!StringUtils.hasText(codeInRequest))
        {
            // 按照逻辑是前端过滤无效参数, 如果进入此逻辑, 按非正常访问处理
            if (!codeInCache.getReuse())
            {
                validateCodeCacheType.removeCache(request, validateCodeType, redisConnectionFactory);
            }
            throw new ValidateCodeException(VALIDATE_CODE_NOT_EMPTY, IpUtil.getRealIp(req), validateCodeType.name());
        }

        codeInRequest = codeInRequest.trim();

        // 校验是否过期
        if (codeInCache.isExpired())
        {
            validateCodeCacheType.removeCache(request, validateCodeType, redisConnectionFactory);
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, IpUtil.getRealIp(req), codeInRequest);
        }

        // 验证码校验
        if (!codeInRequest.equalsIgnoreCase(codeInCache.getCode()))
        {
            if (!codeInCache.getReuse())
            {
                validateCodeCacheType.removeCache(request, validateCodeType, redisConnectionFactory);
            }
            throw new ValidateCodeException(VALIDATE_CODE_ERROR, IpUtil.getRealIp(req), codeInRequest);
        }

        validateCodeCacheType.removeCache(request, validateCodeType, redisConnectionFactory);

    }
}