package top.dcenter.security.core.validate.code.imagecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.security.core.validate.code.ValidateCode;

import javax.imageio.ImageIO;


/**
 * 图片验证码处理器实现
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/6 14:47
 */
@Slf4j
public class ImageValidateCodeProcessor extends AbstractValidateCodeProcessor {

    @Override
    public boolean sent(ServletWebRequest request, ValidateCode validateCode) {
        try
        {
            ImageCode imageCode = (ImageCode) validateCode;
            // TODO 这里可以优化，可以先生成图片，在从图片文件中读取
            ImageIO.write(imageCode.getImage(), "JPEG", request.getResponse().getOutputStream());
            return true;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
