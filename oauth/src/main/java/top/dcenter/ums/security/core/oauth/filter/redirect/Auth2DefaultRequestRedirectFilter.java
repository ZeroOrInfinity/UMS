/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.dcenter.ums.security.core.oauth.filter.redirect;

import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import top.dcenter.ums.security.core.api.oauth.state.service.Auth2StateCoder;
import top.dcenter.ums.security.core.api.tenant.handler.TenantContextHolder;
import top.dcenter.ums.security.core.exception.Auth2Exception;
import top.dcenter.ums.security.core.api.oauth.justauth.request.Auth2DefaultRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.ums.security.common.utils.JsonUtil.isAjaxOrJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.responseWithJson;
import static top.dcenter.ums.security.common.utils.JsonUtil.toJsonString;
import static top.dcenter.ums.security.common.vo.RedirectVo.redirect;

/**
 * This {@code Filter} initiates the authorization code grant or implicit grant flow by
 * redirecting the End-User's user-agent to the Authorization Server's Authorization
 * Endpoint.
 *
 * <p>
 * It builds the OAuth 2.0 Authorization Request, which is used as the redirect
 * {@code URI} to the Authorization Endpoint. The redirect {@code URI} will include the
 * client identifier, requested scope(s), state, response type, and a redirection URI
 * which the authorization server will send the user-agent back to once access is granted
 * (or denied) by the End-User (Resource Owner).
 *
 * <p>
 * By default, this {@code Filter} responds to authorization requests at the {@code URI}
 * {@code /auth2/authorization/{registrationId}} using the default
 * {@link Auth2DefaultRequestResolver}. The {@code URI} template variable
 * {@code {registrationId}} represents the {@link ClientRegistration#getRegistrationId()
 * registration identifier} of the client that is used for initiating the OAuth 2.0
 * Authorization Request.
 *
 * <p>
 * The default base {@code URI} {@code /auth2/authorization} may be overridden via the
 * constructor
 * {@link #Auth2DefaultRequestRedirectFilter(String, Auth2StateCoder, TenantContextHolder, SimpleUrlAuthenticationFailureHandler)},
 * or alternatively, an {@code Auth2DefaultRequestResolver} may be provided to the
 * constructor
 * {@link #Auth2DefaultRequestRedirectFilter(Auth2DefaultRequestResolver, Auth2StateCoder, TenantContextHolder, SimpleUrlAuthenticationFailureHandler)}}
 * to override the resolving of authorization requests.
 *
 * @author Joe Grandja
 * @author Rob Winch
 * @author YongWu zheng
 * @since 5.0
 * @see OAuth2AuthorizationRequest
 * @see Auth2DefaultRequestResolver
 * @see AuthorizationRequestRepository
 * @see ClientRegistration
 * @see ClientRegistrationRepository
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.1">Section
 * 4.1 Authorization Code Grant</a>
 * @see <a target="_blank" href=
 * "https://tools.ietf.org/html/rfc6749#section-4.1.1">Section 4.1.1 Authorization Request
 * (Authorization Code)</a>
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc6749#section-4.2">Section
 * 4.2 Implicit Grant</a>
 * @see <a target="_blank" href=
 * "https://tools.ietf.org/html/rfc6749#section-4.2.1">Section 4.2.1 Authorization Request
 * (Implicit)</a>
 */
@SuppressWarnings({"unused", "JavaDoc"})
public class Auth2DefaultRequestRedirectFilter extends OncePerRequestFilter {

	private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

	private final RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();

	private final Auth2DefaultRequestResolver authorizationRequestResolver;

	private final Auth2StateCoder auth2StateCoder;

	private final SimpleUrlAuthenticationFailureHandler authenticationFailureHandler;

	private RequestCache requestCache = new HttpSessionRequestCache();

	private final TenantContextHolder tenantContextHolder;

	/**
	 * Constructs an {@code Auth2DefaultRequestRedirectFilter} using the provided
	 * parameters.
	 * @param authorizationRequestBaseUri the base {@code URI} used for authorization
	 * requests
	 * @param auth2StateCoder               state 的编解码器
	 * @param tenantContextHolder           多租户处理器
	 * @param authenticationFailureHandler  失败处理器
	 */
	public Auth2DefaultRequestRedirectFilter(@NonNull String authorizationRequestBaseUri,
	                                         @Nullable Auth2StateCoder auth2StateCoder,
	                                         @Nullable TenantContextHolder tenantContextHolder,
	                                         @NonNull SimpleUrlAuthenticationFailureHandler authenticationFailureHandler) {
		Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
		this.authorizationRequestResolver = new Auth2DefaultRequestResolver(authorizationRequestBaseUri);
		this.auth2StateCoder = auth2StateCoder;
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.tenantContextHolder = tenantContextHolder;
	}

