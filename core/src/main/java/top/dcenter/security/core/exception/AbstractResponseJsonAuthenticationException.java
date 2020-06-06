package top.dcenter.security.core.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import top.dcenter.security.core.enums.ErrorCodeEnum;

/**
 * 继承此类后, 如果异常被 {@link SimpleUrlAuthenticationFailureHandler} 处理 Response 会返回 Json 数据
 * @author zyw
 * @version V1.0  Created by 2020/6/6 12:03
 */
public abstract class AbstractResponseJsonAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = 2661098918363948470L;

    @Getter
    protected ErrorCodeEnum errorCodeEnum;
    @Getter
    protected Object data;
    /**
     * 可以是用户名, userId, sessionId 等表示用户唯一的属性
     */
    @Getter
    protected String uid;

    public AbstractResponseJsonAuthenticationException(ErrorCodeEnum errorCodeEnum, Throwable t, Object data,
                                                       String uid) {
        super(errorCodeEnum.getMsg(), t);
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
        this.uid = uid;
    }

    public AbstractResponseJsonAuthenticationException(ErrorCodeEnum errorCodeEnum, Object data, String uid) {
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
        this.data = data;
        this.uid = uid;
    }
}
