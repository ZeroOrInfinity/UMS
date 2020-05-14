package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.dcenter.security.core.validate.code.smscode.SmsCodeSender;

/**
 * 自定义发送短信验证码
 * @author zyw
 * @createrDate 2020-05-14 22:26
 */
@Component
@Slf4j
public class DemoSmsCodeSender implements SmsCodeSender {
    @Override
    public boolean sendSms(String mobile, String validateCode) {
        log.info("Demo =====>: 短信验证码发送成功：{}", validateCode);
        return true;
    }
}