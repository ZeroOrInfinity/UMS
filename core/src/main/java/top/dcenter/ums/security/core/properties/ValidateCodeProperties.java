package top.dcenter.ums.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.ArrayList;
import java.util.List;

import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_CUSTOMIZE_CODE_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_MOBILE_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_SELECTION_CODE_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_SLIDER_CODE_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_SMS_CODE_NAME;
import static top.dcenter.ums.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_TRACK_CODE_NAME;

/**
 * 验证码属性, 各种验证码会设置成同一个 uri 时, 会有优先级(按顺序从高到低): SMS,CUSTOMIZE,SELECTION,TRACK,SLIDER,IMAGE
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 19:52
 */
@Getter
@ConfigurationProperties("security.codes")
public class ValidateCodeProperties {

    @NestedConfigurationProperty
    private final ImageCodeProperties image = new ImageCodeProperties();
    @NestedConfigurationProperty
    private final SmsCodeProperties sms = new SmsCodeProperties();
    @NestedConfigurationProperty
    private final SliderCodeProperties slider = new SliderCodeProperties();
    @NestedConfigurationProperty
    private final TrackCodeProperties track = new TrackCodeProperties();
    @NestedConfigurationProperty
    private final SelectionCodeProperties selection = new SelectionCodeProperties();
    @NestedConfigurationProperty
    private final CustomizeCodeProperties customize = new CustomizeCodeProperties();

    /**
     * 图片验证码属性
     * @author zhailiang
     * @author  zyw
     * @version V1.0  Created by 2020/5/4 16:04
     */
    @Getter
    @Setter
    public static class SmsCodeProperties {

        public SmsCodeProperties() {
            this.authUrls = new ArrayList<>();
        }

        /**
         * 验证码的验证码长度，默认 6 位
         */
        private Integer length = 6;
        /**
         * 验证码的有效时间，默认 120秒
         */
        private Integer expire = 120;
        /**
         * 提交短信验证码请求时，请求中带的短信验证码变量名，默认 smsCode
         */
        private String requestParamSmsCodeName = DEFAULT_REQUEST_PARAM_SMS_CODE_NAME;
        /**
         * 提交短信验证码请求时，请求中带的手机号变量名，默认 mobile
         */
        private String requestParamMobileName = DEFAULT_REQUEST_PARAM_MOBILE_NAME;

        /**
         * 设置需要短信验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 为空
         */
        private List<String> authUrls;


    }

    /**
     * 图片验证码属性
     * @author zhailiang
     * @author  zyw
     * @version V1.0  Created by 2020/5/4 16:04
     */
    @Getter
    @Setter
    public static class ImageCodeProperties {

        public ImageCodeProperties() {
            List<String> list = new ArrayList<>();
            list.add(DEFAULT_LOGIN_PROCESSING_URL_FORM);
            this.authUrls = list;
        }
        /**
         * 图片验证码的宽度，默认 270； 宽度如果小于 height * 45 / 10, 则 width = height * 45 / 10
         */
        private Integer width = 270;
        /**
         * 图片验证码的高度，默认 60
         */
        private Integer height = 60;
        /**
         * 验证码的验证码长度，默认 4位
         */
        private Integer length = 4;
        /**
         * 验证码的有效时间，默认 300秒
         */
        private Integer expire = 300;


        /**
         * 图片验证码的宽度的字面量，默认 width
         */
        private String requestParaWidthName = "width";
        /**
         * 图片验证码的高度的字面量，默认 height
         */
        private String requestParaHeightName = "height";


        /**
         * 提交图片验证码请求时，请求中带的图片验证码变量名，默认 imageCode
         */
        private String requestParamImageCodeName = DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME;
        /**
         * 设置需要图片验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 /authentication/form
         */
        private List<String> authUrls;

    }

    /**
     * 滑块验证码属性
     * @author  zyw
     * @version V1.0  Created by 2020-09-22 13:28
     */
    @SuppressWarnings("jol")
    @Getter
    @Setter
    public static class SliderCodeProperties {

        public SliderCodeProperties() {
            this.authUrls = new ArrayList<>();
        }

