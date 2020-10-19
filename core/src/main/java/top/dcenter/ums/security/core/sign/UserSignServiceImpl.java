package top.dcenter.ums.security.core.sign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPipelineException;
import org.springframework.lang.NonNull;
import top.dcenter.ums.security.core.api.sign.service.SignService;
import top.dcenter.ums.security.core.sign.properties.SignProperties;
import top.dcenter.ums.security.core.util.SignUtil;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
 * 7. 获取用户最近几天的签到情况, 多少天由 SignProperties#getLastFewDays() 决定.<br>
 * 8. 获取用户最近几天的签到情况<br>
 * 9. 删除指定用户与指定日期的数据<br>
 * 10. 删除指定月份的用户签到统计数据<br>
 * @author flex_song zyw
 * @version V1.0  Created by 2020-09-14 10:00
 */
@Slf4j
public class UserSignServiceImpl implements SignService {

    /**
     * 日期格式
     */
    private static final String PATTERN = "yyyy-MM-dd";

    private final RedisConnectionFactory redisConnectionFactory;
    private final SignProperties signProperties;


    private final String charset;

    public UserSignServiceImpl(RedisConnectionFactory redisConnectionFactory, SignProperties signProperties) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.signProperties = signProperties;
        charset = signProperties.getCharset();
    }

    public RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }

    private byte[] buildSignKey(String uid, LocalDate date) throws UnsupportedEncodingException {

        return (signProperties.getSignKeyPrefix()
                + SignUtil.buildSignKey(uid, date)).getBytes(charset);
    }

    private byte[] buildTotalSignKey(LocalDate date) throws UnsupportedEncodingException {

        return (signProperties.getTotalSignKeyPrefix()
                + SignUtil.formatDate(date)).getBytes(charset);
    }

    private byte[] buildDayKey(LocalDate date) throws UnsupportedEncodingException {
        return (Integer.toString(date.getDayOfMonth()).getBytes(charset));
    }

    /**
     * 删除指定用户与指定日期的数据
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public long delSignByUidAndDate(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {

        byte[] key = buildSignKey(uid, date);
        return del(key);
    }

    /**
     * 删除指定用户与指定日期的数据
     *
     * @param keyMap  k = 用户ID, v = LocalDate
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public long delSignByUidAndDate(@NonNull Map<String, LocalDate> keyMap) throws UnsupportedEncodingException {

        List<byte[]> keyList = new ArrayList<>();
        Set<Map.Entry<String, LocalDate>> entries = keyMap.entrySet();
        for (Map.Entry<String, LocalDate> entry : entries)
        {
            keyList.add(buildSignKey(entry.getKey(), entry.getValue()));
        }
        return del(keyList);
    }

    /**
     * 删除指定月份的用户签到统计数据
     *
     * @param date 日期
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public long delTotalSignByDate(@NonNull LocalDate date) throws UnsupportedEncodingException {

        byte[] key = buildTotalSignKey(date);
        return del(key);
    }

    /**
     * 删除指定月份的用户签到统计数据
     *
     * @param dates 日期列表
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public long delTotalSignByDate(@NonNull List<LocalDate> dates) throws UnsupportedEncodingException {
        List<byte[]> keyList = new ArrayList<>();
        for (LocalDate date : dates)
        {
            keyList.add(buildTotalSignKey(date));
        }
        return del(keyList);
    }

    /**
     * 用户签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 之前的签到状态
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public Boolean doSign(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {

        int offset = date.getDayOfMonth() - 1;
        byte[] key = buildSignKey(uid, date);
        byte[] totalKey = buildTotalSignKey(date);
        byte[] dayKey = buildDayKey(date);
        List<Object> pipelineList;
        try (RedisConnection connection = getConnection())
        {
            connection.openPipeline();

            connection.setBit(key, offset, true);
            // 统计所有用户签到统计, 不考虑是否百分百成功, 所以对结果不用关心
            connection.hIncrBy(totalKey, dayKey, 1L);
            connection.expire(key, signProperties.getUserExpired());
            connection.expire(totalKey, signProperties.getTotalExpired());

            pipelineList = connection.closePipeline();
        }
        catch (RedisPipelineException e)
        {
            log.error(String.format("用户签到错误-redis 操作错误: uid=%s, date=%s", uid, formatDate(date, PATTERN)), e);
            // 对于单个用户: 签到操作是幂等操作, 不用考虑之前用户签到是否成功与失败; 对于所有用户签到统计, 类似于PV, 不需要精确统计.
            return false;
        }

        return  Optional.of((Boolean) pipelineList.get(0)).orElse(Boolean.FALSE);
    }

    /**
     * 检查用户是否签到
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到状态
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public boolean checkSign(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {

        int offset = date.getDayOfMonth() - 1;
        byte[] key = buildSignKey(uid, date);
        try (RedisConnection connection = getConnection())
        {
            Boolean isSign = connection.getBit(key, offset);
            return Optional.ofNullable(isSign).orElse(false);
        }
    }

    /**
     * 获取用户当月签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当前的签到次数
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public long getSignCount(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {

        byte[] key = buildSignKey(uid, date);
        try (RedisConnection connection = getConnection())
        {
            Long success = connection.bitCount(key);
            return Optional.ofNullable(success).orElse(0L);
        }
    }

    @Override
    public long getTotalSignCount(@NonNull LocalDate date) throws UnsupportedEncodingException {

        Map<byte[], byte[]> monthMap;
        byte[] totalKey = buildTotalSignKey(date);
        try (RedisConnection connection = getConnection())
        {
            monthMap = connection.hGetAll(totalKey);
        }

        if (monthMap != null)
        {
            return monthMap.values().stream()
                    .mapToLong(b -> {
                        try
                        {
                            return Long.parseLong(new String(b, charset));
                        }
                        catch (Exception e)
                        {
                            log.error(String.format("获取所有用户签到总次数错误: date=%s", formatDate(date, PATTERN)), e);
                            return 0L;
                        }
                    })
                    .sum();
        }

        return -1L;
    }

    /**
     * 获取当月连续签到次数
     *
     * @param uid  用户ID
     * @param date 日期
     * @return 当月连续签到次数
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public long getContinuousSignCount(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {

        int signCount = 0;
        List<Long> list;
        byte[] key = buildSignKey(uid, date);
        final BitFieldSubCommands subCommands =
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType
                                     .unsigned(date.getDayOfMonth()))
                        .valueAt(0L);
        try (RedisConnection connection = getConnection())
        {
            list = connection.bitField(key, subCommands);
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
     * @param date 日期, 不能为 null
     * @return 首次签到日期, 如果没有任何签到日期，返回 null
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public LocalDate getFirstSignDate(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {
        //noinspection UnusedAssignment
        Long pos = -1L;
        byte[] key = buildSignKey(uid, date);
        try (RedisConnection connection = getConnection())
        {
            pos = connection.bitPos(key, true);
        }
        //noinspection
        return (pos == null || pos < 0) ? null : date.withDayOfMonth((int) (pos + 1));
    }

    /**
     * 获取当月的签到情况
     *
     * @param uid  用户ID
     * @param date 日期
     * @return Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public Map<String, Boolean> getSignInfo(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {

        Map<String, Boolean> signMap = new HashMap<>(date.getDayOfMonth());
        List<Long> list;
        byte[] key = buildSignKey(uid, date);
        final BitFieldSubCommands subCommands =
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType
                                     .unsigned(date.lengthOfMonth()))
                        .valueAt(0L);
        try (RedisConnection connection = getConnection())
        {
            list = connection.bitField(key, subCommands);
        }
        fillingSignMap(date, signMap, date.lengthOfMonth(), 0,
                       Optional.ofNullable(list).orElse(new ArrayList<>(0)));
        return signMap;
    }

    /**
     * 获取最近几天的签到情况, 多少天由 <pre> SignProperties#getLastFewDays() </pre>
     *
     * @param uid   用户ID
     * @param date  日期
     * @return      Key 为签到日期，Value 为签到状态的 Map("yyyy-MM-dd", boolean)
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @Override
    public Map<String, Boolean> getSignInfoForTheLastFewDays(@NonNull String uid, @NonNull LocalDate date) throws UnsupportedEncodingException {
        return getSignInfoForTheLastFewDays(uid, date, signProperties.getLastFewDays());
    }

    @Override
    public Map<String, Boolean> getSignInfoForTheLastFewDays(@NonNull String uid, @NonNull LocalDate date, int lastFewDays) throws UnsupportedEncodingException {

        // 今天是当月的第几天
        int dayOfMonth = date.getDayOfMonth();

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
    private void fillingSignMap(@NonNull LocalDate date, @NonNull Map<String, Boolean> signMap, int lowDay, int beforeOfHighDay, @NonNull List<Long> list) {
        if (list.size() > 0)
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
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private void fillingSignDetail2SignMapOfCrossMonth(@NonNull String uid, @NonNull LocalDate date, int dayOfMonth,
                                                       int lastFewDays, @NonNull Map<String, Boolean> signMap) throws UnsupportedEncodingException {


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

        byte[] key = buildSignKey(uid, date);
        // 获取当月中 dayOfMonth 天签到情况
        final BitFieldSubCommands subCommands =
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(currentMonthType))
                        .valueAt(currentMonthOffset);

        byte[] preMonthKey = buildSignKey(uid, preMonthsDate);
        // 获取上月中月底的 remainingDays 天签到情况
        final BitFieldSubCommands preMonthSubCommands =
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(preMonthType))
                        .valueAt(preMonthOffset);
        try (RedisConnection connection = getConnection())
        {
            currentMonthList = connection.bitField(key, subCommands);
            preMonthList = connection.bitField(preMonthKey, preMonthSubCommands);
        }

        // 当月
        fillingSignMap(date, signMap, currentMonthLowDay, currentMonthBeforeOfHighDay,
                       Optional.ofNullable(currentMonthList).orElse(new ArrayList<>(0)));
        // 上月
        fillingSignMap(preMonthsDate, signMap, preMonthLowDay, preMonthBeforeOfHighDay,
                       Optional.ofNullable(preMonthList).orElse(new ArrayList<>(0)));
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
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    private void fillingSignDetail2SignMap(int type, int offset, @NonNull String uid, @NonNull LocalDate date,
                                           int lowDay, int beforeOfHighDay, @NonNull Map<String, Boolean> signMap) throws UnsupportedEncodingException {

        List<Long> list;
        byte[] key = buildSignKey(uid, date);
        final BitFieldSubCommands subCommands =
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(type))
                        .valueAt(offset);
        try (RedisConnection connection = getConnection())
        {
            list = connection.bitField(key, subCommands);
        }
        fillingSignMap(date, signMap, lowDay, beforeOfHighDay,
                       Optional.ofNullable(list).orElse(new ArrayList<>(0)));
    }


    /**
     * 从 redis 中删除指定的 keyList
     *
     * @param keyList  key 列表
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @SuppressWarnings("RedundantThrows")
    private long del(@NonNull List<byte[]> keyList) throws UnsupportedEncodingException {

        Long result;

        byte[][] bytesKey = new byte[keyList.size()][];
        keyList.toArray(bytesKey);
        try (RedisConnection connection = getConnection())
        {
            result = connection.del(bytesKey);
        }
        return  Optional.ofNullable(result).orElse(0L);
    }

    /**
     * 从 redis 中删除指定的 key
     *
     * @param key  key
     * @return 已删除的 key 数量
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    @SuppressWarnings("RedundantThrows")
    private long del(@NonNull byte[] key) throws UnsupportedEncodingException {

        Long result;
        try (RedisConnection connection = getConnection())
        {
            result = connection.del(key);
        }
        return  Optional.ofNullable(result).orElse(0L);
    }

}