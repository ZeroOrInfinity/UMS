package top.dcenter.security.core.api.validate.code;

import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.enums.ValidateCodeType;
import top.dcenter.security.core.exception.ValidateCodeException;
import top.dcenter.security.core.auth.validate.codes.ValidateCode;

/**
 * 验证码处理逻辑接口
 * @author zhailiang
 * @author  zyw
 * @version V1.0
 * Created by 2020/5/6 10:00
 */
public interface ValidateCodeProcessor {
    /**
     * 处理验证码逻辑：产生，缓存到session，发送
     * @param request   ServletWebRequest
     * @return  是否成功的状态
     * @throws ValidateCodeException 验证码异常
     */
    boolean produce(ServletWebRequest request) throws ValidateCodeException;

    /**
     * 获取验证码类型
     * @return  {@link ValidateCodeType}
     */
    ValidateCodeType getValidateCodeType();

    /**
     * 产生验证码
     * @param request ServletWebRequest
     * @return  验证码对象
     */
    ValidateCode generate(ServletWebRequest request);

    /**
     * 缓存到session
     * @param request   ServletWebRequest
     * @param validateCode  验证码对象
     * @return  是否成功的状态
     */
    boolean saveSession(ServletWebRequest request, ValidateCode validateCode);

    /**
     * 发送验证码
     * @param request   ServletWebRequest
     * @param validateCode  验证码对象
     * @return  是否成功的状态
     */
    boolean sent(ServletWebRequest request, ValidateCode validateCode);

    /**
     * 校验验证码
     * @param request   {@link ServletWebRequest}
     * @throws ValidateCodeException 验证码异常
     */
    void validate(ServletWebRequest request) throws ValidateCodeException;
}
