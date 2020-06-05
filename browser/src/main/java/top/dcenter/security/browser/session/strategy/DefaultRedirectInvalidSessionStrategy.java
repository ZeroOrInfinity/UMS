package top.dcenter.security.browser.session.strategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_ENHANCE_CHECK_KEY;

/**
 * Performs a redirect to a fixed URL when an invalid requested session is detected by the
 * {@code SessionManagementFilter}.
 * @author Luke Taylor
 * @medifiedBy zyw
 */
public final class DefaultRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
	private final Log logger = LogFactory.getLog(getClass());
	private final String destinationUrl;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private boolean createNewSession = true;

	public DefaultRedirectInvalidSessionStrategy(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl),
				"url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request,
	                                     HttpServletResponse response) throws IOException {
		logger.debug("Starting new session (if required) and redirecting to '"
				+ destinationUrl + "'");
		if (createNewSession) {
			request.getSession();
		}
		request.getSession().removeAttribute(SESSION_ENHANCE_CHECK_KEY);
		redirectStrategy.sendRedirect(request, response, destinationUrl);
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
