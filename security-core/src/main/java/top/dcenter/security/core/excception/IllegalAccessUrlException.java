package top.dcenter.security.core.excception;

import org.springframework.security.core.AuthenticationException;

/**
 * 非法访问 URL
 * @author zyw
 * @version V1.0  Created by 2020/5/9 21:05
 */
public class IllegalAccessUrlException extends AuthenticationException {

    public IllegalAccessUrlException(String msg, Throwable t) {
        super(msg, t);
    }

    public IllegalAccessUrlException(String msg) {
        super(msg);
    }
}
