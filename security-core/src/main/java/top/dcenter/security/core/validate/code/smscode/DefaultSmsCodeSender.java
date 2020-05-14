package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认短信发送器，无任何实现
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/5 21:36
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {
    @Override
    public boolean sendSms(String mobile, String validateCode) {
        log.info("短信验证码发送成功：{}", validateCode);
        return true;
    }
}
