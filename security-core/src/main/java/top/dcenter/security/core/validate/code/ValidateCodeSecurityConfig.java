package top.dcenter.security.core.validate.code;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

/**
 * 校验码功能配置
 * @author zhailiang
 * @medifiedBy  zyw
 * @createdDate 2020-05-09 00:01
 */
@Component()
public class ValidateCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final ValidateCodeFilter validateCodeFilter;

	public ValidateCodeSecurityConfig(ValidateCodeFilter validateCodeFilter) {
		this.validateCodeFilter = validateCodeFilter;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}
	
}