        /**
         * 设置需要验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 空
         */
        private List<String> authUrls;
        /**
         * 设置滑块验证码二次校验证的 uri(此 uri 不需要实现, 在 ValidateCodeFilter 校验), 默认: /slider/check
         */
        private String sliderCheckUrl = "/slider/check";
        /**
         * 提交验证码请求时，请求中带的验证码变量名，默认 sliderCode. <br>
         * 与 tokenRequestParamName, xRequestParamName, yRequestParamName 互斥关系; <br>
         * 当使用此参数时, 把另外三个参数(kv键值对形式, 键值对之间用逗号风隔) 组装到此参数. <br><br>
         * 注意: 默认传递参数是用另外的三个参数实现验证码校验, 如要使用此参数, 请重新实现
         * {@link top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessor#validate(ServletWebRequest)}
         */
        private String requestParamName = DEFAULT_REQUEST_PARAM_SLIDER_CODE_NAME;
        /**
         * request token param name, 默认: sliderToken.<br>
         *     与 requestParamName 互斥关系.
         */
        public String tokenRequestParamName = "sliderToken";

        /**
         * request X param name, 默认: x.<br>
         *     与 requestParamName 互斥关系.
         */
        public String xRequestParamName = "x";

        /**
         * request Y param name, 默认: y.<br>
         *     与 requestParamName 互斥关系.
         */
        public String yRequestParamName = "y";
        /**
         * 滑块验证码默认过期时间, 180 秒
         */
        private Integer expire = 180;
        /**
         * 滑块图宽度, 默认: 50
         */
        private Integer cutWidth = 50;
        /**
         * 滑块图高度, 默认: 50
         */
        private Integer cutHeight = 50;
        /**
         * 抠图凸起圆心, 默认: 5
         */
        private Integer circleR = 5;
        /**
         * 抠图内部矩形填充大小, 默认: 8
         */
        private Integer rectanglePadding = 8;
        /**
         * 抠图的边框宽度, 默认: 1
         */
        private Integer sliderImgOutPadding = 1;

        /**
         * 原始图片目录，默认 static/image/validate/original
         */
        private String originalImageDirectory = "static/image/validate/original";
        /**
         * 根据原始图片生成的滑块图片目录，用于自定义缓存滑块图片的存储目录, 默认 static/image/validate/template
         */
        private String templateImageDirectory = "static/image/validate/template";

    }

    /**
     * 轨迹验证码属性
     * @author  zyw
     * @version V1.0  Created by 2020-09-22 13:28
     */
    @Getter
    @Setter
    public static class TrackCodeProperties {

        public TrackCodeProperties() {
            this.authUrls = new ArrayList<>();
        }

        /**
         * 设置需要验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 空
         */
        private List<String> authUrls;
        /**
         * 提交验证码请求时，请求中带的验证码变量名，默认 trackCode
         */
        private String requestParamName = DEFAULT_REQUEST_PARAM_TRACK_CODE_NAME;

    }

    /**
     * 选择类验证码属性
     * @author  zyw
     * @version V1.0  Created by 2020-09-22 13:28
     */
    @Getter
    @Setter
    public static class SelectionCodeProperties {

        public SelectionCodeProperties() {
            this.authUrls = new ArrayList<>();
        }

        /**
         * 设置需要验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 空
         */
        private List<String> authUrls;
        /**
         * 提交验证码请求时，请求中带的验证码变量名，默认 selectionCode
         */
        private String requestParamName = DEFAULT_REQUEST_PARAM_SELECTION_CODE_NAME;

    }

    /**
     * 自定义验证码属性
     * @author  zyw
     * @version V1.0  Created by 2020-09-22 13:28
     */
    @Getter
    @Setter
    public static class CustomizeCodeProperties {

        public CustomizeCodeProperties() {
            this.authUrls = new ArrayList<>();
        }

        /**
         * 设置需要验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 空
         */
        private List<String> authUrls;
        /**
         * 提交验证码请求时，请求中带的验证码变量名，默认 customizeCode
         */
        private String requestParamName = DEFAULT_REQUEST_PARAM_CUSTOMIZE_CODE_NAME;

    }

}
