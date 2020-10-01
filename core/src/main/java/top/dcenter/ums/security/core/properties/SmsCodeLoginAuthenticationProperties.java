package top.dcenter.ums.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE;

/**
 * 短信验证码登录属性
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 19:51
 */
@Getter
@Setter
@ConfigurationProperties("ums.mobile.login")
public class SmsCodeLoginAuthenticationProperties {
    /**
     * 手机验证码登录请求处理url, 默认 /authentication/mobile
     */
    public String loginProcessingUrlMobile = DEFAULT_LOGIN_PROCESSING_URL_MOBILE;

    /**
     * 手机验证码登录是否开启, 默认 false
     */
    public Boolean smsCodeLoginIsOpen = false;



}
