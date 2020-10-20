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
package top.dcenter.ums.security.core.oauth.repository;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import top.dcenter.ums.security.core.oauth.justauth.request.Auth2DefaultRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNullElseGet;

/**
 * An implementation of an {@link Auth2DefaultRequestRepository} that stores
 * {@link Auth2DefaultRequest} in the {@code HttpSession}.
 *
 * @author Joe Grandja
 * @author Rob Winch
 * @author YongWu zheng
 * @since 5.0
 * @see Auth2DefaultRequestRepository
 * @see Auth2DefaultRequest
 */
public class HttpSessionAuth2DefaultRequestRepository implements Auth2DefaultRequestRepository<Auth2DefaultRequest> {


    private static final String DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME = HttpSessionAuth2DefaultRequestRepository.class
            .getName() + ".AUTH_DEFAULT_REQUEST";

    private final String sessionAttributeName = DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME;

    @Override
    public Auth2DefaultRequest loadAuthorizationRequest(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        }
        Map<String, Auth2DefaultRequest> authDefaultRequestMap = this.getAuthorizationRequests(request);
        return authDefaultRequestMap.get(stateParameter);
    }

    @Override
    public void saveAuthorizationRequest(Auth2DefaultRequest auth2DefaultRequest, HttpServletRequest request,
                                         HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        if (auth2DefaultRequest == null) {
            this.removeAuthorizationRequest(request, response);
            return;
        }

        String state = auth2DefaultRequest.getAuthStateCache().get(auth2DefaultRequest.getAuthSource().name());
        Assert.hasText(state, "auth2DefaultRequest.state cannot be empty");
        Map<String, Auth2DefaultRequest> authorizationRequests = this.getAuthorizationRequests(request);
        authorizationRequests.put(state, auth2DefaultRequest);
        request.getSession().setAttribute(this.sessionAttributeName, authorizationRequests);
    }

    @Override
    public Auth2DefaultRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        String stateParameter = this.getStateParameter(request);
        if (stateParameter == null) {
            return null;
        }
        Map<String, Auth2DefaultRequest> authorizationRequests = this.getAuthorizationRequests(request);
        Auth2DefaultRequest originalRequest = authorizationRequests.remove(stateParameter);
        if (!authorizationRequests.isEmpty()) {
            request.getSession().setAttribute(this.sessionAttributeName, authorizationRequests);
        }
        else {
            request.getSession().removeAttribute(this.sessionAttributeName);
        }
        return originalRequest;
    }

    /**
     * Gets the state parameter from the {@link HttpServletRequest}
     * @param request the request to use
     * @return the state parameter or null if not found
     */
    private String getStateParameter(HttpServletRequest request) {
        return request.getParameter(OAuth2ParameterNames.STATE);
    }

    /**
     * Gets a non-null and mutable map of {@link Auth2DefaultRequest} to
     * an {@link Auth2DefaultRequest}
     * @param request   HttpServletRequest
     * @return a non-null and mutable map of {@link Auth2DefaultRequest}
     * to an {@link Auth2DefaultRequest}.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Auth2DefaultRequest> getAuthorizationRequests(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Map<String, Auth2DefaultRequest> authDefaultRequestMap = (session != null)
                ? (Map<String, Auth2DefaultRequest>) session.getAttribute(this.sessionAttributeName) : null;
        return requireNonNullElseGet(authDefaultRequestMap, () -> new HashMap<>(0));
    }
}
