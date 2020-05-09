package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/3 19:52
 */
@Getter
@Setter
@ConfigurationProperties("security.social")
public class SocialProperties {

    private QqProperties qq = new QqProperties();

    /**
     * Social 处理第三方登录的 RedirectUrl 前缀， 默认为 "/auth"
     */
    private String filterProcessesUrl = "/auth";
    /**
     * Social domain, 例如：https://localhost 默认为 "http://127.0.0.1"
     */
    private String domain = "http://127.0.0.1";



    @Getter
    @Setter
    public class QqProperties extends org.springframework.boot.autoconfigure.social.SocialProperties {
        /**
         * 服务提供商标识, 默认为 qq
         */
        private String providerId = "qq";
        /**
         * 回调地址(格式必须是：domain/filterProcessesUrl/providerId)，默认
         */
        private String redirectUrl = domain + "/" + filterProcessesUrl + "/" + providerId;
    }

}
