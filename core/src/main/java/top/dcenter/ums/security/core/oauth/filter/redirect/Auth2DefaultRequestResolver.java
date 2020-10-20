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

package top.dcenter.ums.security.core.oauth.filter.redirect;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import top.dcenter.ums.security.core.oauth.justauth.Auth2RequestHolder;
import top.dcenter.ums.security.core.oauth.justauth.request.Auth2DefaultRequest;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.STATE;

/**
 * An implementation of an {@link Auth2AuthorizationRequestResolver} that attempts to
 * resolve an {@link Auth2DefaultRequest} from the provided
 * {@code HttpServletRequest} using the default request {@code URI} pattern
 * {@code /auth2/authorization/{registrationId}}.
 *
 * <p>
 * {@link #Auth2DefaultRequestResolver(String)}.
 *
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/5 10:54
 * @see Auth2DefaultRequestResolver
 * @see Auth2DefaultRequestRedirectFilter
 */
public final class Auth2DefaultRequestResolver implements Auth2AuthorizationRequestResolver {

	private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

	private final AntPathRequestMatcher authorizationRequestMatcher;

	/**
	 * Constructs a {@code Auth2DefaultRequestResolver} using the provided
	 * parameters.
	 * @param authorizationRequestBaseUri the base {@code URI} used for resolving
	 * authorization requests
	 */
	public Auth2DefaultRequestResolver(String authorizationRequestBaseUri) {
		Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
		this.authorizationRequestMatcher = new AntPathRequestMatcher(
				authorizationRequestBaseUri + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
	}

	@Override
	public Auth2DefaultRequest resolve(HttpServletRequest request) {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(request.getParameter(STATE)))
		{
			return null;
		}
		String registrationId = this.resolveRegistrationId(request);
		if (registrationId == null) {
			return null;
		}
		return resolve(request, registrationId);
	}

	@Override
	public Auth2DefaultRequest resolve(HttpServletRequest request, String registrationId) {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(request.getParameter(STATE)))
		{
			return null;
		}
		if (registrationId == null) {
			return null;
		}
		return Auth2RequestHolder.getAuth2DefaultRequest(registrationId);
	}

	public String resolveRegistrationId(HttpServletRequest request) {
		if (this.authorizationRequestMatcher.matches(request)) {
			return this.authorizationRequestMatcher.matcher(request).getVariables()
					.get(REGISTRATION_ID_URI_VARIABLE_NAME);
		}
		return null;
	}

}