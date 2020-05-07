package top.dcenter.security.core.validate.code.imagecode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.enums.ValidateStatus;
import top.dcenter.security.core.validate.code.AbstractValidateCodeProcessor;
import top.dcenter.security.core.validate.code.ValidateCode;

import javax.imageio.ImageIO;

import static top.dcenter.security.core.consts.SecurityConstants.SESSION_KEY_IMAGE;


/**
 * 图片验证码处理器实现
 * @author zyw
 * @version V1.0  Created by 2020/5/6 14:47
 */
@Slf4j
public class ImageValidateCodeProcessor extends AbstractValidateCodeProcessor {

    private final SessionStrategy sessionStrategy;
    private final ImageCodeGenerator imageCodeGenerator;

    public ImageValidateCodeProcessor(ImageCodeGenerator imageCodeGenerator) {
        this.imageCodeGenerator = imageCodeGenerator;
        this.sessionStrategy = new HttpSessionSessionStrategy();
    }

    @Override
    public ValidateStatus sent(ServletWebRequest request, ValidateCode validateCode) {
        try
        {
            ImageCode imageCode = (ImageCode) validateCode;
            sessionStrategy.setAttribute(request, SESSION_KEY_IMAGE, imageCode);
            // TODO 这里可以优化，可以先生成图片，在从图片文件中读取
            ImageIO.write(imageCode.getImage(), "JPEG", request.getResponse().getOutputStream());
            return ValidateStatus.SUCCESS;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        sessionStrategy.removeAttribute(request, SESSION_KEY_IMAGE);
        return ValidateStatus.FAILURE;
    }
}
