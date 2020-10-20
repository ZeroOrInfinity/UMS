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

package top.dcenter.ums.security.social.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import top.dcenter.ums.security.core.api.config.HttpSecurityAware;
import top.dcenter.ums.security.social.api.callback.BaseOAuth2ConnectionFactory;
import top.dcenter.ums.security.social.callback.SocialOAuth2AuthenticationService;
import top.dcenter.ums.security.social.properties.SocialProperties;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * social 第三方登录核心配置,
 * {@link #postProcess(Object)} 使 OAuth2 支持统一的回调地址, 此方法的作用:  更换 {@link OAuth2AuthenticationService} 为其子类
 * {@link SocialOAuth2AuthenticationService}, 子类主要覆写了方法
 * {@link SocialOAuth2AuthenticationService#buildReturnToUrl(HttpServletRequest)} 使其支持统一的回调地址. <br><br>
 * 如果确实需要自定义，请实现此类的子类，并注册进 IOC 容器。会替换此类.
 * 注意: 覆写方法 {@link #postProcess(Object)} 时一定要调用
 * <code>
 *     super.postProcess(object);
 * </code><br><br>
 *     推荐通过实现 {@link SocialSecurityAutoConfigurerAware} 接口。<br><br>
 *     例如：不需要使用 {@link #postProcess(Object)} 方法配置且通过 {@link WebSecurityConfigurerAdapter#configure(HttpSecurity)}
 *     配置，则请实现 {@link HttpSecurityAware} 接口
 *
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020-05-09 11:37
 */
@SuppressWarnings({"JavadocReference", "AlibabaLowerCamelCaseVariableNaming"})
@Slf4j
public class SocialCoreConfig extends SpringSocialConfigurer {

	private SocialProperties socialProperties;

	public SocialCoreConfig(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);

		// 更换 OAuth2AuthenticationService，添加新的 SocialOAuth2AuthenticationService(覆写 buildReturnToUrl(request)方法)
		// 使其支持统一的回调地址。
		SocialAuthenticationServiceRegistry registry = (SocialAuthenticationServiceRegistry) filter.getAuthServiceLocator();

		// 1. 获取 ConnectionFactories 并另外暂存到 Set
		Set<String> providerIds = registry.registeredProviderIds();
		Set<ConnectionFactory> connectionFactorySet =
				providerIds.stream().map(registry::getConnectionFactory).collect(Collectors.toSet());
		// 2. 用反射或取 registry 中的 ConnectionFactories 并清空 ConnectionFactories 的值
		Map<Class<?>, String> apiTypeIndex;
		try
		{
			Field connectionFactoriesField = registry.getClass().getSuperclass().getDeclaredField("connectionFactories");
			Field apiTypeIndexField = registry.getClass().getSuperclass().getDeclaredField("apiTypeIndex");
			connectionFactoriesField.setAccessible(true);
			apiTypeIndexField.setAccessible(true);
			Map<String, ConnectionFactory<?>> connectionFactories = (Map<String, ConnectionFactory<?>>) connectionFactoriesField.get(registry);
			apiTypeIndex = (Map<Class<?>, String>) apiTypeIndexField.get(registry);
			connectionFactories.clear();
			apiTypeIndex.clear();
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		// 3. 更换 Service 并设置 ConnectionFactory
		for (ConnectionFactory connectionFactory : connectionFactorySet)
		{
			if (connectionFactory instanceof BaseOAuth2ConnectionFactory)
			{
				BaseOAuth2ConnectionFactory baseOAuth2ConnectionFactory = (BaseOAuth2ConnectionFactory) connectionFactory;
				registry.addAuthenticationService(new SocialOAuth2AuthenticationService<>(baseOAuth2ConnectionFactory));
				Class<?> apiType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
				apiTypeIndex.put(apiType, connectionFactory.getProviderId());
			}

		}

		filter.setFilterProcessesUrl(socialProperties.getCallbackUrl());
		filter.setSignupUrl(socialProperties.getSignUpUrl());
		filter.setDefaultFailureUrl(socialProperties.getFailureUrl());
		return (T) filter;
	}

}