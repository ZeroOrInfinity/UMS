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

package demo.security.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler;
import top.dcenter.ums.security.core.exception.ExpiredSessionDetectedException;
import top.dcenter.ums.security.core.exception.IllegalAccessUrlException;
import top.dcenter.ums.security.core.exception.ParameterErrorException;
import top.dcenter.ums.security.core.exception.UserNotExistException;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.exception.ValidateCodeParamErrorException;
import top.dcenter.ums.security.core.exception.ValidateCodeProcessException;
import top.dcenter.ums.security.core.vo.ResponseResult;

/**
 * controller 异常处理器
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 15:35
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends SecurityControllerExceptionHandler {

    @Override
    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleUserNotException(UserNotExistException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getUid());
    }

    @Override
    @ExceptionHandler(ParameterErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult parameterErrorException(ParameterErrorException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getData());
    }

    @Override
    @ExceptionHandler(ValidateCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult validateCodeException(ValidateCodeException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum());
    }

    @Override
    @ExceptionHandler(ValidateCodeParamErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult validateCodeParamErrorException(ValidateCodeParamErrorException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getData());
    }

    @Override
    @ExceptionHandler(ValidateCodeProcessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult validateCodeProcessException(ValidateCodeProcessException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum());
    }
    @Override
    @ExceptionHandler(IllegalAccessUrlException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult illegalAccessUrlException(IllegalAccessUrlException ex) {
        String errorMsg = ex.getMessage();
        return ResponseResult.fail(errorMsg, ex.getErrorCodeEnum());
    }

    @Override
    @ExceptionHandler(ExpiredSessionDetectedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult expiredSessionDetectedException(ExpiredSessionDetectedException ex) {
        String errorMsg = ex.getMessage();
        return ResponseResult.fail(errorMsg, ex.getErrorCodeEnum());
    }

}