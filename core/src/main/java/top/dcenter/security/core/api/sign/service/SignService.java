package top.dcenter.security.core.api.sign.service;

import top.dcenter.security.core.properties.SignProperties;
import top.dcenter.security.core.sign.UserSignServiceImpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Map;

/**
 * 基于Redis位图的用户签到功能实现类<br><br>
 * 要自定义签到功能, 实现此接口, 注入 IOC 即可替换 {@link UserSignServiceImpl}<br><br>
 * 实现功能：<br>
 * 1. 用户签到<br>
 * 2. 检查用户是否签到<br>
 * 3. 获取用户当月签到次数<br>
 * 4. 获取用户当月最近连续签到次数<br>
 * 5. 获取用户当月首次签到日期<br>
 * 6. 获取用户当月每天的签到详情<br>
 * 7. 获取用户最近几天的签到情况<br>
 * @author zyw
 * @version V1.0
 * Created by 2020/9/13 21:21
 */
public interface SignService {
    /**
     * 用户签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 之前的签到状态
     * @throws UnsupportedEncodingException
     */
    boolean doSign(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 检查用户是否签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到状态
     * @throws UnsupportedEncodingException
     */
    boolean checkSign(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取用户当月签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到次数
     * @throws UnsupportedEncodingException
     */
    long getSignCount(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取当月连续签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当月连续签到次数
     * @throws UnsupportedEncodingException
     */
    long getContinuousSignCount(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取当月首次签到日期
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 首次签到日期, 如果没有任何签到日期，返回 null
     * @throws UnsupportedEncodingException
     */
    LocalDate getFirstSignDate(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取当月的签到情况
     *
     * @param uid  用户ID
     * @param date 日期
     * @return Key 为签到日期，Value 为签到状态的 Map<"yyyy-MM-dd", boolean>
     * @throws UnsupportedEncodingException
     */
    Map<String, Boolean> getSignInfo(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取最近几天的签到情况, 多少天由 {@link SignProperties#getLastFewDays()} 决定
     *
     * @param uid  用户ID
     * @param date 日期
     * @return Key 为签到日期，Value 为签到状态的 Map<"yyyy-MM-dd", boolean>
     * @throws UnsupportedEncodingException
     */
    Map<String, Boolean> getSignInfoForTheLastFewDays(String uid, LocalDate date) throws UnsupportedEncodingException;

}
