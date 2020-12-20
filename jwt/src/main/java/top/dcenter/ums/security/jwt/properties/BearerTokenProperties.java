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
package top.dcenter.ums.security.jwt.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

/**
 * Bearer token 属性
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.5 14:28
 */
@Getter
public class BearerTokenProperties {

    /**
     * request parameter name, 默认为: access_token. 不能为 null 值.<br>
     * 注意:<br>
     * 1. 属性 bearerTokenParameterName 只有在 allowFormEncodedBodyParameter 或 allowUriQueryParameter 其中一方为 {@code true} 时生效.<br>
     */
    @Setter
    private String bearerTokenParameterName = "access_token";

    /**
     * bearer token header name. 默认: Authorization . 不能为 null 值. <br>
     * 注意:<br>
     * 1. 属性 bearerTokenHeaderName 只有在 allowFormEncodedBodyParameter, allowUriQueryParameter 都为 {@code false} 时生效.<br>
     * 2. 如果是跨域的场景, 需要设置 ums.client.cors.accessControlExposeHeaders.<br>
     * 3. 当 {@code usm.jwt.refreshHandlerPolicy=AUTO_RENEW} 时, 不管此字段是否生效, 刷新的 jwt 直接设置到此 header 中, 前端可以从相应的 header 中获取.
     */
    @Setter
    private String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

    /**
     * request parameter name, 默认为: refresh_token . 不能为 null 值. <br>
     * 注意:<br>
     * 1. 属性 refreshTokenParameterName 只有在 allowFormEncodedBodyParameter 或 allowUriQueryParameter 其中一方为 {@code true} 时生效.<br>
     * 2. 目前 allowFormEncodedBodyParameter, allowUriQueryParameter 属性还不能配置, 默认都为 false, refreshTokenParameterName 总是失效.<br>
     */
    @Setter
    private String refreshTokenParameterName = "refresh_token";
    /**
     * bearer token header name. 默认: refresh_token . 不能为 null 值. <br>
     * 注意:<br>
     * 1. 属性 refreshTokenHeaderName 只有在 allowFormEncodedBodyParameter, allowUriQueryParameter 都为 {@code false} 时生效.<br>
     * 2. 如果是跨域的场景, 需要设置 ums.client.cors.accessControlExposeHeaders
     */
    @Setter
    private String refreshTokenHeaderName = "refresh_token";

    /**
     * 是否支持从 uri query parameter 传递参数名称为 {@code bearerTokenParameterName} 的 bearer token, 默认: false <br>
     * 注意: 属性 allowFormEncodedBodyParameter, allowUriQueryParameter 只能是其中一个值为 true, 表示使用
     * bearerTokenParameterName 的值, bearerTokenHeaderName 失效;
     * 两个值为 false 时表示使用 bearerTokenHeaderName 的值, bearerTokenParameterName 失效.
     */
    private final Boolean allowUriQueryParameter = Boolean.FALSE;
    /**
     * 是否支持从 form encoded body parameter 传递参数名称为 {@code bearerTokenParameterName} 的 bearer token
     * 或传递参数名称为{@code refreshTokenParameterName} 的 refresh token, 默认: false <br>
     * 注意: <br>
     * 1. 属性 allowFormEncodedBodyParameter, allowUriQueryParameter 只能是其中一个值为 true, 表示使用
     *    bearerTokenParameterName 的值, bearerTokenHeaderName 失效;<br>
     * 2. 两个值为 false 时表示使用 bearerTokenHeaderName 的值, bearerTokenParameterName 失效.<br>
     * 3. 此属性也控制认证成功后 jwt 与 refresh token 返回的方式, false 表示从 header 中返回, true 表示 json 返回.<br>
     * 4. 当启用通过 request 的 form 来传递 JWT 时会带来很多局限性, 前端只能通过 {@link org.springframework.http.HttpMethod#POST}
     *    来访问需要权限的 API; 一般情况下请保持此默认值, 通过请求头传递.
     */
    @Setter
    private Boolean allowFormEncodedBodyParameter = Boolean.FALSE;


}
