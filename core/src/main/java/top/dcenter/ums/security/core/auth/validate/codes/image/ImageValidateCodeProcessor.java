package top.dcenter.ums.security.core.auth.validate.codes.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeType;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 图片验证码处理器实现。如要自定义图片验证码处理器，请继承此类并重写 sent 方法且注入IOC容器即可
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/6 14:47
 */
@Slf4j
public class ImageValidateCodeProcessor extends AbstractValidateCodeProcessor {

    public ImageValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
        super(validateCodeGeneratorHolder);
    }

    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        try
        {
            if (!(validateCode instanceof ImageCode))
            {
                return false;
            }
            ImageCode imageCode = (ImageCode) validateCode;

            HttpServletResponse response = request.getResponse();
            if (response == null)
            {
                return false;
            }
            ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
            return true;
        }
        catch (Exception e)
        {
            HttpServletRequest req = request.getRequest();
            log.error(String.format("发送验证码失败: error=%s, ip=%s, sid=%s, uri=%s, validateCode=%s",
                                    e.getMessage(),
                                    req.getRemoteAddr(),
                                    request.getSessionId(),
                                    req.getRequestURI(),
                                    validateCode.toString()), e);
        }
        return false;
    }

    @Override
    public ValidateCodeType getValidateCodeType() {
        return ValidateCodeType.IMAGE;
    }
}
