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

package demo.security.sign;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.ums.security.core.api.sign.service.SignService;
import top.dcenter.ums.security.core.sign.config.EnableSign;
import top.dcenter.ums.security.core.sign.properties.SignProperties;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static top.dcenter.ums.security.core.util.SignUtil.formatDate;

/**
 * 测试签到功能
 * @author flex_song
 * @author YongWu zheng
 * @version V1.0  Created by 2020/9/14 17:49
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@RestController
@EnableSign
public class DemoSignController {

    private final SignService signService;
    private SignProperties signProperties;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public DemoSignController(SignService signService, SignProperties signProperties) {
        this.signService = signService;
        this.signProperties = signProperties;
    }

    /**
     * 签到测试
     * @return json
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    @RequestMapping("/testSign")
    public String testSign() throws UnsupportedEncodingException {

        LocalDate today = LocalDate.now();

        StringBuilder sb = new StringBuilder();

        {
            // 签到
            boolean signed = signService.doSign("1000", today.minusDays(12));
            if (signed) {
                sb.append("您已签到：").append(formatDate(today.minusDays(12), "yyyy-MM-dd")).append("<br>");
            } else {
                sb.append("签到完成：").append(formatDate(today.minusDays(12), "yyyy-MM-dd")).append("<br>");
            }
        }

        for (int i = 10; i > 3; i--)
        {
            // 签到
            boolean signed = signService.doSign("1000", today.minusDays(i));
            if (signed) {
                sb.append("您已签到：").append(formatDate(today.minusDays(i), "yyyy-MM-dd")).append("<br>");
            } else {
                sb.append("签到完成：").append(formatDate(today.minusDays(i), "yyyy-MM-dd")).append("<br>");
            }
        }

        {
            // 签到
            boolean signed = signService.doSign("1000", today);
            if (signed) {
                sb.append("您已签到：").append(formatDate(today, "yyyy-MM-dd")).append("<br>");
            } else {
                sb.append("签到完成：").append(formatDate(today, "yyyy-MM-dd")).append("<br>");
            }
        }


        // 查询 today 是否签到
        {
            boolean signed = signService.checkSign("1000", today);
            if (signed) {
                sb.append("今天您已签到：").append(formatDate(today, "yyyy-MM-dd")).append("<br>");
            } else {
                sb.append("今天尚未签到：").append(formatDate(today, "yyyy-MM-dd")).append("<br>");
            }
        }

        // 查询 当月 总签到数
        {
            long count = signService.getSignCount("1000", today);
            sb.append("本月签到次数：").append(count).append("<br>");
        }

        // 查询连续签到天数
        {
            long count = signService.getContinuousSignCount("1000", today);
            sb.append("连续签到次数：").append(count).append("<br>");
        }

        // 查询当月首次签到日期
        {
            LocalDate date = signService.getFirstSignDate("1000", today);
            sb.append("本月首次签到：").append(formatDate(date, "yyyy-MM-dd")).append("<br>");
        }

        // 显示当月签到情况
        {
            sb.append("当月签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfo("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }

        // ====== 添加上一个月数据 =========
        today = today.minusMonths(1);
        int lengthOfMonth = today.lengthOfMonth();
        int dayOfMonth = today.getDayOfMonth();
        int len = lengthOfMonth - dayOfMonth;

        for (int i = 0; i < len; i++)
        {
            signService.doSign("1000", today.plusDays(i));
        }


        // getSignCount
        {
            long count = signService.getSignCount("1000", today);
            sb.append("上月签到次数：").append(count).append("<br>");
        }

        // getContinuousSignCount
        {
            long count = signService.getContinuousSignCount("1000", today);
            sb.append("上月连续签到次数：").append(count).append("<br>");
        }

        // getFirstSignDate
        {
            LocalDate date = signService.getFirstSignDate("1000", today);
            sb.append("上月首次签到：").append(formatDate(date, "yyyy-MM-dd")).append("<br>");
        }

        // getSignMap
        {
            sb.append("上月签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfo("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }

        sb.append("本月所有用户总签数：").append("<br>");
        long allSignCount = signService.getTotalSignCount(today);
        sb.append(allSignCount).append("<br>");

        return sb.toString();
    }

    /**
     * 获取最近几天的签到情况, 默认为 7 天<br><br>
     * @param forwardDays   forwardDays
     * @return json
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @RequestMapping("/testSignOfLastSevenDays/{forwardDays}")
    public String testSignOfRange(@PathVariable Integer forwardDays) throws UnsupportedEncodingException {

        LocalDate today = LocalDate.now();

        StringBuilder sb = new StringBuilder();


        // 获取最近几天的签到情况, 默认为 7 天
        {
            sb.append("最近").append(signProperties.getLastFewDays()).append("天签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfoForTheLastFewDays("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }

        // 往前推 day 天后, 最近七天签到情况:
        sb.append("往前推 ").append(forwardDays).append(" 天后, 最近").append(signProperties.getLastFewDays()).append("天签到情况: ").append("<br>");
        today = today.minusDays(forwardDays);

        // 获取最近几天的签到情况, 默认为 7 天
        {
            sb.append("最近七天签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfoForTheLastFewDays("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }

        sb.append("本月所有用户总签数：").append("<br>");
        long allSignCount = signService.getTotalSignCount(today);
        sb.append(allSignCount).append("<br>");

        return sb.toString();
    }

    /**
     * 删除签到 key 测试
     * @return json
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @RequestMapping("/testDelOfCurrentMonth")
    public String testDelOfCurrentMonth() throws UnsupportedEncodingException {

        LocalDate today = LocalDate.now();

        StringBuilder sb = new StringBuilder();


        {
            sb.append("删除用户当月签到 key").append("<br>");
            long amount = signService.delSignByUidAndDate("1000", today);
            sb.append("删除数量: ").append(amount).append("<br>");
        }

        {
            sb.append("删除当月用户签到统计 key").append("<br>");
            List<LocalDate> list = Collections.singletonList(today);
            long amount = signService.delTotalSignByDate(list);
            sb.append("删除数量: ").append(amount).append("<br>");
        }

        sb.append("<br>");

        return sb.toString();
    }
}