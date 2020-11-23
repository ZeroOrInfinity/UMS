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

package top.dcenter.ums.security.common.consts;

/**
 * 正则表达式常量池
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/6 13:41
 */
public class RegexConstants {
    /**
     * 手机号正则表达式
     */
    public static final String MOBILE_PATTERN = "^[1]([3-9])[0-9]{9}$";
    /**
     * RFC 6819 安全检查(https://oauth.net/advisories/2014-1-covert-redirect/)时, 使用的正则表达式
     */
    public static final String RFC_6819_CHECK_REGEX = "^(([a-zA-z]+://)?[^/]+)+/.*$";

    /**
     * 域名的正则表达式, 针对域名的解析, 例如: www.example.com , www.example.com.cn, www.example.top aaa.bbb.example.cc,
     * 127.0.0.1
     */
    public static final String DOMAIN_REGEX = "^([^\\.]+\\.)+((([^\\.]+\\.)((com\\.))([^\\.]{1,4}$))|((.*(?<!com)\\.)([^\\.]+$)))";
    /**
     * {@link #DOMAIN_REGEX} 的正则表达式中一级域名的组的索引, 对于 ip 形式的不适用. 例如:
     * <pre>
     * String domain = "www.xxx.com.cn";
     * Pattern pattern = Pattern.compile(DOMAIN_REGEX);
     * Matcher matcher = pattern.matcher(domain);
     * if (matcher.find()) {
     *     String topDomain =  matcher.group(TOP_DOMAIN_INDEX);
     * }
     * // 输出:
     * domain: www.example.com -> topDomain: example.com
     * domain: www.example.com.cn -> topDomain: example.com.cn
     * // 注意: 对于 ip 形式的不适用
     * domain:  127.0.0.1 -> topDomain: 0.1
     * </pre>
     */
    public static final int TOP_DOMAIN_INDEX = 2;

    public static final String DIGITAL_REGEX = "\\d";

    /**
     * url scheme regex
     */
    public static final String URL_SCHEME_REGEX = "^([a-zA-z]+://)";
}