	/**
	 * Constructs an {@code Auth2DefaultRequestRedirectFilter} using the provided
	 * parameters.
	 * @param authorizationRequestResolver the resolver used for resolving authorization
	 * requests
	 * @param auth2StateCoder               state 的编解码器
	 * @param tenantContextHolder           多租户处理器
	 * @param authenticationFailureHandler  失败处理器
	 * @since 5.1
	 */
	public Auth2DefaultRequestRedirectFilter(@NonNull Auth2DefaultRequestResolver authorizationRequestResolver,
	                                         @Nullable Auth2StateCoder auth2StateCoder,
	                                         @Nullable TenantContextHolder tenantContextHolder,
	                                         @NonNull SimpleUrlAuthenticationFailureHandler authenticationFailureHandler) {
		Assert.notNull(authorizationRequestResolver, "authorizationRequestResolver cannot be null");
		this.authorizationRequestResolver = authorizationRequestResolver;
		this.auth2StateCoder = auth2StateCoder;
		this.authenticationFailureHandler = authenticationFailureHandler;
		this.tenantContextHolder = tenantContextHolder;
	}

	/**
	 * Sets the {@link RequestCache} used for storing the current request before
	 * redirecting the OAuth 2.0 Authorization Request.
	 * @param requestCache the cache used for storing the current request
	 */
	public final void setRequestCache(RequestCache requestCache) {
		Assert.notNull(requestCache, "requestCache cannot be null");
		this.requestCache = requestCache;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
	                                @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		try {
			Auth2DefaultRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
			if (authorizationRequest != null) {
				if (this.tenantContextHolder != null) {
					this.tenantContextHolder.tenantIdHandle(request, null);
				}

				this.sendRedirectForAuthorization(request, response, authorizationRequest);
				return;
			}
		}
		catch (Auth2Exception ex) {
			this.authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
			return;
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			this.unsuccessfulRedirectForAuthorization(request, response, ex);
			return;
		}
		try {
			filterChain.doFilter(request, response);
		}
		catch (IOException ex) {
			throw ex;
		}
		catch (Exception ex) {
			// Check to see if we need to handle ClientAuthorizationRequiredException
			Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(ex);
			ClientAuthorizationRequiredException authzEx = (ClientAuthorizationRequiredException) this.throwableAnalyzer
					.getFirstThrowableOfType(ClientAuthorizationRequiredException.class, causeChain);
			if (authzEx != null) {
				try {
					Auth2DefaultRequest authorizationRequest = this.authorizationRequestResolver.resolve(request,
							authzEx.getClientRegistrationId());
					if (authorizationRequest == null) {
						throw authzEx;
					}
					this.sendRedirectForAuthorization(request, response, authorizationRequest);
					this.requestCache.saveRequest(request, response);
				}
				catch (Exception failed) {
					this.unsuccessfulRedirectForAuthorization(request, response, failed);
				}
				return;
			}
			if (ex instanceof ServletException) {
				throw (ServletException) ex;
			}
			//noinspection ConstantConditions
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			throw new RuntimeException(ex);
		}
	}

	private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
			Auth2DefaultRequest authorizationRequest) throws IOException {
		String state = authorizationRequest.generateState();
		if (this.auth2StateCoder != null) {
			// 对 state 进行自定义编码 https://gitee.com/pcore/just-auth-spring-security-starter/issues/I22JC7
			state = this.auth2StateCoder.encode(state, request);
		}
		String authorize = authorizationRequest.authorize(state);

		if (isAjaxOrJson(request)) {
			responseWithJson(response, HttpStatus.OK.value(), toJsonString(redirect(authorize)));
			return;
		}

		this.authorizationRedirectStrategy.sendRedirect(request, response, authorize);
	}

	private void unsuccessfulRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
			Exception ex) throws IOException {
		this.logger.error(LogMessage.format("Authorization Request failed: %s", ex, ex));
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {

		@Override
		protected void initExtractorMap() {
			super.initExtractorMap();
			registerExtractor(ServletException.class, (throwable) -> {
				ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
				return ((ServletException) throwable).getRootCause();
			});
		}

	}

}
