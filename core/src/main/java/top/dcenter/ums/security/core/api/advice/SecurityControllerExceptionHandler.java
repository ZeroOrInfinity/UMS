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

package top.dcenter.ums.security.core.api.advice;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.*;
import top.dcenter.ums.security.core.vo.ResponseResult;

/**
 * 核心错误处理器,如需自定义，继承此类并注入 IOC 容器即可
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/2 15:35
 */

@ControllerAdvice
public class SecurityControllerExceptionHandler {

    @ExceptionHandler(SmsCodeRepeatedRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseResult smsCodeRepeatedRequestException(SmsCodeRepeatedRequestException ex) {
        return ResponseResult.fail(ex.getMessage(), ex.getErrorCodeEnum(), ex.getData());
    }

    @ExceptionHandler(Auth2Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ResponseResult auth2Exception(Auth2Exception ex) {
        return ResponseResult.fail(ex.getMessage(), ex.getErrorCodeEnum(), ex.getData());
    }

    @ExceptionHandler(RolePermissionsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseResult rolePermissionsException(RolePermissionsException ex) {
        return ResponseResult.fail(ex.getMessage(), ex.getErrorCodeEnum(), ex.getData());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult usernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseResult.fail("用户名或密码错误", ErrorCodeEnum.USERNAME_OR_PASSWORD_ERROR, null);
    }

    @ExceptionHandler(AccountDisabledException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult accountDisabledException(AccountDisabledException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getUid());
    }
    @ExceptionHandler(AccountExpiredException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult accountExpiredException(AccountExpiredException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getUid());
    }
    @ExceptionHandler(AccountLockedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult accountLockedException(AccountLockedException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getUid());
    }
    @ExceptionHandler(CredentialsExpiredException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult credentialsExpiredException(CredentialsExpiredException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getUid());
    }
    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult userNotException(UserNotExistException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getUid());
    }

    @ExceptionHandler(ParameterErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult parameterErrorException(ParameterErrorException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getData());
    }
    
    @ExceptionHandler(ValidateCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult validateCodeException(ValidateCodeException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getData());
    }

    @ExceptionHandler(ValidateCodeParamErrorException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult validateCodeParamErrorException(ValidateCodeParamErrorException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum(), ex.getData());
    }

    @ExceptionHandler(ValidateCodeProcessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult validateCodeProcessException(ValidateCodeProcessException ex) {
        String message = ex.getMessage();
        return ResponseResult.fail(message, ex.getErrorCodeEnum());
    }
    @ExceptionHandler(IllegalAccessUrlException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult illegalAccessUrlException(IllegalAccessUrlException ex) {
        String errorMsg = ex.getMessage();
        return ResponseResult.fail(errorMsg, ex.getErrorCodeEnum(), ex.getData());
    }

    @ExceptionHandler(ExpiredSessionDetectedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult expiredSessionDetectedException(ExpiredSessionDetectedException ex) {
        String errorMsg = ex.getMessage();
        return ResponseResult.fail(errorMsg, ex.getErrorCodeEnum());
    }

}