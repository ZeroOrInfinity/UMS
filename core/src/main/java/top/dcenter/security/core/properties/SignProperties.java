package top.dcenter.security.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 签到配置属性
 * @author zyw
 * @version V1.0  Created by 2020/9/14 18:58
 */
@Getter
@Setter
@ConfigurationProperties("security.sign")
public class SignProperties {

    /**
     * 用于 redis 签到 key 前缀，默认为： u:sign:
     */
    private String signKeyPrefix = "u:sign:";

    /**
     * String 与 byte[] 互相转换时所用的 charset
     */
    private String charset = "UTF-8";
    /**
     * 获取最近几天的签到情况, 默认为 7 天
     */
    private Integer lastFewDays = 7;

}
