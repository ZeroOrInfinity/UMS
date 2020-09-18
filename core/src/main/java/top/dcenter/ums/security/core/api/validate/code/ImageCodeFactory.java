package top.dcenter.ums.security.core.api.validate.code;

import top.dcenter.ums.security.core.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.auth.validate.codes.image.DefaultImageCodeFactory;
import top.dcenter.ums.security.core.auth.validate.codes.image.ImageCode;

import javax.servlet.ServletRequest;

/**
 * 图片验证码生成器。如要自定义图片验证码生成器，请实现此类。<br><br>
 *     注意：自定义实现类注册 ioc 容器，会自动覆盖 {@link DefaultImageCodeFactory}，建议实现 {@link ImageCodeFactory}时，使用带有缓存池的工厂
 * @author zyw
 * @version V1.0
 * Created by 2020/5/22 11:14
 */
public interface ImageCodeFactory {
    /**
     * 获取图片验证码. <br><br>
     *     如果 request 中传递了图片的宽与高的值，参数名称必须与 {@link ValidateCodeProperties} 中的 security.codes.image
     *     .request-para-height-name=height 和 security.codes.image.request-para-width-name=width 一致。
     *     如果 request 中没有传递相关参数，则会使用 security.codes.image.height=60 和 security.codes.image.width=270 默认配置。<br><br>
     *     以上参数都可以自定义配置。
     * @param request request
     * @return 图片验证码
     */
    ImageCode getImageCode(ServletRequest request);
}
