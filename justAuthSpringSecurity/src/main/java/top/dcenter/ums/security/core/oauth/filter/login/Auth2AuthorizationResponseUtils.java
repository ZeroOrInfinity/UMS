package top.dcenter.ums.security.core.oauth.filter.login;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Utility methods for an OAuth 2.0 Authorization Response.
 *
 * @author Joe Grandja
 * @since 5.1
 * @see OAuth2AuthorizationResponse
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
final class Auth2AuthorizationResponseUtils {

	private Auth2AuthorizationResponseUtils() {
	}

	static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());
		map.forEach((key, values) -> {
			if (values.length > 0) {
				for (String value : values) {
					params.add(key, value);
				}
			}
		});
		return params;
	}

	static boolean isAuthorizationResponse(MultiValueMap<String, String> request) {
		return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
	}

	static boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {
		return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.CODE))
				&& StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
	}

	static boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {
		return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.ERROR))
				&& StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
	}

	@SuppressWarnings("unused")
	static OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {
		String code = request.getFirst(OAuth2ParameterNames.CODE);
		String errorCode = request.getFirst(OAuth2ParameterNames.ERROR);
		String state = request.getFirst(OAuth2ParameterNames.STATE);
		if (StringUtils.hasText(code)) {
			return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build();
		}
		String errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION);
		String errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI);
		// @formatter:off
		return OAuth2AuthorizationResponse.error(errorCode)
				.redirectUri(redirectUri)
				.errorDescription(errorDescription)
				.errorUri(errorUri)
				.state(state)
				.build();
		// @formatter:on
	}

}