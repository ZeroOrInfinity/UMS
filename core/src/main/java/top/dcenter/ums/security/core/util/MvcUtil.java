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

package top.dcenter.ums.security.core.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.dcenter.ums.security.common.consts.RegexConstants.DIGITAL_REGEX;
import static top.dcenter.ums.security.common.consts.RegexConstants.DOMAIN_REGEX;
import static top.dcenter.ums.security.common.consts.RegexConstants.TOP_DOMAIN_INDEX;
import static top.dcenter.ums.security.common.consts.RegexConstants.URL_SCHEME_REGEX;

/**
 * 功能: <br>
 * 1. 获取 servletContextPath<br>
 * 2. 获取 本应用的一级域名<br>
 * 3. 从 request 中获取一级域名
 * 4. 检查 redirectUrl 是否是本应用的域名, 防止跳转到外链<br>
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/17 18:32
 */
public final class MvcUtil {

    private MvcUtil() { }

    public static final String TOP_DOMAIN_PARAM_NAME = "topDomain";

    public static final String IP6_SEPARATOR = ":";

    public static final String LOCALHOST = "localhost";

    /**
     * 一级域名(不包括二级域名), 比如: www.example.com -> example.com, www.example.com.cn -> example.com.cn
     * 测试时用的 IP 或 localhost 直接原样设置就行.
     * 在应用启动时通过 {@code SecurityAutoConfiguration} 自动注入.
     */
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static String topDomain = "";

    /**
     * 获取一级域名, 通过属性 {@code ums.client.topDomain} 设置此值.
     * @return  返回一级域名
     */
    public static String getTopDomain() {
        if (StringUtils.hasText(topDomain)) {
            return topDomain;
        }
        throw new RuntimeException("topDomain 未初始化, 可通过属性 ums.client.topDomain 设置此值.");
    }

    /**
     * 从 request 中获取一级域名, ip6 或 ip4 或 localhost 时直接原样返回. 例如:
     * <pre>
     * www.example.com -> example.com,
     * aaa.bbb.example.top -> example.top,
     * www.example.com.cn -> example.com.cn,
     * aaa.bbb.example.com.cn -> example.com.cn,
     * 127.0.0.1 -> 127.0.0.1,
     * ABCD:EF01:2345:6789:ABCD:EF01:2345:6789 -> ABCD:EF01:2345:6789:ABCD:EF01:2345:6789,
     * localhost -> localhost
     * </pre>
     * @param request   request
     * @return  返回一级域名
     */
    public static String getTopDomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        // 排除 ip6
        if (serverName.contains(IP6_SEPARATOR)) {
            return serverName;
        }
        // 排除 localhost
        if (serverName.equalsIgnoreCase(LOCALHOST)) {
            return serverName;
        }
        // 排除 ip4
        int lastIndexOf = serverName.lastIndexOf(".");
        if (Pattern.matches(DIGITAL_REGEX, serverName.substring(lastIndexOf + 1))) {
            return serverName;
        }
        // 提取一级域名并返回
        Pattern pattern = Pattern.compile(DOMAIN_REGEX);
        Matcher matcher = pattern.matcher(serverName);
        if (matcher.find()) {
            return matcher.group(TOP_DOMAIN_INDEX);
        }
        return serverName;
    }

    /**
     * 检查 redirectUrl 是否是本应用的域名, 防止跳转到外链
     * @param redirectUrl   要跳转的目标地址
     * @return  返回 true 时表示是本应用的链接
     */
    public static boolean isSelfTopDomain(String redirectUrl) {
        Pattern pattern = Pattern.compile(URL_SCHEME_REGEX);
        Matcher matcher = pattern.matcher(redirectUrl);
        if (matcher.find()) {
            String uri = matcher.replaceFirst("");
            int indexOf = uri.indexOf("/");
            if (indexOf != -1) {
                uri = uri.substring(0, indexOf);
            }
            return uri.contains(topDomain);
        }
        return true;
    }

}