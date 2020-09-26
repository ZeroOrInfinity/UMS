package top.dcenter.ums.security.core.properties;

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
     * 用于 redis 总签到 key 前缀，默认为： total:sign:
     */
    private String totalSignKeyPrefix = "total:sign:";

    /**
     * redis key(String) 转 byte[] 转换时所用的 charset
     */
    private String charset = "UTF-8";
    /**
     * 获取最近几天的签到情况, 不能大于 28 天, 默认为 7 天
     */
    private Integer lastFewDays = 7;

    /**
     * 用户签到 redis key TTL, 默认: 二个月 , 单位: 秒
     */
    private Long userExpired = 2678400L;
    /**
     *  用户签到统计 redis key TTL, 默认: 二个月 , 单位: 秒
     */
    private Long totalExpired = 5356800L;

    public void setLastFewDays(Integer lastFewDays) {
        //noinspection AlibabaUndefineMagicConstant
        if (lastFewDays > 28)
        {
            throw new RuntimeException("获取最近几天的签到天数不能大于 28 天");
        }
        this.lastFewDays = lastFewDays;
    }
}
