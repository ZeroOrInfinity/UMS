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

package demo.validate.code.slider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.ums.security.core.api.validate.code.enums.ValidateCodeType;
import top.dcenter.ums.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletRequest;


/**
 *  滑块验证码控制器
 * @author YongWu zheng
 * @version V1.0  Created by 2020-09-22 15:00
 */
@Slf4j
@RestController
@RequestMapping("/slider")
public class SliderCoderController {


    /**
     * 验证方法, 所有验证逻辑都通过 {@link top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter} 处理:<br>
     *     1. 验证不通过, 过滤器直接抛出 {@link top.dcenter.ums.security.core.exception.ValidateCodeException } ,
     *        再通过 {@link top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler} 处理返回.
     *     2. 验证通过, 通过此方法返回.
     *
     * @return  ResponseResult
     */
    @RequestMapping(value = "check2",method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult check(HttpServletRequest request) {
        SliderCode sliderCode = (SliderCode) request.getSession().getAttribute(ValidateCodeType.CUSTOMIZE.getSessionKey());
        return ResponseResult.success(null, sliderCode.getCode());
    }

}