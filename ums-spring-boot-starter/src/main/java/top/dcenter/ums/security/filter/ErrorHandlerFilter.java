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
package top.dcenter.ums.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.util.NestedServletException;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.exception.BusinessException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.nonNull;
import static top.dcenter.ums.security.common.utils.JsonUtil.responseWithJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.toJsonString;
import static top.dcenter.ums.security.common.vo.ResponseResult.fail;

/**
 * 错误处理器
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.15 0:28
 */
@Slf4j
public class ErrorHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (AuthenticationException | BusinessException e) {
            log.error(e.getMessage(),e);
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            ErrorCodeEnum errorCodeEnum;
            if (e instanceof AuthenticationException) {
                status = HttpStatus.UNAUTHORIZED.value();
                errorCodeEnum = ErrorCodeEnum.UNAUTHORIZED;
            }
            else {
                errorCodeEnum = ((BusinessException) e).getErrorCodeEnum();
            }
            responseWithJson(response, status, toJsonString(fail(errorCodeEnum, e.getMessage())));
        }
        catch (NestedServletException e) {
            String msg = e.getMessage();
            log.error(msg, e);
            Throwable cause = e.getCause();

            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            ErrorCodeEnum errorCode = ErrorCodeEnum.INTERNAL_SERVER_ERROR;
            String errorMsg = msg;
            if (cause instanceof MaxUploadSizeExceededException) {
                MaxUploadSizeExceededException maxUploadSizeExceededException = ((MaxUploadSizeExceededException) cause);
                errorCode = ErrorCodeEnum.MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION;
                String message = maxUploadSizeExceededException.getMessage();
                if (nonNull(message)) {
                    errorMsg = message.substring(message.lastIndexOf(":") + 2);
                }
                else {
                    errorMsg = errorCode.getMsg();
                }

            }
            responseWithJson(response, status, toJsonString(fail(errorCode, errorMsg)));
        }
        catch (ServletException e) {
            log.error(e.getMessage(),e);
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseWithJson(response, status, toJsonString(fail(ErrorCodeEnum.INTERNAL_SERVER_ERROR, e.getMessage())));
        }
    }
}