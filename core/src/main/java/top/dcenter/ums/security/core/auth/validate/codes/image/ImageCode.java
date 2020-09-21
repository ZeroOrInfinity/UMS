package top.dcenter.ums.security.core.auth.validate.codes.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;

import java.awt.image.BufferedImage;

/**
 * 图片验证码封装, 添加 @Transient 注解的是为了再保存 session 时把不必要的且图片字段清楚
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 23:38
 */
@Getter
@Setter
@ToString
public class ImageCode extends ValidateCode {

    private static final long serialVersionUID = 2978186282822455898L;

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private BufferedImage image;

    /**
     * 图片验证码
     * @param image     tp
     * @param code      验证码字符串
     * @param expireIn  秒
     */
    public ImageCode(BufferedImage image, String code, int expireIn) {
        super(code, expireIn);
        this.image = image;
    }

}
