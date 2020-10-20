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

package top.dcenter.ums.security.social.banding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_CODE;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_SCOPE;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_PARAMETER_STATE;
import static top.dcenter.ums.security.core.consts.SecurityConstants.URL_SEPARATOR;

/**
 * 相对于 {@link org.springframework.social.connect.web.ConnectSupport} 修改了回调地址的逻辑，删除 OAuth1 逻辑
 * @see org.springframework.social.connect.web.ConnectSupport
 * @author YongWu zheng
 * @version V1.0  Created by 2020/5/20 14:54
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@Slf4j
public class BandingConnectSupport {

    private boolean useAuthenticateUrl;

    private String applicationUrl;

    /**
     * 不带 providerId
     */
    private String callbackUrl;

    private SessionStrategy sessionStrategy;

    public BandingConnectSupport() {
        this(new HttpSessionSessionStrategy());
    }

    public BandingConnectSupport(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    /**
     * Flag indicating if this instance will support OAuth-based auth instead of the traditional user authorization.
     * Some providers expose a special "authenticateUrl" the user should be redirected to as part of an OAuth-based auth attempt.
     * Setting this flag to true has {@link #buildOAuthUrl(ConnectionFactory, NativeWebRequest) oauthUrl} return this authenticate URL.
     * @param useAuthenticateUrl whether to use the authenticat url or not
     * @see OAuth1Operations#buildAuthenticateUrl(String, OAuth1Parameters)
     * @see OAuth2Operations#buildAuthenticateUrl(OAuth2Parameters)
     */
    public void setUseAuthenticateUrl(boolean useAuthenticateUrl) {
        this.useAuthenticateUrl = useAuthenticateUrl;
    }

    /**
     * Configures the base secure URL for the application this controller is being used in e.g. <code>https://myapp.com</code>. Defaults to null.
     * If specified, will be used to generate OAuth callback URLs.
     * If not specified, OAuth callback URLs are generated from {@link HttpServletRequest HttpServletRequests}.
     * You may wish to set this property if requests into your application flow through a proxy to your application server.
     * In this case, the HttpServletRequest URI may contain a scheme, host, and/or port value that points to an internal server not appropriate for an external callback URL.
     * If you have this problem, you can set this property to the base external URL for your application and it will be used to construct the callback URL instead.
     * @param applicationUrl the application URL value
     */
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    /**
     * Configures a specific callback URL that is to be used instead of calculating one based on the application URL or current request URL.
     * When set this URL will override the default behavior where the callback URL is derived from the current request and/or a specified application URL.
     * When set along with applicationUrl, the applicationUrl will be ignored.
     * @param callbackUrl the callback URL to send to providers during authorization. Default is null.
     */
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    /**
     * Builds the provider URL to redirect the user to for connection authorization.
     * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
     * @param request the current web request
     * @return the URL to redirect the user to for authorization
     * @throws IllegalArgumentException if the connection factory is not OAuth1 based.
     */
    public String buildOAuthUrl(ConnectionFactory<?> connectionFactory, NativeWebRequest request) {
        return buildOAuthUrl(connectionFactory, request, null, null);
    }

    /**
     * Builds the provider URL to redirect the user to for connection authorization.
     * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
     * @param request the current web request
     * @param additionalParameters parameters to add to the authorization URL.
     * @param providerId parameters to add to the callback URL. allowed to null
     * @return the URL to redirect the user to for authorization
     * @throws IllegalArgumentException if the connection factory is not OAuth1 based.
     */
    public String buildOAuthUrl(ConnectionFactory<?> connectionFactory, NativeWebRequest request,
                                MultiValueMap<String, String> additionalParameters, String providerId) {
        if (connectionFactory instanceof OAuth2ConnectionFactory) {
            return buildOAuth2Url((OAuth2ConnectionFactory<?>) connectionFactory, request, additionalParameters, providerId);
        } else {
            throw new IllegalArgumentException("ConnectionFactory not supported");
        }
    }

    /**
     * Complete the connection to the OAuth1 provider.
     * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
     * @param request the current web request
     * @return a new connection to the service provider
     */
    public Connection<?> completeConnection(OAuth1ConnectionFactory<?> connectionFactory, NativeWebRequest request) {
        String verifier = request.getParameter("oauth_verifier");
        AuthorizedRequestToken requestToken = new AuthorizedRequestToken(extractCachedRequestToken(request), verifier);
        OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(requestToken, null);
        return connectionFactory.createConnection(accessToken);
    }

    /**
     * Complete the connection to the OAuth2 provider.
     * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
     * @param request the current web request
     * @param providerId parameters to add to the callback URL. allowed to null
     * @return a new connection to the service provider
     */
    public Connection<?> completeConnection(OAuth2ConnectionFactory<?> connectionFactory, NativeWebRequest request, String providerId) {
        if (connectionFactory.supportsStateParameter()) {
            verifyStateParameter(request);
        }

        String code = request.getParameter(URL_PARAMETER_CODE);
        try {
            AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(request), null);
            return connectionFactory.createConnection(accessGrant);
        } catch (HttpClientErrorException e) {
            log.warn("错误: HttpClientErrorException while completing connection: {}\n      Response body: {}" + e.getMessage(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private void verifyStateParameter(NativeWebRequest request) {
        String state = request.getParameter(URL_PARAMETER_STATE);
        String originalState = extractCachedOAuth2State(request);
        if (state == null || !state.equals(originalState)) {
            throw new IllegalStateException("The OAuth2 'state' parameter is missing or doesn't match.");
        }
    }

    protected String callbackUrl(NativeWebRequest request) {
        if (callbackUrl != null) {
            return callbackUrl;
        }
        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        assert nativeRequest != null;
        if (applicationUrl != null) {
            return applicationUrl + connectPath(nativeRequest);
        } else {
            return nativeRequest.getRequestURL().toString();
        }
    }

    // internal helpers

    private String buildOAuth2Url(OAuth2ConnectionFactory<?> connectionFactory, NativeWebRequest request, MultiValueMap<String, String> additionalParameters, String providerId) {
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        String defaultScope = connectionFactory.getScope();
        OAuth2Parameters parameters = getOAuth2Parameters(request, defaultScope, additionalParameters, providerId);
        // 添加统一的回调地址由 callbackUrl(request) 设置，功能性回调地址由此处通过generateState() 注入 state。
        String state =
                ((BaseOAuth2ConnectionFactory<?>) connectionFactory).generateState("/connect"+ URL_SEPARATOR + providerId);

        parameters.add(URL_PARAMETER_STATE, state);
        sessionStrategy.setAttribute(request, OAUTH2_STATE_ATTRIBUTE, state);
        if (useAuthenticateUrl) {
            return oauthOperations.buildAuthenticateUrl(parameters);
        } else {
            return oauthOperations.buildAuthorizeUrl(parameters);
        }
    }

    private OAuth2Parameters getOAuth2Parameters(NativeWebRequest request, String defaultScope, MultiValueMap<String, String> additionalParameters, String providerId) {
        OAuth2Parameters parameters = new OAuth2Parameters(additionalParameters);
        parameters.putAll(getRequestParameters(request, URL_PARAMETER_SCOPE));
        parameters.setRedirectUri(callbackUrl(request));
        String scope = request.getParameter(URL_PARAMETER_SCOPE);
        if (scope != null) {
            parameters.setScope(scope);
        } else if (defaultScope != null) {
            parameters.setScope(defaultScope);
        }
        return parameters;
    }

    private String connectPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return request.getServletPath() + (pathInfo != null ? pathInfo : "");
    }

    private OAuthToken extractCachedRequestToken(WebRequest request) {
        OAuthToken requestToken = (OAuthToken) sessionStrategy.getAttribute(request, OAUTH_TOKEN_ATTRIBUTE);
        sessionStrategy.removeAttribute(request, OAUTH_TOKEN_ATTRIBUTE);
        return requestToken;
    }

    private String extractCachedOAuth2State(WebRequest request) {
        String state = (String) sessionStrategy.getAttribute(request, OAUTH2_STATE_ATTRIBUTE);
        sessionStrategy.removeAttribute(request, OAUTH2_STATE_ATTRIBUTE);
        return state;
    }

    private MultiValueMap<String, String> getRequestParameters(NativeWebRequest request, String... ignoredParameters) {
        List<String> ignoredParameterList = asList(ignoredParameters);
        MultiValueMap<String, String> convertedMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (!ignoredParameterList.contains(entry.getKey())) {
                convertedMap.put(entry.getKey(), asList(entry.getValue()));
            }
        }
        return convertedMap;
    }

    private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

    private static final String OAUTH2_STATE_ATTRIBUTE = "oauth2State";
}