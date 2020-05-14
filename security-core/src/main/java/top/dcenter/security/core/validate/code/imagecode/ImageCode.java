package top.dcenter.security.core.validate.code.imagecode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.dcenter.security.core.validate.code.ValidateCode;

import java.awt.image.BufferedImage;

/**
 * 图片验证码封装
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 23:38
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageCode extends ValidateCode {
    private BufferedImage image;

    public ImageCode(BufferedImage image, String code, int expireIn) {
        super(code, expireIn);
        this.image = image;
    }

}
