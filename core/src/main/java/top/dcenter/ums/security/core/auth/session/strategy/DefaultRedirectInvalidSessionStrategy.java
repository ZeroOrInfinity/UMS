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

package top.dcenter.ums.security.core.auth.session.strategy;

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

import static top.dcenter.ums.security.core.util.AuthenticationUtil.determineInvalidSessionRedirectUrl;

/**
 * Performs a redirect to a fixed URL when an invalid requested session is detected by the
 * {@code SessionManagementFilter}.<br><br>
 *     继承此类并注入 IOC 容器会替换此类
 * @author Luke Taylor
 * @author YongWu zheng
 */
@Slf4j
public final class DefaultRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
	private final String destinationUrl;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
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

		String redirectUrl = determineInvalidSessionRedirectUrl(request, response, destinationUrl, matcher, requestCache);
		if (log.isDebugEnabled())
		{
			log.debug("Starting new session and redirecting to '{}'", redirectUrl);
		}
		redirectStrategy.sendRedirect(request, response, redirectUrl);
	}

}