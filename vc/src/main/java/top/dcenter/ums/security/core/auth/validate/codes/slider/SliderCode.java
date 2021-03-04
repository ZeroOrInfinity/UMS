/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.auth.validate.codes.slider;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import top.dcenter.ums.security.core.api.validate.code.ValidateCode;

/**
 * 滑块验证码, 添加 @Transient 注解的是为了在保存 session 时把不必要的图片字段值清除.
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/21 12:33
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@ToString
@Getter
@Setter
public class SliderCode extends ValidateCode {

    private static final long serialVersionUID = 2275969703368971952L;

    /** 标记 @Transient 时, 缓存 ValidateCode 时会自动清除 */
    @ToString.Exclude
    @Transient
    private transient String markImage;
    @ToString.Exclude
    @Transient
    private transient String sourceImage;

    /**
     * token
     */
    private String token;

    /**
     * 是否二次校验. 流程: 第一次用户滑块完成校验一次, 并设置此属性为 true, 第二次再次校验 token
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean secondCheck;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer locationX;

    private Integer locationY;

    private Integer sourceWidth;

    private Integer sourceHeight;

    public SliderCode() {
        this.markImage = null;
        this.sourceImage = null;
        this.secondCheck = false;
    }

    /**
     * 滑块验证码
     * @param code          验证码
     * @param expireIn      秒
     * @param token         token
     * @param markImage      markImage
     * @param sourceImage   sourceImage
     * @param sourceWidth   sourceWidth
     * @param sourceHeight  sourceHeight
     * @param x             x
     * @param locationY             locationY
     */
    public SliderCode(String code, int expireIn, String token,
                      String markImage, String sourceImage, Integer x, Integer locationY,
                      Integer sourceWidth, Integer sourceHeight) {
        super(code, expireIn);
        this.token = token;
        this.markImage = markImage;
        this.sourceImage = sourceImage;
        this.locationX = x;
        this.locationY = locationY;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        secondCheck = false;
    }
}