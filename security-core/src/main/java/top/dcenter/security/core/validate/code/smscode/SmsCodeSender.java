package top.dcenter.security.core.validate.code.smscode;

/**
 * 短信验证码发生 spi 接口
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
}
