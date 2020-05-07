package top.dcenter.security.core.validate.code;

import javax.servlet.ServletRequest;

/**
 * 权限认证校验码生成接口。
 * 自定义生成验证码逻辑时：
 *  1. 实现此验证码，
 *  2. 如果要覆盖已有的验证码逻辑，向 IOC 容器注册的 beanName 必须与要覆盖的验证码生成器的 beanName 一样。
 *     要覆盖的验证码生成器的 beanName 可以从 ValidateCodeBeanConfig 查看。
 *
 * @author zyw23
 * @version V1.0
 * Created by 2020/5/4 23:14
 */
public interface ValidateCodeGenerator<T> {
    /**
     * 生成校验码
     * @param request   获取校验码请求
     * @return  返回校验码对象
     */
    T generate(ServletRequest request);

    /**
     * 获取请求中的校验码参数的名称
     * @return  返回校验码参数的名称
     */
    String getRequestParamValidateCodeName();
}
