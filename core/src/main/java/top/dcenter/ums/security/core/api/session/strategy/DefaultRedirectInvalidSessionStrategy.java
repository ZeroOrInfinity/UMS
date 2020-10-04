package top.dcenter.ums.security.core.api.session.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.determineRedirectUrl;

/**
 * Performs a redirect to a fixed URL when an invalid requested session is detected by the
 * {@code SessionManagementFilter}.<br><br>
 *     继承此类并注入 IOC 容器会替换此类
 * @author Luke Taylor
 * @author zyw
 */
@Slf4j
public final class DefaultRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
	private final String destinationUrl;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private boolean createNewSession = true;
	private final AntPathMatcher matcher;
	private final RequestCache requestCache;

	public DefaultRedirectInvalidSessionStrategy(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl),
				"url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
		this.matcher = new AntPathMatcher();
		this.requestCache = new HttpSessionRequestCache();
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request,
	                                     HttpServletResponse response) throws IOException {

		String redirectUrl = determineRedirectUrl(request, response, destinationUrl, matcher, requestCache);
		if (log.isDebugEnabled())
		{
			log.debug("Starting new session and redirecting to '{}'", redirectUrl);
		}
		redirectStrategy.sendRedirect(request, response, redirectUrl);
	}

	/**
	 * Determines whether a new session should be created before redirecting (to avoid
	 * possible looping issues where the same session ID is sent with the redirected
	 * request). Alternatively, ensure that the configured URL does not pass through the
	 * {@code SessionManagementFilter}.
	 *
	 * @param createNewSession defaults to {@code true}.
	 */
	public void setCreateNewSession(boolean createNewSession) {
		this.createNewSession = createNewSession;
	}
}
