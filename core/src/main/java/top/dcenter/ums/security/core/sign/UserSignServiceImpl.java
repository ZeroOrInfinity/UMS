package top.dcenter.ums.security.core.sign;

import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import top.dcenter.ums.security.core.api.sign.service.SignService;
import top.dcenter.ums.security.core.properties.SignProperties;
import top.dcenter.ums.security.core.util.SignUtil;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static top.dcenter.ums.security.core.util.SignUtil.formatDate;

/**
 * 基于Redis位图的用户签到功能实现类<br><br>
 * 要自定义签到功能, 实现 {@link SignService}, 注入 IOC 即可<br><br>
 * 实现功能：<br>
 * 1. 用户签到<br>
 * 2. 检查用户是否签到<br>
 * 3. 获取用户当月签到次数<br>
 * 4. 获取用户当月最近连续签到次数<br>
 * 5. 获取用户当月首次签到日期<br>
 * 6. 获取用户当月每天的签到详情<br>
 * 7. 获取用户最近几天的签到情况<br>
 * @author flex_song zyw
 * @version V1.0  Created by 2020-09-14 10:00
 */
public class UserSignServiceImpl implements SignService {

    private final RedisConnectionFactory redisConnectionFactory;
    private final SignProperties signProperties;

    public UserSignServiceImpl(RedisConnectionFactory redisConnectionFactory, SignProperties signProperties) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.signProperties = signProperties;
    }

    public RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }

    private byte[] buildSignKey(String uid, LocalDate date) throws UnsupportedEncodingException {
        return (signProperties.getSignKeyPrefix()
                + SignUtil.buildSignKey(uid, date)).getBytes(signProperties.getCharset());
    }

    /**
     * 用户签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 之前的签到状态
     * @throws UnsupportedEncodingException
     */
    @Override
    public boolean doSign(String uid, LocalDate date) throws UnsupportedEncodingException {
        int offset = date.getDayOfMonth() - 1;
        try (RedisConnection connection = getConnection())
        {
            final Boolean isSet = connection.setBit(buildSignKey(uid, date), offset, true);
            return Optional.ofNullable(isSet).orElse(false);
        }
    }

    /**
     * 检查用户是否签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到状态
     * @throws UnsupportedEncodingException
     */
    @Override
    public boolean checkSign(String uid, LocalDate date) throws UnsupportedEncodingException {
        int offset = date.getDayOfMonth() - 1;
        try (RedisConnection connection = getConnection())
        {
            Boolean isSign = connection.getBit(buildSignKey(uid, date), offset);
            return Optional.ofNullable(isSign).orElse(false);
        }
    }

    /**
     * 获取用户当月签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到次数
     * @throws UnsupportedEncodingException
     */
    @Override
    public long getSignCount(String uid, LocalDate date) throws UnsupportedEncodingException {
        try (RedisConnection connection = getConnection())
        {
            Long success = connection.bitCount(buildSignKey(uid, date));
            return Optional.ofNullable(success).orElse(0L);
        }
    }

    /**
     * 获取当月连续签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当月连续签到次数
     * @throws UnsupportedEncodingException
     */
    @Override
    public long getContinuousSignCount(String uid, LocalDate date) throws UnsupportedEncodingException {
        int signCount = 0;
        List<Long> list = null;
        try (RedisConnection connection = getConnection())
        {
            final BitFieldSubCommands subCommands =
                    BitFieldSubCommands.create()
                            .get(BitFieldSubCommands.BitFieldType
                            .unsigned(date.getDayOfMonth()))
                            .valueAt(0L);
            list = connection.bitField(buildSignKey(uid, date), subCommands);
        }
        if (list != null && list.size() > 0) {
            // 取低位连续不为0的个数即为连续签到次数，需考虑当天尚未签到的情况
            long v = list.get(0) == null ? 0 : list.get(0);
            for (int i = 0; i < date.getDayOfMonth(); i++) {
                if (v >> 1 << 1 == v) {
                    // 低位为0且非当天说明连续签到中断了
                    if (i > 0)
                    {
                        break;
                    }
                } else {
                    signCount += 1;
                }
                v >>= 1;
            }
        }
        return signCount;
    }

    /**
     * 获取当月首次签到日期
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 首次签到日期, 如果没有任何签到日期，返回 null
     * @throws UnsupportedEncodingException
     */
    @Override
    public LocalDate getFirstSignDate(String uid, LocalDate date) throws UnsupportedEncodingException {
        Long pos = -1L;
        try (RedisConnection connection = getConnection())
        {
            pos = connection.bitPos(buildSignKey(uid, date), true);
        }
        return (pos != null && pos < 0) ? null : date.withDayOfMonth((int) (pos + 1));
    }

    /**
     * 获取当月的签到情况
     *
     * @param uid  用户ID
     * @param date 日期
     * @return Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException
     */
    @Override
    public Map<String, Boolean> getSignInfo(String uid, LocalDate date) throws UnsupportedEncodingException {
        Map<String, Boolean> signMap = new HashMap<>(date.getDayOfMonth());
        List<Long> list;
        try (RedisConnection connection = getConnection())
        {
            final BitFieldSubCommands subCommands =
                    BitFieldSubCommands.create()
                                       .get(BitFieldSubCommands.BitFieldType
                                       .unsigned(date.lengthOfMonth()))
                                       .valueAt(0L);
            list = connection.bitField(buildSignKey(uid, date), subCommands);
        }
        fillingSignMap(date, signMap, date.lengthOfMonth(), 0, list);
        return signMap;
    }

    /**
     * 获取最近几天的签到情况, 多少天由 {@link SignProperties#getLastFewDays()} 决定
     *
     * @param uid   用户ID
     * @param date  日期
     * @return      Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException
     */
    @Override
    public Map<String, Boolean> getSignInfoForTheLastFewDays(String uid, LocalDate date) throws UnsupportedEncodingException {

        // 今天是当月的第几天
        int dayOfMonth = date.getDayOfMonth();
        // 获取最近几天的签到情况, 默认为 7 天
        int lastFewDays = signProperties.getLastFewDays();

        Map<String, Boolean> signMap = new HashMap<>(dayOfMonth);

        // 当天在本月中的天数 >= 要获取最近 lastFewDays 天的签到情况
        if (dayOfMonth >= lastFewDays)
        {

            fillingSignDetail2SignMap(lastFewDays, dayOfMonth - lastFewDays, uid, date, dayOfMonth,
                                      dayOfMonth - lastFewDays, signMap);
        }
        // 当天在本月中的天数 < 要获取最近 lastFewDays 天的签到情况, 需要从上个月获取
        else
        {
            fillingSignDetail2SignMapOfCrossMonth(uid, date, dayOfMonth, lastFewDays, signMap);
        }

        return signMap;
    }

    /**
     * 把 list 数据注入到 signMap 中
     * @param date              date 与 lowDay 和 beforeOfHighDay 有对应关系
     * @param signMap           Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @param lowDay            低位数(DayOfMonth)
     * @param beforeOfHighDay   高位数(DayOfMonth)
     * @param list              签到的 bit 数据
     */
    private void fillingSignMap(LocalDate date, Map<String, Boolean> signMap, int lowDay, int beforeOfHighDay, List<Long> list) {
        if (list != null && list.size() > 0)
        {
            // 由低位到高位，为0表示未签到，为1表示已签到
            long v = list.get(0) == null ? 0 : list.get(0);
            for (int i = lowDay; i > beforeOfHighDay; i--)
            {
                LocalDate d = date.withDayOfMonth(i);
                signMap.put(formatDate(d, "yyyy-MM-dd"), v >> 1 << 1 != v);
                v >>= 1;
            }
        }
    }


    /**
     * 获取指定天数的签到情况, 指定天数都在不在同一个月内
     * @param uid           用户id
     * @param date          日期
     * @param dayOfMonth    当月的第几天
     * @param lastFewDays   获取最近几天的签到情况, 默认为 7 天
     * @param signMap       Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException
     */
    private void fillingSignDetail2SignMapOfCrossMonth(String uid, LocalDate date, int dayOfMonth,
                                                       int lastFewDays, Map<String, Boolean> signMap) throws UnsupportedEncodingException {


        LocalDate preMonthsDate = date.minusMonths(1);
        int lengthOfPreMonth = preMonthsDate.lengthOfMonth();
        int remainingDays = lastFewDays - dayOfMonth;

        // 当月
        int currentMonthType = dayOfMonth;
        int currentMonthOffset = 0;
        int currentMonthLowDay = dayOfMonth;
        int currentMonthBeforeOfHighDay = 0;

        // 上月
        int preMonthType = remainingDays;
        int preMonthOffset = lengthOfPreMonth- remainingDays;
        int preMonthLowDay = lengthOfPreMonth;
        int preMonthBeforeOfHighDay = lengthOfPreMonth - remainingDays;


        List<Long> currentMonthList, preMonthList;
        try (RedisConnection connection = getConnection())
        {
            // 获取当月中 dayOfMonth 天签到情况
            final BitFieldSubCommands subCommands =
                            BitFieldSubCommands.create()
                                               .get(BitFieldSubCommands.BitFieldType.unsigned(currentMonthType))
                                               .valueAt(currentMonthOffset);
            currentMonthList = connection.bitField(buildSignKey(uid, date), subCommands);

            // 获取上月中月底的 remainingDays 天签到情况
            final BitFieldSubCommands preMonthSubCommands =
                            BitFieldSubCommands.create()
                                               .get(BitFieldSubCommands.BitFieldType.unsigned(preMonthType))
                                               .valueAt(preMonthOffset);
            preMonthList = connection.bitField(buildSignKey(uid, preMonthsDate), preMonthSubCommands);
        }

        // 当月
        fillingSignMap(date, signMap, currentMonthLowDay, currentMonthBeforeOfHighDay, currentMonthList);
        // 上月
        fillingSignMap(preMonthsDate, signMap, preMonthLowDay, preMonthBeforeOfHighDay, preMonthList);
    }



    /**
     * 获取指定天数的签到情况, 指定天数都在同一个月内
     * @param type              无符号的位数
     * @param offset            偏移量
     * @param uid               用户id
     * @param date              日期
     * @param lowDay            低位数(DayOfMonth)
     * @param beforeOfHighDay   高位数(DayOfMonth)
     * @param signMap           Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException
     */
    private void fillingSignDetail2SignMap(int type, int offset, String uid, LocalDate date,
                                           int lowDay, int beforeOfHighDay, Map<String, Boolean> signMap) throws UnsupportedEncodingException {

        List<Long> list;
        try (RedisConnection connection = getConnection())
        {
            final BitFieldSubCommands subCommands =
                            BitFieldSubCommands.create()
                                               .get(BitFieldSubCommands.BitFieldType.unsigned(type))
                                               .valueAt(offset);
            list = connection.bitField(buildSignKey(uid, date), subCommands);
        }
        fillingSignMap(date, signMap, lowDay, beforeOfHighDay, list);
    }

}