package top.dcenter.ums.security.core.api.validate.code;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCodeGenerator;
import top.dcenter.ums.security.core.auth.validate.codes.sms.SmsCodeGenerator;
import top.dcenter.ums.security.core.exception.ValidateCodeException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_ERROR;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;

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

    /**
     * 从 request 中获取验证码, 与 session 缓存中的验证码进行校验. <br><br>
     * 不抛出异常就表示校验通过.<br>
     * @param request   servletWebRequest
     * @throws ValidateCodeException    ValidateCodeException
     */
    void validate(ServletWebRequest request) throws ValidateCodeException;

    /**
     * 默认的验证码校验方式, 只适合通过 request 传递一个参数 与 session 中的验证码且通过 {@link String#equals(Object)} 方法判断的方式.<br><br>
     * 不抛出异常就表示校验通过.<br>
     * 注意: 不要覆盖此方法. 想修改自定义校验逻辑, 实现 {@link #validate(ServletWebRequest)} 即可.
     * @param request   servletWebRequest
     * @throws ValidateCodeException    ValidateCodeException
     */
    default void defaultValidate(ServletWebRequest request) throws ValidateCodeException {
        // 获取 session 中的验证码
        HttpServletRequest req = request.getRequest();
        ValidateCodeType validateCodeType = ValidateCodeType.valueOf(getValidateCodeType().toUpperCase());
        String sessionKey = validateCodeType.getSessionKey();
        HttpSession session = req.getSession();
        ValidateCode codeInSession = (ValidateCode) session.getAttribute(sessionKey);
        // 获取 request 中的验证码
        String requestParamValidateCodeName = getRequestParamValidateCodeName();
        String codeInRequest = request.getParameter(requestParamValidateCodeName);

        // 检查 session 是否有值
        if (codeInSession == null)
        {
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, req.getRemoteAddr(), codeInRequest);
        }

        // 校验参数是否有效
        if (!StringUtils.isNotBlank(codeInRequest))
        {
            // 按照逻辑是前端过滤无效参数, 如果进入此逻辑, 按非正常访问处理
            session.removeAttribute(sessionKey);
            throw new ValidateCodeException(VALIDATE_CODE_NOT_EMPTY, req.getRemoteAddr(), validateCodeType.name());
        }

        codeInRequest = codeInRequest.trim();

        // 校验是否过期
        if (codeInSession.isExpired())
        {
            session.removeAttribute(sessionKey);
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, req.getRemoteAddr(), codeInRequest);
        }
        // 验证码校验
        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest))
        {
            if (!codeInSession.getReuse())
            {
                session.removeAttribute(sessionKey);
            }
            throw new ValidateCodeException(VALIDATE_CODE_ERROR, req.getRemoteAddr(), codeInRequest);
        }
        session.removeAttribute(sessionKey);

    }
}
