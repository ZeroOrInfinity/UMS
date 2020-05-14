package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_MOBILE_NAME;

/**
 * 短信验证码登录属性
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:51
 */
@Getter
@Setter
@ConfigurationProperties("security.smsCodeLogin")
@ConditionalOnProperty(prefix = "security.smsCodeLogin", name = "sms-code-login-is-open", havingValue = "true")
public class SmsCodeAuthenticationProperties {
    /**
     * 手机验证码登录请求处理url, 默认 /authentication/mobile
     */
    public String loginProcessingUrlMobile = DEFAULT_LOGIN_PROCESSING_URL_MOBILE;

    /**
     * 提交短信验证码请求时，请求中带的手机号变量名，默认 mobile
     */
    private String requestParamMobileName = DEFAULT_REQUEST_PARAM_MOBILE_NAME;

    /**
     * 手机验证码登录是否开启, 默认 false
     */
    public Boolean smsCodeLoginIsOpen = false;



}
