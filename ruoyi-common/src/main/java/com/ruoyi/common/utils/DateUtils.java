package com.ruoyi.common.utils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 * 
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * [将yyyy-MM-dd'T'HH:mmXXX时间格式转为yyyy-MM-dd hh:mm:ss]
     * @author 陈湘岳 2024/2/27
     * @param dateTimeStr 传入的yyyy-MM-dd'T'HH:mmXXX时间格式
     * @return java.lang.String 转换后的时间格式
     **/
    public static String convertToYYYYMMDDhhmmss(String dateTimeStr) {
        // 使用ISO_DATE_TIME格式解析输入字符串
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeStr, inputFormatter);

        // 定义输出格式
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        // 格式化输出
        return outputFormatter.format(zonedDateTime);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2)
    {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算时间差
     *
     * @param endDate 最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(Date endDate, Date startTime)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }


    /**
     * 判断指定时间是否距今超过指定天数
     * @param targetDate 要判断的时间
     * @param days 天数
     * @param includeToday 是否包含当天（true: ≥ 指定天数; false: > 指定天数）
     * @return 是否超过指定天数
     */
    public static boolean isOverDays(Date targetDate, int days, boolean includeToday) {
        if (targetDate == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDateTime = targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime daysAgo = now.minusDays(days);

        if (includeToday) {
            // 包含当天：目标时间 ≤ daysAgo
            return !targetDateTime.isAfter(daysAgo);
        } else {
            // 不包含当天：目标时间 < daysAgo
            return targetDateTime.isBefore(daysAgo);
        }
    }

    /**
     * 判断是否超过30天（不包含第30天）
     */
    public static boolean isOver30Days(Date targetDate) {
        return isOverDays(targetDate, 30, false);
    }

    /**
     * 判断是否超过30天（包含第30天）
     */
    public static boolean isOver30DaysInclusive(Date targetDate) {
        return isOverDays(targetDate, 30, true);
    }

    /**
     * 获取距今天数
     */
    public static long getDaysFromNow(Date targetDate) {
        if (targetDate == null) {
            return 0;
        }

        Instant now = Instant.now();
        Instant target = targetDate.toInstant();

        // 计算天数差
        long days = java.time.Duration.between(target, now).toDays();
        return days;
    }

    /**
     * 获取本月第一天开始的时间
     */
    public static Date beginOfMonth() {
        //获取本月第一天的时间
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        return Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    //获取今天开始的时间
    public static Date beginOfDay() {
        //获取今天的时间
        LocalDate today = LocalDate.now();
        return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    //获取今年第一天开始的时间
    public static Date beginOfYear() {
        //获取今年第一天的时间
        LocalDate firstDayOfYear = LocalDate.now().withDayOfYear(1);
        return Date.from(firstDayOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    //获取指定天数之前的时间
    public static Date getDateBefore(int days) {
        //获取指定天数之前的时间
        LocalDate dateBefore = LocalDate.now().minusDays(days);
        return Date.from(dateBefore.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
