package top.dcenter.security.core.validate.code.smscode;

import lombok.extern.slf4j.Slf4j;
import top.dcenter.security.core.enums.ValidateStatus;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/5 21:36
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {
    @Override
    public ValidateStatus sendSms(String mobile, String validateCode) {
        log.info("短信验证码发送成功：{}", validateCode);
        return ValidateStatus.SUCCESS;
    }
}
