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

package top.dcenter.ums.security.core.auth.validate.codes.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.consts.RegexConstants;
import top.dcenter.ums.security.common.utils.IpUtil;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.api.validate.code.ValidateCode;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeCacheType;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.exception.SmsCodeRepeatedRequestException;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.exception.ValidateCodeParamErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

import static org.springframework.http.HttpStatus.OK;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.MOBILE_FORMAT_ERROR;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.MOBILE_PARAMETER_ERROR;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.SMS_CODE_REPEATED_REQUEST;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_ERROR;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EXISTS_IN_CACHE;
import static top.dcenter.ums.security.common.utils.JsonUtil.responseWithJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.toJsonString;
import static top.dcenter.ums.security.common.vo.ResponseResult.success;
import static top.dcenter.ums.security.core.api.validate.code.sms.SmsCodeSender.SMS_CODE_SEPARATOR;


/**
 * 短信验证码处理器。如要自定义短信验证码处理器，请继承此类并重写 sent 方法且注入 IOC 容器即可
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/6 15:09
 */
@Slf4j
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Autowired
    protected SmsCodeSender smsCodeSender;
    @Autowired
    protected ValidateCodeProperties validateCodeProperties;

    public SmsValidateCodeProcessor(@NonNull ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                    @NonNull ValidateCodeCacheType validateCodeCacheType,
                                    @Nullable RedisConnectionFactory redisConnectionFactory) {
        super(validateCodeGeneratorHolder, validateCodeCacheType, ValidateCode.class, redisConnectionFactory);
    }

    /**
     * @see  AbstractValidateCodeProcessor
     * @param request   ServletWebRequest
     * @param validateCode  验证码对象
     * @return boolean
     */
    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        String mobile = null;
        HttpServletRequest req = request.getRequest();
        String ip = IpUtil.getRealIp(req);
        String sid = request.getSessionId();
        String uri = req.getRequestURI();
        try
        {
            mobile = ServletRequestUtils.getRequiredStringParameter(req, validateCodeProperties.getSms().getRequestParamMobileName());

            if (StringUtils.hasText(mobile) && mobile.matches(RegexConstants.MOBILE_PATTERN))
            {
                ValidateCode codeInCache = super.validateCodeCacheType.getCodeInCache(request,
                                                                                      getValidateCodeType(),
                                                                                      ValidateCode.class,
                                                                                      super.redisConnectionFactory);
                if (null != codeInCache && !codeInCache.isExpired()) {
                    throw new SmsCodeRepeatedRequestException(SMS_CODE_REPEATED_REQUEST, ip, mobile);
                }

                HttpServletResponse response = request.getResponse();
                if (response == null)
                {
                    return false;
                }

                // 去除手机号前缀
                String smsCode = validateCode.getCode();
                int indexOf = smsCode.indexOf(SMS_CODE_SEPARATOR);
                smsCode = smsCode.substring(indexOf + SMS_CODE_SEPARATOR.length());

                final boolean result = smsCodeSender.sendSms(mobile,
                                                             new ValidateCode(smsCode,validateCode.getExpireIn()));
                responseWithJson(response, OK.value(), toJsonString(success("", validateCode.getExpireIn())));

                return result;
            }

        }
        catch (ServletRequestBindingException e)
        {
            String msg = String.format("发送验证码失败-手机号参数错误: error=%s, ip=%s, sid=%s, uri=%s, validateCode=%s",
                                          e.getMessage(), ip, sid, uri, validateCode.toString());
            log.error(msg, e);
            throw new ValidateCodeParamErrorException(MOBILE_PARAMETER_ERROR,
                                                      validateCodeProperties.getSms().getRequestParamMobileName(),
                                                      ip);
        }
        catch (PatternSyntaxException e) {
            String msg = String.format("发送验证码失败-手机号格式不正确: error=%s, ip=%s, sid=%s, uri=%s, validateCode=%s",
                                          e.getMessage(), ip, sid, uri, validateCode.toString());
            log.error(msg, e);
        }
        catch (IOException e) {
            String msg = String.format("发送验证码成功, 响应失败: error=%s, ip=%s, sid=%s, uri=%s, validateCode=%s",
                                       e.getMessage(), ip, sid, uri, validateCode.toString());
            log.error(msg, e);
        }

        throw new ValidateCodeParamErrorException(MOBILE_FORMAT_ERROR, mobile, ip);
    }

    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {
        ValidateCodeType validateCodeType = getValidateCodeType();
        ValidateCodeGenerator<?> validateCodeGenerator = getValidateCodeGenerator(validateCodeType);

        // 获取 session 中的验证码
        HttpServletRequest req = request.getRequest();

        ValidateCode codeInCache = validateCodeCacheType.getCodeInCache(request, validateCodeType,
                                                                        validateCodeClass, redisConnectionFactory);
        // 获取 手机号
        String mobile = request.getParameter(validateCodeProperties.getSms().getRequestParamMobileName());
        // 获取 request 中的验证码, 增加对手机号的校验
        String codeInRequest = mobile + SMS_CODE_SEPARATOR +
                request.getParameter(validateCodeGenerator.getRequestParamValidateCodeName());

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

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.SMS;
    }
}