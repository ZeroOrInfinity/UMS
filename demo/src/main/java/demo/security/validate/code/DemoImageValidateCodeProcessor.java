package demo.security.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeGeneratorHolder;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCode;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageValidateCodeProcessor;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义图片验证码处理器
 * @author zyw
 * @version V1.0  Created by 2020-05-14 23:52
 */
@Component
@Slf4j
public class DemoImageValidateCodeProcessor extends ImageValidateCodeProcessor {

    public DemoImageValidateCodeProcessor(ValidateCodeGeneratorHolder validateCodeGeneratorHolder) {
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
            ImageIO.write(imageCode.getImage(), "JPG", response.getOutputStream());
            log.info("Demo ========>: imageCode = {}", imageCode);
            return true;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}