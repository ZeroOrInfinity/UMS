package top.dcenter.ums.security.core.api.validate.code;

import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator;

import javax.servlet.ServletRequest;

/**
 * 权限认证验证码生成接口。默认实现 {@link ImageCodeGenerator} 与
 * {@link SmsCodeGenerator}<br><br>
 * 在 {@link ValidateCodeType} 中还定义了其他未实现的常见验证码：<br><br>
 *     {@link ValidateCodeType#SELECTION}，<br><br>
 *     {@link ValidateCodeType#SLIDER}，<br><br>
 *     {@link ValidateCodeType#TRACK}，<br><br>
 *     {@link ValidateCodeType#CUSTOMIZE}。<br><br>
 * 自定义生成验证码逻辑时：<br><br>
 *  1. 实现此验证码生成器接口，<br><br>
 *  2. 如果要覆盖已有的验证码逻辑，继承他，再向 IOC 容器注册自己。
 *
 * @author zhailiang
 * @author  zyw
 * @version V1.0
 * Created by 2020/5/4 23:14
 */
public interface ValidateCodeGenerator<T> {
    /**
     * 生成验证码
     * @param request   获取验证码请求
     * @return  返回验证码对象
     */
    T generate(ServletRequest request);

    /**
     * 获取验证码类型
     * @return  ValidateCodeType 的小写字符串
     */
    String getValidateCodeType();

    /**
     * 获取请求中的验证码参数的名称
     * @return  返回验证码参数的名称
     */
    String getRequestParamValidateCodeName();

}
