package top.dcenter.ums.security.core.auth.validate.codes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码封装
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 23:38
 */
@Getter
@Setter
@ToString
public class ValidateCode implements Serializable {

    private static final long serialVersionUID = 8564646192066649173L;

    private String code;
    private LocalDateTime expireTime;
    private String token;

    public ValidateCode(String code, int expireIn, String token) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.token = token;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
