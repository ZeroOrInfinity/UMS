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
 * @author zyw
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
