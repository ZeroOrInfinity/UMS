package top.dcenter.security.core.api.validate.code;

import top.dcenter.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.security.core.auth.validate.codes.sms.SmsCodeGenerator;
import top.dcenter.security.core.enums.ValidateCodeType;

import javax.servlet.ServletRequest;

/**
 * 权限认证验证码生成接口。默认实现 {@link ImageCodeGenerator} 与
 * {@link SmsCodeGenerator}<br>
 * 在 {@link ValidateCodeType} 中还定义了其他未实现的常见验证码：<br>
 *     {@link ValidateCodeType#SELECTION}，<br>
 *     {@link ValidateCodeType#SLIDER}，<br>
 *     {@link ValidateCodeType#TRACK}，<br>
 *     {@link ValidateCodeType#CUSTOMIZE}。<br>
 * 自定义生成验证码逻辑时：<br>
 *  1. 实现此验证码生成器接口，<br>
 *  2. 如果要覆盖已有的验证码逻辑，继承他，再向 IOC 容器注册自己。
 *
 * @author zhailiang
 * @medifiedBy  zyw
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
