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

package top.dcenter.ums.security.core.auth.validate.codes.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.utils.IpUtil;
import top.dcenter.ums.security.common.utils.JsonUtil;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.api.validate.code.ValidateCode;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.exception.ValidateCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EXISTS_IN_CACHE;
import static top.dcenter.ums.security.common.utils.UuidUtils.getUUID;

/**
 * 滑块验证码处理器, 自定义处理器请继承此类且注入 IOC 容器即可
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/21 23:05
 */
@Slf4j
public class SliderCoderProcessor extends AbstractValidateCodeProcessor {

    private final ValidateCodeProperties validateCodeProperties;
    /**
     * 验证码处理逻辑的默认实现抽象类.<br><br>
     *  @param validateCodeGeneratorHolder  validateCodeGeneratorHolder
     * @param validateCodeProperties        validateCodeProperties
     * @param redisConnectionFactory        缓存类型不为 redis 时可以为 null
     */
    public SliderCoderProcessor(@NonNull ValidateCodeGeneratorHolder validateCodeGeneratorHolder,
                                @NonNull ValidateCodeProperties validateCodeProperties,
                                @Nullable RedisConnectionFactory redisConnectionFactory) {
        super(validateCodeGeneratorHolder, validateCodeProperties.getValidateCodeCacheType(),
              SliderCode.class, redisConnectionFactory);
        this.validateCodeProperties = validateCodeProperties;
    }

    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        try
        {
            if (!(validateCode instanceof SliderCode))
            {
                return false;
            }
            SliderCode sliderCode = (SliderCode) validateCode;

            HttpServletResponse response = request.getResponse();
            if (response == null)
            {
                return false;
            }

            Integer locationX = sliderCode.getLocationX();
            // 发生到前端数据时 移除 locationX 数据,
            sliderCode.setLocationX(null);
            String resultJson = JsonUtil.toJsonString(sliderCode);
            // 设置回 locationX 数据, 用于后续缓存操作
            sliderCode.setLocationX(locationX);

            JsonUtil.responseWithJson(response, HttpStatus.OK.value(), resultJson);
            if (log.isDebugEnabled())
            {
                log.debug("发送滑块验证码: sliderCode = {}", sliderCode.toString());
            }
            return true;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.SLIDER;
    }


    @SuppressWarnings({"ConstantConditions", "AlibabaLowerCamelCaseVariableNaming"})
    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {
        // 获取 session 中的 SliderCode
        HttpServletRequest req = request.getRequest();
        ValidateCodeType sliderType = ValidateCodeType.SLIDER;
        SliderCode sliderCodeInSession =
                (SliderCode) this.validateCodeCacheType.getCodeInCache(request, sliderType,
                                                                       SliderCode.class, this.redisConnectionFactory);

        // 检查 session 是否有值
        if (sliderCodeInSession == null)
        {
            throw new ValidateCodeException(VALIDATE_CODE_NOT_EXISTS_IN_CACHE, IpUtil.getRealIp(req), request.getSessionId());
        }

        // 检测是否是第二此校验
        if (sliderCodeInSession.getSecondCheck())
        {
            defaultValidate(request, validateCodeProperties.getSlider().getTokenRequestParamName(),
                            SliderCode.class, this.validateCodeCacheType, this.redisConnectionFactory);
            return;
        }

        ValidateCodeProperties.SliderCodeProperties slider = validateCodeProperties.getSlider();
        String tokenRequestParamName = slider.getTokenRequestParamName();
        String xRequestParamName = slider.getXRequestParamName();
        String yRequestParamName = slider.getYRequestParamName();

        // 获取 request 中的验证码
        String token = request.getParameter(tokenRequestParamName);
        String x = request.getParameter(xRequestParamName);
        String y = request.getParameter(yRequestParamName);

        // 校验参数是否有效
        checkParam(sliderType, request, !StringUtils.hasText(token), VALIDATE_CODE_NOT_EMPTY, tokenRequestParamName, sliderCodeInSession);
        checkParam(sliderType, request, !StringUtils.hasText(x), VALIDATE_CODE_NOT_EMPTY, xRequestParamName, sliderCodeInSession);
        checkParam(sliderType, request, !StringUtils.hasText(y), VALIDATE_CODE_NOT_EMPTY, yRequestParamName, sliderCodeInSession);

        token = token.trim();
        Integer locationX = Integer.parseInt(x);
        Integer locationY = Integer.parseInt(y);


        // 校验是否过期
        checkParam(sliderType, request, sliderCodeInSession.isExpired(), VALIDATE_CODE_EXPIRED, token, sliderCodeInSession);

        // 验证码校验
        boolean verify = sliderCodeInSession.getLocationY().equals(locationY)
                         && Math.abs(sliderCodeInSession.getLocationX() - locationX) < 2;
        if (!verify)
        {
            if (!sliderCodeInSession.getReuse())
            {
                this.validateCodeCacheType.removeCache(request, sliderType, this.redisConnectionFactory);
            }
            throw new ValidateCodeException(VALIDATE_CODE_FAILURE, IpUtil.getRealIp(req), token);
        }

        // 更新 session 中的验证码信息, 以便于第二次校验
        sliderCodeInSession.setSecondCheck(true);
        // 方便二次校验时, 调用 ValidateCodeGenerator.defaultValidate 方法.
        sliderCodeInSession.setCode(getUUID());
        // 这里第一次校验通过, 第二次校验不需要使用复用功能, 不然第二次校验时不会清除 session 中的验证码缓存
        sliderCodeInSession.setReuse(false);

        this.validateCodeCacheType.save(request, sliderCodeInSession, sliderType, this.redisConnectionFactory);

    }

    /**
     * 根据 condition 是否删除 缓存中 指定的 Key 的缓存, 并抛出异常
     * @param sliderType        滑块验证码类型
     * @param request           {@link ServletWebRequest}
     * @param condition         参数是否有效的条件
     * @param errorCodeEnum     errorCodeEnum
     * @param errorData         返回前端的错误数据
     * @param sliderCode        滑块验证码
     * @throws ValidateCodeException ValidateCodeException
     */
    private void checkParam(ValidateCodeType sliderType, ServletWebRequest request, boolean condition,
                            ErrorCodeEnum errorCodeEnum, String errorData, SliderCode sliderCode) throws ValidateCodeException {
        if (condition)
        {
            if (sliderCode.getReuse()) {
                // 按照逻辑是前端过滤无效参数, 如果进入此逻辑, 按非正常访问处理
                this.validateCodeCacheType.removeCache(request, sliderType, this.redisConnectionFactory);
            }
            throw new ValidateCodeException(errorCodeEnum, IpUtil.getRealIp(request.getRequest()), errorData);
        }
    }
}