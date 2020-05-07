package top.dcenter.security.core.excception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


/**
 * @author zyw
 * @version V1.0  Created by 2020/5/2 15:28
 */

@Getter
public class UserNotExistException extends AuthenticationException {
    private static final long serialVersionUID = 3042211783958201322L;
    private String id;
    public UserNotExistException(String id) {
        super("user id = " + id + " not exist");
        this.id = id;
    }

    public UserNotExistException(String message, Throwable cause, String id) {
        super(message, cause);
        this.id = id;
    }
}
