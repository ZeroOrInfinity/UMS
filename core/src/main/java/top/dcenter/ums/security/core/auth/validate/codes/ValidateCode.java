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
    /**
     * 是否复用, 如果复用, 不会重新产生验证码, 仍使用验证码失败的验证码
     */
    private Boolean reuse;

    /**
     * 验证码构造器: 默认 <pre>reuse = false;</pre> 不复用
     * @param code      验证码
     * @param expireIn  秒
     */
    public ValidateCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        reuse = false;
    }

    public void setExpireTime(int expireIn) {
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    /**
     * 验证码构造器: 默认不复用
     * @param code      验证码
     * @param expireIn  过期日期
     * @param reuse     是否复用, 如果复用, 不会重新产生验证码, 仍使用验证码失败的验证码, 默认: false 即不复用
     */
    public ValidateCode(String code, int expireIn, Boolean reuse) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.reuse = reuse;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
