package top.dcenter.ums.security.core.auth.validate.codes.slider;

import top.dcenter.ums.security.core.exception.ValidateCodeException;

/**
 * 滑块验证码工厂
 * @author zyw23
 * @version V1.0
 * Created by 2020/9/21 21:19
 */
public interface SliderCodeFactory {

    /**
     * 获取滑块验证码对象
     *
     * @return  返回滑块验证码对象
     * @throws ValidateCodeException  ValidateCodeException
     */
    SliderCode getSliderCode() throws ValidateCodeException;
}
