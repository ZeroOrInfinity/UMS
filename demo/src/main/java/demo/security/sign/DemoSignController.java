package demo.security.sign;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dcenter.security.core.properties.SignProperties;
import top.dcenter.security.core.api.sign.service.SignService;
import top.dcenter.security.core.sign.config.EnableSign;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import static top.dcenter.security.core.util.SignUtil.formatDate;

/**
 * 测试签到功能
 * @author flex_song zyw
 * @version V1.0  Created by 2020/9/14 17:49
 */
@RestController
@EnableSign
public class DemoSignController {

    private final SignService signService;
    private SignProperties signProperties;
    public DemoSignController(SignService signService, SignProperties signProperties) {
        this.signService = signService;
        this.signProperties = signProperties;
    }

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


        { // 查询 today 是否签到
            boolean signed = signService.checkSign("1000", today);
            if (signed) {
                sb.append("今天您已签到：").append(formatDate(today, "yyyy-MM-dd")).append("<br>");
            } else {
                sb.append("今天尚未签到：").append(formatDate(today, "yyyy-MM-dd")).append("<br>");
            }
        }

        { // 查询 当月 总签到数
            long count = signService.getSignCount("1000", today);
            sb.append("本月签到次数：").append(count).append("<br>");
        }

        { // 查询连续签到天数
            long count = signService.getContinuousSignCount("1000", today);
            sb.append("连续签到次数：").append(count).append("<br>");
        }

        { // 查询当月首次签到日期
            LocalDate date = signService.getFirstSignDate("1000", today);
            sb.append("本月首次签到：").append(formatDate(date, "yyyy-MM-dd")).append("<br>");
        }

        { // 显示当月签到情况
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


        { // getSignCount
            long count = signService.getSignCount("1000", today);
            sb.append("上月签到次数：").append(count).append("<br>");
        }

        { // getContinuousSignCount
            long count = signService.getContinuousSignCount("1000", today);
            sb.append("上月连续签到次数：").append(count).append("<br>");
        }

        { // getFirstSignDate
            LocalDate date = signService.getFirstSignDate("1000", today);
            sb.append("上月首次签到：").append(formatDate(date, "yyyy-MM-dd")).append("<br>");
        }

        { // getSignMap
            sb.append("上月签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfo("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }


        return sb.toString();
    }

    /**
     * 获取最近几天的签到情况, 默认为 7 天, security.sign.lastFewDays=10<br><br>
     * 访问 http://localhost:9090/testSignOfLastSevenDays/10 的结果(访问此链接前先访问http://localhost:9090/testSign, 添加一些签到数据):
     * 最近10天签到情况：
     * 2020-09-06: √
     * 2020-09-07: √
     * 2020-09-08: √
     * 2020-09-09: √
     * 2020-09-10: √
     * 2020-09-11: -
     * 2020-09-12: -
     * 2020-09-13: -
     * 2020-09-14: √
     * 2020-09-15: -
     * 往前推 10 天后, 最近七天签到情况:
     * 最近10天签到情况：
     * 2020-08-27: √
     * 2020-08-28: √
     * 2020-08-29: √
     * 2020-08-30: √
     * 2020-08-31: -
     * 2020-09-01: -
     * 2020-09-02: √
     * 2020-09-03: -
     * 2020-09-04: √
     * 2020-09-05: √
     * @param forwardDays
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/testSignOfLastSevenDays/{forwardDays}")
    public String testSignOfRange(@PathVariable Integer forwardDays) throws UnsupportedEncodingException {

        LocalDate today = LocalDate.now();

        StringBuilder sb = new StringBuilder();


        { // 获取最近几天的签到情况, 默认为 7 天
            sb.append("最近").append(signProperties.getLastFewDays()).append("天签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfoForTheLastFewDays("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }

        // 往前推 day 天后, 最近七天签到情况:
        sb.append("往前推 ").append(forwardDays).append(" 天后, 最近").append(signProperties.getLastFewDays()).append("天签到情况: ").append("<br>");
        today = today.minusDays(forwardDays);

        { // 获取最近几天的签到情况, 默认为 7 天
            sb.append("最近七天签到情况：").append("<br>");
            Map<String, Boolean> signInfo = new TreeMap<>(signService.getSignInfoForTheLastFewDays("1000", today));
            for (Map.Entry<String, Boolean> entry : signInfo.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue() ? "√" : "-").append("<br>");
            }
        }

        return sb.toString();
    }
}
