package top.dcenter.security.core.validate.code;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 校验码封装
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 23:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateCode {
    private String code;
    private LocalDateTime expireTime;

    public ValidateCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
