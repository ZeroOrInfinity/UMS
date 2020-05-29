package top.dcenter.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.validate.code.ValidateCode;
import top.dcenter.security.core.api.validate.code.ValidateCodeGenerator;
import top.dcenter.security.core.validate.code.imagecode.ImageCode;
import top.dcenter.security.core.validate.code.imagecode.ImageValidateCodeProcessor;

import javax.imageio.ImageIO;
import java.util.Map;

/**
 * 自定义图片校验码处理器
 * @author zyw
 * @createrDate 2020-05-14 23:52
 */
@Component
@Slf4j
public class DemoImageValidateCodeProcessor extends ImageValidateCodeProcessor {

    public DemoImageValidateCodeProcessor(Map<String, ValidateCodeGenerator<?>> validateCodeGenerators) {
        super(validateCodeGenerators);
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
            ImageIO.write(imageCode.getImage(), "JPG", request.getResponse().getOutputStream());
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