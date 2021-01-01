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
package top.dcenter.ums.security.jwt.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.vo.ResponseResult;

import static top.dcenter.ums.security.common.consts.SecurityConstants.CONTROLLER_ADVICE_ORDER_DEFAULT_VALUE;
import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;

/**
 * JWT 异常处理器,如需自定义，继承此类并注入 IOC 容器即可
 * @author YongWu zheng
 * @since 2021.1.1 22:56
 */
@Order(CONTROLLER_ADVICE_ORDER_DEFAULT_VALUE - 3)
@ControllerAdvice
@Slf4j
public class JwtControllerAdvice {

    @ExceptionHandler(OAuth2AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult oAuth2AuthenticationException(OAuth2AuthenticationException ex) {
        String errorMsg = ex.getMessage();
        return ResponseResult.fail(errorMsg, ErrorCodeEnum.UNAUTHORIZED, getMdcTraceId());
    }

}
