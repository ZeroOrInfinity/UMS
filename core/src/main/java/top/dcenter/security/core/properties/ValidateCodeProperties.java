package top.dcenter.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_MOBILE_NAME;
import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_REQUEST_PARAM_SMS_CODE_NAME;

/**
 * 验证码属性
 * @author zhailiang
 * @medifiedBy  zyw
 * @version V1.0  Created by 2020/5/3 19:52
 */
@ConfigurationProperties("security.codes")
public class ValidateCodeProperties {

    private final ImageCodeProperties image = new ImageCodeProperties();
    private final SmsCodeProperties sms = new SmsCodeProperties();

    public ImageCodeProperties getImage() {
        return image;
    }

    public SmsCodeProperties getSms() {
        return sms;
    }

    /**
     * 图片验证码属性
     * @author zhailiang
     * @medifiedBy  zyw
     * @version V1.0  Created by 2020/5/4 16:04
     */
    public static class SmsCodeProperties {

        public SmsCodeProperties() {
            this.authUrls = new ArrayList<>();
        }

        /**
         * 验证码的验证码长度，默认 6 位
         */
        private int length = 6;
        /**
         * 验证码的有效时间，默认 120秒
         */
        private int expire = 120;
        /**
         * 提交短信验证码请求时，请求中带的短信验证码变量名，默认 smsCode
         */
        private String requestParamSmsCodeName = DEFAULT_REQUEST_PARAM_SMS_CODE_NAME;
        /**
         * 提交短信验证码请求时，请求中带的手机号变量名，默认 mobile
         */
        private String requestParamMobileName = DEFAULT_REQUEST_PARAM_MOBILE_NAME;

        /**
         * 设置需要短信验证码认证的 uri，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 /authentication/form
         */
        private List<String> authUrls;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public String getRequestParamSmsCodeName() {
            return requestParamSmsCodeName;
        }

        public void setRequestParamSmsCodeName(String requestParamSmsCodeName) {
            this.requestParamSmsCodeName = requestParamSmsCodeName;
        }

        public String getRequestParamMobileName() {
            return requestParamMobileName;
        }

        public void setRequestParamMobileName(String requestParamMobileName) {
            this.requestParamMobileName = requestParamMobileName;
        }

        public List<String> getAuthUrls() {
            return authUrls;
        }

        public void setAuthUrls(List<String> authUrls) {
            this.authUrls = authUrls;
        }
    }

    /**
     * 图片验证码属性
     * @author zhailiang
     * @medifiedBy  zyw
     * @version V1.0  Created by 2020/5/4 16:04
     */
    public static class ImageCodeProperties {

        public ImageCodeProperties() {
            List<String> list = new ArrayList<>();
            list.add(DEFAULT_LOGIN_PROCESSING_URL_FORM);
            this.authUrls = list;
        }
        /**
         * 图片验证码的宽度，默认 270； 宽度如果小于 height * 45 / 10, 则 width = height * 45 / 10
         */
        private int width = 270;
        /**
         * 图片验证码的高度，默认 60
         */
        private int height = 60;
        /**
         * 验证码的验证码长度，默认 4位
         */
        private int length = 4;
        /**
         * 验证码的有效时间，默认 300秒
         */
        private int expire = 300;


        /**
         * 图片验证码的宽度的字面量，默认 width
         */
        private String requestParaWidthName = "width";
        /**
         * 图片验证码的高度的字面量，默认 height
         */
        private String requestParaHeightName = "height";


        /**
         * 提交图片验证码请求时，请求中带点图片验证码变量名，默认 imageCode
         */
        private String requestParamImageCodeName = DEFAULT_REQUEST_PARAM_IMAGE_CODE_NAME;
        /**
         * 设置需要图片验证码认证的 uri，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 /authentication/form
         */
        private List<String> authUrls;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public String getRequestParaWidthName() {
            return requestParaWidthName;
        }

        public void setRequestParaWidthName(String requestParaWidthName) {
            this.requestParaWidthName = requestParaWidthName;
        }

        public String getRequestParaHeightName() {
            return requestParaHeightName;
        }

        public void setRequestParaHeightName(String requestParaHeightName) {
            this.requestParaHeightName = requestParaHeightName;
        }

        public String getRequestParamImageCodeName() {
            return requestParamImageCodeName;
        }

        public void setRequestParamImageCodeName(String requestParamImageCodeName) {
            this.requestParamImageCodeName = requestParamImageCodeName;
        }

        public List<String> getAuthUrls() {
            return authUrls;
        }

        public void setAuthUrls(List<String> authUrls) {
            this.authUrls = authUrls;
        }
    }
}
