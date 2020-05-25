package top.dcenter.security.core.api.validate.code;

import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.validate.code.smscode.DefaultSmsCodeSender;

/**
 * 发送短信验证码接口，更改短息验证码实现此接口。<br>
 *      注意：自定义实现类注册 ioc 容器，会自动覆盖 {@link DefaultSmsCodeSender}
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0
 * Created by 2020/5/5 21:30
 */
public interface SmsCodeSender {

    /**
     * 通过第三方发送短信验证码接口
     * @param mobile     手机号码
     * @param validateCode  校验码
     * @return  发送校验码是否成功的状态
     */
    boolean sendSms(String mobile, String validateCode);

    /**
     * 生成校验码
     * @return
     */
    ValidateCode getCode();
}
