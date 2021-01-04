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
package top.dcenter.ums.security.jwt.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;
import top.dcenter.ums.security.jwt.exception.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.JWT_INVALID;
import static top.dcenter.ums.security.common.enums.ErrorCodeEnum.JWT_RE_AUTH;
import static top.dcenter.ums.security.common.utils.JsonUtil.responseWithJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.toJsonString;
import static top.dcenter.ums.security.common.vo.ResponseResult.fail;
import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;

/**
 * Jwt 统一异常处理器
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.20 18:45
 */
public class JwtExceptionOnceFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (JwtReAuthException e) {
            log.error(e.getMessage(), e);
            responseWithJson(response,
                             HttpStatus.UNAUTHORIZED.value(),
                             toJsonString(fail(JWT_RE_AUTH.getMsg(), JWT_RE_AUTH, getMdcTraceId())));
        }
        catch (JwkSetUriAccessDeniedException e) {
            log.error(e.getMessage(), e);
            responseWithJson(response,
                             HttpStatus.NOT_FOUND.value(),
                             toJsonString(fail(e.getMessage(), e.getErrorCodeEnum(), e.getData())));
        }
        catch (JwtCreateException | SaveRefreshTokenException e) {
            log.error(e.getMessage(), e);
            responseWithJson(response,
                             HttpStatus.INTERNAL_SERVER_ERROR.value(),
                             toJsonString(fail(e.getMessage(), e.getErrorCodeEnum(), e.getData())));
        }
        catch (InvalidBearerTokenException e) {
            log.error(e.getMessage(), e);
            responseWithJson(response,
                             HttpStatus.UNAUTHORIZED.value(),
                             toJsonString(fail(JWT_INVALID.getMsg(), JWT_INVALID, getMdcTraceId())));
        }
        catch (JwtInvalidException | JwtExpiredException | DuplicateRefreshTokenException
                | RefreshTokenInvalidException | RefreshTokenNotFoundException | MismatchRefreshJwtPolicyException e) {
            log.error(e.getMessage(), e);
            responseWithJson(response,
                             HttpStatus.UNAUTHORIZED.value(),
                             toJsonString(fail(e.getMessage(), e.getErrorCodeEnum(), e.getData())));
        }
        catch (NestedServletException e) {
            Throwable cause = e.getCause();
            if (cause instanceof JwtReAuthException) {
                JwtReAuthException exception = ((JwtReAuthException) cause);
                log.error(exception.getMessage(), exception);
                responseWithJson(response,
                                 HttpStatus.UNAUTHORIZED.value(),
                                 toJsonString(fail(exception.getMessage(), exception.getErrorCodeEnum(), exception.getData())));
                return;
            }
            if (cause instanceof BaseUmsJwtException) {
                BaseUmsJwtException exception = ((BaseUmsJwtException) cause);
                log.error(exception.getMessage(), exception);
                responseWithJson(response,
                                 HttpStatus.UNAUTHORIZED.value(),
                                 toJsonString(fail(exception.getMessage(), exception.getErrorCodeEnum(), exception.getData())));
                return;
            }
            if (cause instanceof JwkSetUriAccessDeniedException) {
                JwkSetUriAccessDeniedException exception = ((JwkSetUriAccessDeniedException) cause);
                log.error(exception.getMessage(), exception);
                responseWithJson(response,
                                 HttpStatus.NOT_FOUND.value(),
                                 toJsonString(fail(exception.getMessage(), exception.getErrorCodeEnum(), exception.getData())));
                return;
            }
            if (cause instanceof BaseJwtException) {
                BaseJwtException exception = ((BaseJwtException) cause);
                log.error(exception.getMessage(), exception);
                responseWithJson(response,
                                 HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                 toJsonString(fail(exception.getMessage(), exception.getErrorCodeEnum(), exception.getData())));
                return;
            }
            if (cause instanceof InvalidBearerTokenException) {
                InvalidBearerTokenException exception = ((InvalidBearerTokenException) cause);
                log.error(exception.getMessage(), exception);
                responseWithJson(response,
                                 HttpStatus.UNAUTHORIZED.value(),
                                 toJsonString(fail(JWT_INVALID.getMsg(), JWT_INVALID, getMdcTraceId())));
                return;
            }
            throw e;
        }

    }
}
