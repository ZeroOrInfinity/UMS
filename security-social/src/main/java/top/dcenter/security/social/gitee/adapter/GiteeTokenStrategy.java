package top.dcenter.security.social.gitee.adapter;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.social.oauth2.OAuth2Version;

/**
 * Strategy enumeration where each value carries an interceptor defining how an access token is carried on API requests.
 * @author Craig Walls
 */
public enum GiteeTokenStrategy {

	/**
	 * Indicates that the access token should be carried in the Authorization header as an OAuth2 Bearer token.
	 */
	AUTHORIZATION_HEADER {
		@Override
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2RequestInterceptor(accessToken, oauth2Version);
		}
	},
	/**
	 * Indicates that the access token should be carried as a query parameter named "access_token".
	 */
	ACCESS_TOKEN_PARAMETER {
		@Override
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken);
		}
	},
	/**
	 * Indicates that the access token should be carried as a query parameter named "oauth_token".
	 */
	OAUTH_TOKEN_PARAMETER {
		@Override
		public ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version) {
			return new OAuth2TokenParameterRequestInterceptor(accessToken, "oauth_token");
		}
	};

	abstract ClientHttpRequestInterceptor interceptor(String accessToken, OAuth2Version oauth2Version);

}
