package top.dcenter.ums.security.core.api.sign.service;

import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.sign.UserSignServiceImpl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
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
 * 7. 获取用户最近几天的签到情况, 多少天由 SignProperties#getLastFewDays() 决定.<br>
 * 8. 获取用户最近几天的签到情况<br>
 * 9. 删除指定用户与指定日期的数据<br>
 * 10. 删除指定月份的用户签到统计数据<br>
 * @author zyw
 * @version V1.0
 * Created by 2020/9/13 21:21
 */
public interface SignService {

    /**
     * 删除指定用户与指定日期的数据
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long delSignByUidAndDate(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException;

    /**
     * 删除指定用户与指定日期的数据
     *
     * @param keyMap k = 用户ID, v = LocalDate
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long delSignByUidAndDate(@NonNull Map<String, LocalDate> keyMap) throws UnsupportedEncodingException;

    /**
     * 删除指定月份的用户签到统计数据
     *
     * @param date 日期
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long delTotalSignByDate(@NonNull LocalDate date) throws UnsupportedEncodingException;

    /**
     * 删除指定月份的用户签到统计数据
     *
     * @param dates 日期列表
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long delTotalSignByDate(@NonNull List<LocalDate> dates) throws UnsupportedEncodingException;

    /**
     * 用户签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 之前的签到状态
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    boolean doSign(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 检查用户是否签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到状态
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    boolean checkSign(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取用户当月签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到次数
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long getSignCount(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取所有用户的当月签到总次数
     *
     * @param date 日期
     * @return 当前的签到次数, -1 表示获取数据时发生错误
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long getTotalSignCount(LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取当月连续签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当月连续签到次数
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    long getContinuousSignCount(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取当月首次签到日期
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 首次签到日期, 如果没有任何签到日期，返回 null
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    LocalDate getFirstSignDate(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取当月的签到情况
     *
     * @param uid  用户ID
     * @param date 日期
     * @return Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    Map<String, Boolean> getSignInfo(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取最近几天的签到情况, 多少天由 <pre> SignProperties#getLastFewDays() </pre> 决定
     *
     * @param uid           用户ID
     * @param date          日期
     * @return Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    Map<String, Boolean> getSignInfoForTheLastFewDays(String uid, LocalDate date) throws UnsupportedEncodingException;

    /**
     * 获取最近几天的签到情况, 多少天由 lastFewDays 决定
     *
     * @param uid           用户ID
     * @param date          日期
     * @param lastFewDays   最近几天
     * @return Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    Map<String, Boolean> getSignInfoForTheLastFewDays(String uid, LocalDate date, int lastFewDays) throws UnsupportedEncodingException;

}
