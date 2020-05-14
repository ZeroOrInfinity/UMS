package top.dcenter.security.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * social 第三方登录配置, 如果需要自定义，请实现此类的子类，并注册进 IOC 容器。
 * @author zhailiang
 * @medifiedBy  zyw
 * @createdDate 2020-05-09 11:37
 */
public class SocialCoreConfigurer extends SpringSocialConfigurer {

	private SocialProperties socialProperties;

	public SocialCoreConfigurer(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(socialProperties.getFilterProcessesUrl());
		filter.setSignupUrl(socialProperties.getSingUpUrl());
		// 要添加失败处理器
		//filter.setPostFailureUrl(socialProperties.getFailureUrl())
		return (T) filter;
	}

}