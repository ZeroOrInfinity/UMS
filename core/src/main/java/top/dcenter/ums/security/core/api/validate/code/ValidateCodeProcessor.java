package top.dcenter.ums.security.core.api.validate.code;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;
import top.dcenter.ums.security.core.exception.ValidateCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_ERROR;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;

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

    /**
     * 默认的验证码校验方式, 只适合通过 request 传递一个参数 与 session 中的验证码且通过 {@link String#equals(Object)} 方法判断的方式.<br><br>
     * 不抛出异常就表示校验通过.<br>
     * 注意: 不要覆盖此方法. 想修改自定义校验逻辑, 实现 {@link #validate(ServletWebRequest)} 即可.
     * @param request                       servletWebRequest
     * @param requestParamValidateCodeName  验证码的请求参数名称
     * @throws ValidateCodeException    ValidateCodeException
     */
    default void defaultValidate(ServletWebRequest request, String requestParamValidateCodeName) throws ValidateCodeException {
        // 获取 session 中的验证码
        HttpServletRequest req = request.getRequest();
        ValidateCodeType validateCodeType = getValidateCodeType();
        String sessionKey = validateCodeType.getSessionKey();
        HttpSession session = req.getSession();
        ValidateCode codeInSession = (ValidateCode) session.getAttribute(sessionKey);
        // 获取 request 中的验证码
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
