package top.dcenter.security.core.api.validate.code;

import top.dcenter.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.security.core.auth.validate.codes.sms.DefaultSmsCodeSender;

/**
 * 发送短信验证码接口，更改短息验证码实现此接口。<br><br>
 *      注意：自定义实现类注册 ioc 容器，会自动覆盖 {@link DefaultSmsCodeSender}
 * @author zhailiang
 * @author  zyw
 * @version V1.0
 * Created by 2020/5/5 21:30
 */
public interface SmsCodeSender {

    /**
     * 通过第三方发送短信验证码接口
     * @param mobile     手机号码
     * @param validateCode  验证码
     * @return  发送验证码是否成功的状态
     */
    boolean sendSms(String mobile, String validateCode);

    /**
     * 生成验证码
     * @return {@link ValidateCode}
     */
    ValidateCode getCode();
}
