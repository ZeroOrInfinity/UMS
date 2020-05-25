package top.dcenter.security.core.api.validate.code;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.excception.ValidateCodeException;
import top.dcenter.security.core.validate.code.ValidateCode;

/**
 * 校验码处理逻辑接口
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0
 * Created by 2020/5/6 10:00
 */
public interface ValidateCodeProcessor {
    /**
     * 处理校验码逻辑：产生，缓存到session，发送
     * @param request   ServletWebRequest
     * @return  是否成功的状态
     * @throws ValidateCodeException
     */
    boolean produce(ServletWebRequest request) throws ValidateCodeException;

    /**
     * 获取校验码类型
     * @return  ValidateCodeType 的小写字符串
     */
    String getValidateCodeType();

    /**
     * 产生校验码
     * @param request ServletWebRequest
     * @return  校验码对象
     */
    ValidateCode generate(ServletWebRequest request);

    /**
     * 缓存到session
     * @param request   ServletWebRequest
     * @param validateCode  校验码对象
     * @return  是否成功的状态
     */
    boolean saveSession(ServletWebRequest request, ValidateCode validateCode);

    /**
     * 发送校验码
     * @param request   ServletWebRequest
     * @param validateCode  校验码对象
     * @return  是否成功的状态
     */
    boolean sent(ServletWebRequest request, ValidateCode validateCode);

    /**
     * 校验验证码
     * @param request
     * @throws ServletRequestBindingException
     * @throws ValidateCodeException
     */
    void validate(ServletWebRequest request) throws ServletRequestBindingException, ValidateCodeException;
}
