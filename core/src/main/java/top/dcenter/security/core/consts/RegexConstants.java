package top.dcenter.security.core.consts;

/**
 * 正则表达式常量池
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/6 13:41
 */
public class RegexConstants {

    /**
     * 手机号正则表达式
     */
    public static final String MOBILE_PATTERN = "1\\d{10}";
    /**
     * RFC 6819 安全检查(https://oauth.net/advisories/2014-1-covert-redirect/)时, 使用的正则表达式
     */
    public static final String RFC_6819_CHECK_REGEX = "^(([a-zA-z]+://)?[^/]+)+/.*$";
}
