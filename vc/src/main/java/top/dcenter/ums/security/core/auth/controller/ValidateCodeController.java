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

package top.dcenter.ums.security.core.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.common.utils.ReflectionUtil;
import top.dcenter.ums.security.common.vo.ResponseResult;
import top.dcenter.ums.security.core.advice.ValidateCodeControllerAdviceHandler;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessorHolder;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.auth.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.auth.validate.codes.slider.SliderCode;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.exception.ValidateCodeProcessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.ums.security.common.consts.SecurityConstants.URL_SEPARATOR;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;
import static top.dcenter.ums.security.common.utils.IpUtil.getRealIp;


/**
 * 验证码 控制器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/3 23:41
 */
@Slf4j
@Api(tags = "验证码")
@ResponseBody
public class ValidateCodeController implements InitializingBean {

    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Autowired
    private ValidateCodeProperties validateCodeProperties;

    @Autowired
    private GenericApplicationContext applicationContext;

    /**
     * 获取图片验证码, 根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor} 接口实现
     *
     * @param request  request
     * @param response {@link HttpServletResponse}
     */
    @ApiOperation(value = "根据 type 获取对应的验证码", notes = "根据不同的类型响应的方式不同, image 类型返回 png 图片," +
            "sms 类型返回包含过期时间的 Json 数据, 滑块验证码返回 Json 数据(图片已转换成BASE64编码的字符串)", httpMethod = "GET")
    @RequestMapping(value = "/code/{type}", method = RequestMethod.GET)
    public void createCode(@ApiParam(name = "type", value = "验证码类型, 目前支持: image, sms, slider",
                            allowableValues = "image, sms, track, slider, selection, customize",
                            required = true, example = "image")
                           @PathVariable("type") String type,
                           HttpServletRequest request, HttpServletResponse response) {

        ValidateCodeProcessor validateCodeProcessor;
        if (validateCodeProcessorHolder != null) {
            validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(type);
        }
        else {
            validateCodeProcessor = null;
        }

        if (validateCodeProcessor == null) {
            String ip = getRealIp(request);
            log.warn("创建验证码错误: error={}, ip={}, type={}", ILLEGAL_VALIDATE_CODE_TYPE.getMsg(), ip, type);
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, ip, type);
        }

        boolean validateStatus = validateCodeProcessor.produce(new ServletWebRequest(request, response));

        if (!validateStatus) {
            String ip = getRealIp(request);
            log.warn("发送验证码失败: error={}, ip={}, type={}", ILLEGAL_VALIDATE_CODE_TYPE.getMsg(), ip, type);
            throw new ValidateCodeProcessException(GET_VALIDATE_CODE_FAILURE, ip, type);
        }

    }

    /**
     * 验证方法, 所有验证逻辑都通过 {@link top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter} 处理:<br>
     *     1. 验证不通过, 过滤器直接抛出 {@link top.dcenter.ums.security.core.exception.ValidateCodeException } ,
     *        再通过 {@link ValidateCodeControllerAdviceHandler} 处理返回.
     *     2. 验证通过, 通过此方法返回.
     *
     * @return  ResponseResult
     */
    @ApiOperation(value = "校验滑块验证码", notes = "所有验证逻辑都通过, 返回成功的 Json 详细, ", httpMethod = "POST",
                  response = ResponseResult.class)
    @RequestMapping(value = {"/slider/check"}, method = RequestMethod.POST)
    public ResponseResult sliderCheck(HttpServletRequest request) {
        SliderCode sliderCode = (SliderCode) request.getSession().getAttribute(ValidateCodeType.SLIDER.getKeyPrefix());
        return ResponseResult.success(null, sliderCode.getCode());
    }



    @Override
    public void afterPropertiesSet() throws Exception {

        // 1. 解决循环应用问题
        this.validateCodeProcessorHolder = applicationContext.getBean(ValidateCodeProcessorHolder.class);

        // 2. 动态注入 sliderCheck() RequestMapping 的映射 uri
        String methodName = "sliderCheck";
        ReflectionUtil.setRequestMappingUri(methodName,
                                            validateCodeProperties.getSlider().getSliderCheckUrl(),
                                            this.getClass(),
                                            HttpServletRequest.class);

        // 3. 动态注入 createCode() RequestMapping 的映射 uri
        methodName = "createCode";
        ReflectionUtil.setRequestMappingUri(methodName,
                                     validateCodeProperties.getValidateCodeUrlPrefix() + URL_SEPARATOR + "{type}",
                                     this.getClass(),
                                     String.class, HttpServletRequest.class, HttpServletResponse.class);

        // 4. 在 mvc 中做 Uri 映射等动作
        ReflectionUtil.registerController("validateCodeController", applicationContext, null);


    }
}