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

package top.dcenter.ums.security.core.oauth.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;
import top.dcenter.ums.security.core.api.oauth.justauth.request.Auth2DefaultRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * An {@link AbstractAuthenticationToken} for OAuth 2.0 Login, which leverages the OAuth
 * 2.0 Authorization Code Grant Flow.
 *
 * @author YongWu zheng
 * @since 2.0.0
 * @see AbstractAuthenticationToken
 * @see Auth2DefaultRequest
 */
public class Auth2LoginAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	@Getter
	private final Auth2DefaultRequest auth2DefaultRequest;

	@Getter
	private final HttpServletRequest request;

	/**
	 * This constructor should be used when the auth2DefaultRequest callback is
	 * complete.
	 * @param auth2DefaultRequest   the auth2DefaultRequest
	 * @param request               the request
	 */
	public Auth2LoginAuthenticationToken(Auth2DefaultRequest auth2DefaultRequest, HttpServletRequest request) {
		super(Collections.emptyList());
		Assert.notNull(auth2DefaultRequest, "auth2DefaultRequest cannot be null");
		Assert.notNull(request, "request cannot be null");
		this.auth2DefaultRequest = auth2DefaultRequest;
		this.setAuthenticated(false);
		this.request = request;
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

}
