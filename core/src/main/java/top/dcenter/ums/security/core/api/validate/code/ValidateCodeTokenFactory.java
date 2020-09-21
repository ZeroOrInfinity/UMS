package top.dcenter.ums.security.core.api.validate.code;

/**
 * 验证码随机 token 工厂类
 * @author zyw
 * @version V1.0  Created by 2020/9/21 12:53
 */
public interface ValidateCodeTokenFactory {

    /**
     * 获取验证码的随机 token
     * @return      随机 token 字符串
     */
    String getToken();
}
