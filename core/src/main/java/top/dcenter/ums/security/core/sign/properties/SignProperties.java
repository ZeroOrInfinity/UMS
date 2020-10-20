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

package top.dcenter.ums.security.core.sign.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

/**
 * 签到配置属性
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/14 18:58
 */
@Getter
@Setter
@ConfigurationProperties("ums.sign")
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
     * redis key(String) 转 byte[] 转换时所用的 charset, 默认: StandardCharsets.UTF_8
     */
    private String charset = StandardCharsets.UTF_8.name();
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