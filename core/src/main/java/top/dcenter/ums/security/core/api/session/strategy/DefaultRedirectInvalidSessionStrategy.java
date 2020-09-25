package top.dcenter.ums.security.core.api.session.strategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import top.dcenter.ums.security.core.consts.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static top.dcenter.ums.security.core.util.AuthenticationUtil.isPermitUri;
import static top.dcenter.ums.security.core.util.MvcUtil.getUrlPathHelper;

/**
 * Performs a redirect to a fixed URL when an invalid requested session is detected by the
 * {@code SessionManagementFilter}.<br><br>
 *     继承此类并注入 IOC 容器会替换此类
 * @author Luke Taylor
 * @author zyw
 */
public final class DefaultRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
	private final Log logger = LogFactory.getLog(getClass());
	private final String destinationUrl;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private boolean createNewSession = true;
	private final AntPathMatcher matcher;

	public DefaultRedirectInvalidSessionStrategy(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl),
				"url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
		this.matcher = new AntPathMatcher();
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request,
	                                     HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();
		String redirectUrl = destinationUrl;

		// 去除 ServletContextPath 的 uri
		String requestUri = getUrlPathHelper().getPathWithinApplication(request);
		// 是否为 permitAll url
		if (isPermitUri(requestUri, session, matcher))
		{
			// 设置跳转目标 url 为自己, 重新刷新 session
			redirectUrl = requestUri;
		}
		logger.debug("Starting new session (if required) and redirecting to '"
				+ redirectUrl + "'");
		if (createNewSession) {
			request.getSession();
		}

		request.getSession().removeAttribute(SecurityConstants.SESSION_ENHANCE_CHECK_KEY);

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
