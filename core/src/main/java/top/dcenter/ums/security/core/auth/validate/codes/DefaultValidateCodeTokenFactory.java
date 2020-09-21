package top.dcenter.ums.security.core.auth.validate.codes;

import top.dcenter.ums.security.core.api.validate.code.ValidateCodeTokenFactory;

import java.util.UUID;

/**
 * 默认的验证码随机 token 工厂
 * @author zyw
 * @version V1.0  Created by 2020/9/21 12:57
 */
public class DefaultValidateCodeTokenFactory implements ValidateCodeTokenFactory {

    @Override
    public String getToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
