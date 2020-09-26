package demo.validate.code.slider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCode;

/**
 * 滑块验证码, 添加 @Transient 注解的是为了在保存 session 时把不必要的图片字段值清除.
 * @author zyw
 * @version V1.0  Created by 2020/9/21 12:33
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@ToString
@Getter
@Setter
public class SliderCode extends ValidateCode {

    private static final long serialVersionUID = 2275969703368971952L;

    @ToString.Exclude
    @Transient
    private String newImage;
    @ToString.Exclude
    @Transient
    private String sourceImage;

    /**
     * token
     */
    private String token;

    /**
     * 是否二次校验. 流程: 第一次用户滑块完成校验一次, 并设置此属性为 true, 第二次再次校验 token
     */
    private Boolean secondCheck;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer locationX;

    private Integer locationY;

    private Integer sourceWidth;

    private Integer sourceHeight;

    /**
     * 滑块验证码
     * @param code          验证码
     * @param expireIn      秒
     * @param token         token
     * @param newImage      newImage
     * @param sourceImage   sourceImage
     * @param sourceWidth   sourceWidth
     * @param sourceHeight  sourceHeight
     * @param x             x
     * @param locationY             locationY
     */
    public SliderCode(String code, int expireIn, String token,
                      String newImage, String sourceImage, Integer x, Integer locationY,
                      Integer sourceWidth, Integer sourceHeight) {
        super(code, expireIn);
        this.token = token;
        this.newImage = newImage;
        this.sourceImage = sourceImage;
        this.locationX = x;
        this.locationY = locationY;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        secondCheck = false;
    }
}
