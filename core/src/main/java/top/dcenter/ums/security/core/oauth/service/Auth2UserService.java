/*
 * Copyright 2002-2020 the original author or authors.
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

package top.dcenter.ums.security.core.oauth.service;

import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import top.dcenter.ums.security.core.oauth.justauth.request.Auth2DefaultRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementations of this interface are responsible for obtaining the user attributes of
 * the End-User (Resource Owner) from the UserInfo Endpoint using the
 * {@link Auth2DefaultRequest#getAccessToken(AuthCallback) Access Token} granted to the
 * {@link Auth2DefaultRequest#getUserInfo(AuthToken)} } and returning an AuthUser.
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/10 7:54
 * @since 2.0.0
 * @see Auth2DefaultRequest
 * @see AuthUser
 */
public interface Auth2UserService {

	/**
	 * Returns an {@link AuthUser} after obtaining the user attributes of the End-User
	 * from the UserInfo Endpoint.
	 * @param auth2Request  the user OAuth2 request
	 * @param request       HttpServletRequest
	 * @return an {@link AuthUser}
	 * @throws OAuth2AuthenticationException if an error occurs while attempting to obtain
	 * the user attributes from the UserInfo Endpoint
	 */
	AuthUser loadUser(Auth2DefaultRequest auth2Request, HttpServletRequest request) throws OAuth2AuthenticationException;

}
