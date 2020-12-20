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
package top.dcenter.ums.security.jwt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.dcenter.ums.security.common.enums.ErrorCodeEnum;
import top.dcenter.ums.security.common.utils.ReflectionUtil;
import top.dcenter.ums.security.common.vo.ResponseResult;
import top.dcenter.ums.security.core.api.service.UmsUserDetailsService;
import top.dcenter.ums.security.jwt.JwtContext;
import top.dcenter.ums.security.jwt.claims.service.GenerateClaimsSetService;
import top.dcenter.ums.security.jwt.exception.RefreshTokenInvalidException;
import top.dcenter.ums.security.jwt.properties.BearerTokenProperties;
import top.dcenter.ums.security.jwt.properties.JwtProperties;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.isNull;
import static top.dcenter.ums.security.core.mdc.utils.MdcUtil.getMdcTraceId;

/**
 * jwt refresh token controller
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.18 21:03
 */
@Api(tags = "刷新 JWT")
@Controller
public class JwtRefreshTokenController implements InitializingBean, ApplicationContextAware {

    private final GenerateClaimsSetService generateClaimsSetService;
    private final UmsUserDetailsService umsUserDetailsService;
    private final BearerTokenProperties bearerTokenProperties;
    private final String jwtByRefreshTokenUri;
    private final JwtDecoder jwtDecoder;

    private ApplicationContext applicationContext;


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public JwtRefreshTokenController(GenerateClaimsSetService generateClaimsSetService,
                                     UmsUserDetailsService umsUserDetailsService,
                                     JwtDecoder jwtDecoder,
                                     JwtProperties jwtProperties) {
        this.generateClaimsSetService = generateClaimsSetService;
        this.umsUserDetailsService = umsUserDetailsService;
        this.jwtDecoder = jwtDecoder;
        this.bearerTokenProperties = jwtProperties.getBearer();
        this.jwtByRefreshTokenUri = jwtProperties.getJwtByRefreshTokenUri();
    }

    @ApiOperation(value = "根据 refreshToken 刷新 JWT", notes = "refreshToken 的值通过指定的请求头进行传递",
            httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "Authorization",
                    value = "通过属性 ums.jwt.bearer.bearerTokenHeaderName 来确定 headerName, 默认为: Authorization, " +
                            "header 与 form 二选一; 不管 jwt 是否失效, 必须传递此参数",
                    example = "Authorization: Bearer xxx.xxx.xxx"
            ),
            @ApiImplicitParam(paramType = "form", name = "assess_token",
                    value = "通过属性 ums.jwt.bearer.bearerTokenParameterName 来确定 parameterName, 默认为: assess_token, " +
                            "header 与 form 二选一; 不管 jwt 是否失效, 必须传递此参数",
                    example = "assess_token=xxxx.xxxx.xxxx"
            ),
            @ApiImplicitParam(paramType = "header", name = "refresh_token",
                    value = "通过属性 ums.jwt.bearer.refreshTokenHeaderName 来确定 headerName, 默认为: refresh_token; header 与 form 二选一",
                    example = "refresh_token: xxxx.xxxx.xxxx"
            ),
            @ApiImplicitParam(paramType = "form", name = "refresh_token",
                    value = "通过属性 ums.jwt.bearer.refreshTokenParameterName 来确定 parameterName, 默认为: refresh_token; header 与 form 二选一",
                    example = "refresh_token=xxxx.xxxx.xxxx"
            )
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    headers = {@Header(name = "Authorization",
                            description = "通过属性 ums.jwt.bearer.bearerTokenHeaderName 来确定 headerName, 默认为: Authorization; 示例: Authorization: Bearer xxx.xxx.xxx")},
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            examples = {
                                    @ExampleObject(name = "body",
                                            summary = "此 body 实际上没什么意义, 只是表示请求成功, jwt 值在 Authorization 请求头上.",
                                            value = "{\"code\":0,\"msg\":\"刷新 jwt 成功\",\"timestamp\":\"2020-12-17 20:05:32\"}"
                                    ),
                                    @ExampleObject(name = "body",
                                            summary = "表示请求成功, jwt 值在 data 字段上.",
                                            value = "{\"code\":0,\"msg\":\"刷新 jwt 成功\",\"data\":\"xxxx.xxxx.xxxx\",\"timestamp\":\"2020-12-17 20:05:32\"}"
                                    ),
                            }
                    )
            ),
    })
    @RequestMapping(value = "/jwt/refreshToken", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult getJwtByRefreshToken(HttpServletRequest request) {

        String refreshToken = JwtContext.getRefreshTokenOrBearerToken(request,
                                                                      bearerTokenProperties.getRefreshTokenParameterName(),
                                                                      bearerTokenProperties.getRefreshTokenHeaderName());
        if (isNull(refreshToken)) {
            throw new RefreshTokenInvalidException(ErrorCodeEnum.JWT_REFRESH_TOKEN_NOT_FOUND, getMdcTraceId());
        }

        //noinspection unused
        // 获取 jwt, 如果需要从响应头返回则把 jwt 设置到响应头.
        Jwt jwt = JwtContext.generateJwtByRefreshToken(refreshToken, request, jwtDecoder,
                                                       umsUserDetailsService, generateClaimsSetService);

        // 如果需要从 body 返回
        if (bearerTokenProperties.getAllowFormEncodedBodyParameter()) {
            return ResponseResult.success("刷新 jwt 成功", jwt.getTokenValue());
        }

        return ResponseResult.success("刷新 jwt 成功");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1. 动态注入 getJwtByRefreshToken() requestMapping 的映射 uri
        String methodName = "getJwtByRefreshToken";
        ReflectionUtil.setRequestMappingUri(methodName,
                                            jwtByRefreshTokenUri,
                                            this.getClass(),
                                            HttpServletRequest.class);

        // 2. 在 mvc 中做 Uri 映射等动作
        ReflectionUtil.registerController("jwtRefreshTokenController",
                                          (GenericApplicationContext) applicationContext,
                                          JwtRefreshTokenController.class);


    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
