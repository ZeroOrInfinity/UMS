/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.dcenter.ums.security.core.api.oauth.signup;

import me.zhyd.oauth.model.AuthUser;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import top.dcenter.ums.security.core.api.oauth.entity.ConnectionData;
import top.dcenter.ums.security.core.api.oauth.repository.exception.UpdateConnectionException;
import top.dcenter.ums.security.core.exception.RegisterUserFailureException;

import java.util.List;

/**
 * A command that signs up a new user in the event no user id could be mapped from a {@link AuthUser}.
 * Allows for implicitly creating a local user profile from connection data during a provider sign-in attempt.
 * note: To replace the implementation logic of the built-in {@code auth_token} and {@code user_connection} tables,
 * implement this interface to inject the IOC container,
 * And set the property ums.repository.enableStartUpInitializeTable = false.
 * @author YongWu zheng
 * @version V2.0  Created by 2020-10-08 20:10
 */
public interface ConnectionService {

	/**
	 * Sign up a new user of the application from the connection.
	 * 如果 {@code authUser.getUsername()} 重名, 则使用 {@code authUser.getUsername() + "_" + authUser.getSource()} 或
	 * {@code authUser.getUsername() + "_" + authUser.getSource() +  "_" + authUser.getUuid()} 即
	 * username_{providerId}_{providerUserId}.
	 * @param authUser      the user info from the provider sign-in attempt
	 * @param providerId    第三方服务商, 如: qq, github
	 * @param encodeState   加密后的 state.   {@code https://gitee.com/pcore/just-auth-spring-security-starter/issues/I22JC7}
	 * @return the new user UserDetails. May be null to indicate that an implicit failed to register local user.
	 * @throws RegisterUserFailureException 用户重名或注册失败
	 */
	@NonNull
	UserDetails signUp(@NonNull AuthUser authUser, @NonNull String providerId, @NonNull String encodeState) throws RegisterUserFailureException;

	/**
	 * 根据传入的参数更新第三方授权登录的用户信息, 包括 accessToken 信息,
	 * @param authUser          {@link AuthUser}
	 * @param connectionData    第三方授权登录的用户信息
	 * @throws UpdateConnectionException    更新异常
	 */
	void updateUserConnectionAndAuthToken(final AuthUser authUser, final ConnectionData connectionData) throws UpdateConnectionException;

	/**
	 * 第三方授权登录信息{@link AuthUser}绑定到本地账号{@link UserDetails}, 且添加第三方授权登录信息到 user_connection 与 auth_token
	 *
	 * @param principal     本地用户数据
	 * @param authUser      第三方用户信息
	 * @param providerId    第三方服务商 Id
	 */
	void binding(@NonNull UserDetails principal, @NonNull AuthUser authUser, @NonNull String providerId);

	/**
	 * 根据 providerId 与 providerUserId 获取 ConnectionData list.
	 * @param providerId        第三方服务商, 如: qq, github
	 * @param providerUserId    第三方用户 Id
	 * @return  connection data list
	 */
	@Nullable
	List<ConnectionData> findConnectionByProviderIdAndProviderUserId(@NonNull String providerId,
	                                                                 @NonNull String providerUserId);
}
