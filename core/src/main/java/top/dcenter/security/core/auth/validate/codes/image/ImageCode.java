package top.dcenter.security.core.auth.validate.codes.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import top.dcenter.security.core.auth.validate.codes.ValidateCode;

import java.awt.image.BufferedImage;

/**
 * 图片验证码封装
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 23:38
 */
@Getter
@Setter
public class ImageCode extends ValidateCode {

    private static final long serialVersionUID = 2978186282822455898L;

    @Transient
    private transient BufferedImage image;

    public ImageCode(BufferedImage image, String code, int expireIn) {
        super(code, expireIn);
        this.image = image;
    }

}
