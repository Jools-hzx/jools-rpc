package com.jools.rpc.time;

import cn.hutool.core.util.StrUtil;
import com.jools.rpc.utils.DateUtils;
import org.apache.log4j.helpers.DateTimeDateFormat;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/11/23 17:18
 * @description: TODO
 */
public class TimeAPITest {

    @Test
    public void testDateUtils() {
        LocalDateTime currentDateTime = DateUtils.getCurrentDateTime();
        DateTimeFormatter zhFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String formatTime = zhFormatter.format(currentDateTime);
        System.out.println(formatTime);
        /*
         输出: 2024-11-23 22:41:39
         */
    }

    @Test
    public void testDate2LocalDateTime() {
        // LocalDateTime 转换为 Date
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        Date date = Date.from(zonedDateTime.toInstant());

        //Date 转换为 LocalDateTime
        Date d = new Date();
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Test
    public void testDateFormatter() {
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm ZZZZ");
        System.out.println(formatter.format(zdt));

        DateTimeFormatter zhFormatter = DateTimeFormatter.ofPattern("yyyy MMM dd EE HH:mm", Locale.CHINA);
        System.out.println(zhFormatter.format(zdt));

        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("E, MMMM/dd/yyyy HH:mm", Locale.US);
        System.out.println(ofPattern);

        /*
         输出:
            2024-11-23T20:43 GMT+08:00
            2024 11月 23 周六 20:43
            Text(DayOfWeek,SHORT)',''
            'Text(MonthOfYear)'/'Value(DayOfMonth,2)'/'Value(YearOfEra,4,19,EXCEEDS_PAD)' 'Value(HourOfDay,2)':'Value(MinuteOfHour,2)
         */
    }

    @Test
    public void testInstant() {
        //1. 创建 Instant 的对象，获取此刻时间信息
        Instant now = Instant.now();

        //获取总秒数
        long second = now.getEpochSecond();
        System.out.println(second);

        //不够 1s 的纳秒数
        int nano = now.getNano();
        System.out.println(nano);

        //基于纳秒加减
        Instant plus = now.plusNanos(111);
        System.out.println(plus.getEpochSecond());

        //作用: 做代码的性能分析，或者记录用户的操作时间
        Instant n1 = Instant.now();
        //代码执行
        Instant n2 = Instant.now();
        System.out.println("执行时间:" + (n2.getEpochSecond() - n1.getEpochSecond()));

        /*
        输出:
        1732357104
        798495700
        1732357104
        执行时间:0
         */
    }

    @Test
    public void testWithModify() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime modify = now.withMonth(12);
        System.out.println(modify);

        // 获取本月第一天
        System.out.println("当月第一天0:00时刻" + now.withDayOfMonth(1));
        //获取当月第一天
        System.out.println("当月第一天：" + now.with(TemporalAdjusters.firstDayOfMonth()));
        //获取下月第一天
        System.out.println("下月第一天：" + now.with(TemporalAdjusters.firstDayOfNextMonth()));
        //获取明年第一天
        System.out.println("明年第一天：" + now.with(TemporalAdjusters.firstDayOfNextYear()));
        //获取本年第一天
        System.out.println("本年第一天：" + now.with(TemporalAdjusters.firstDayOfYear()));
        //获取当月最后一天
        System.out.println("当月最后一天：" + now.with(TemporalAdjusters.lastDayOfMonth()));
        //获取本年最后一天
        System.out.println("本年最后一天：" + now.with(TemporalAdjusters.lastDayOfYear()));
        //获取当月第三周星期五
        System.out.println("当月第三周星期五：" + now.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.FRIDAY)));
        //获取上周一
        System.out.println("上周一：" + now.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)));
        //获取下周日
        System.out.println("下周日：" + now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
    }

    @Test
    public void testModifyLocalDateTime() {
        LocalDateTime dt = LocalDateTime.of(2024, 10, 10, 20, 30, 59);
        System.out.println(dt);

        //加 5 天减 3 小时
        LocalDateTime mdt = dt.plusDays(5).minusHours(3);
        System.out.println(mdt);

        //减去一个月
        LocalDateTime newDt = dt.minusMonths(1);
        System.out.println(newDt);

        /*
         输出:
            2024-10-10T20:30:59
            2024-10-15T17:30:59
            2024-09-10T20:30:59
         */
    }


    @Test
    public void testLocalDateTime() {

        LocalDateTime localDateTime = LocalDateTime.of(2024, 11, 23, 17, 55, 0);
        LocalDate localDay = LocalDate.of(2024, 10, 07);
        LocalTime localTime = LocalTime.parse("08:15:07");
        System.out.println(localDateTime);
        System.out.println(localDay);
        System.out.println(localTime);

        /*
        输出:
            2024-11-23T17:55
            2024-10-07
            08:15:07
         */
    }

    static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_LOCAL
            = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );

    @Test
    public void testSimpleDateFormat() throws ParseException {

        // SimpleDateFormat线程不安全，每次使用都要构造新的，在初始的时候定义解析的字符串格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将指定字符串String解析为Date
        Date date = format.parse("2024-10-07 16:10:22");

        // 将Date格式化为String
        String str = format.format(date);
        System.out.println(str);
    }


    @Test
    public void testTimeZone() {
        // 当前时间:
        Calendar c = Calendar.getInstance();
        // 清除所有字段:
        c.clear();
        // 设置为北京时区:
        c.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        // 设置年月日时分秒:
        c.set(2024, 9 /* 10月 */, 10, 8, 15, 0);
        // 显示时间:
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        System.out.println(sdf.format(c.getTime()));

        /*
        输出: 2024-10-09 20:15:00
         */
    }

    @Test
    public void testFormatCalendar() {
        // 当前时间:
        Calendar c = Calendar.getInstance();
        // 清除所有:
        c.clear();
        // 设置年月日时分秒:
        c.set(2019, 10 /* 11月 */, 20, 8, 15, 0);
        // 加5天并减去2小时:
        c.add(Calendar.DAY_OF_MONTH, 5);
        c.add(Calendar.HOUR_OF_DAY, -2);
        // 显示时间:
        var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = c.getTime();
        System.out.println(sdf.format(d));
    }

    @Test
    public void testCalendar() {

        // 获取当前时间:
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);//返回年份不用转换
        int m = 1 + c.get(Calendar.MONTH);//返回月份需要加1
        int d = c.get(Calendar.DAY_OF_MONTH);
        int w = c.get(Calendar.DAY_OF_WEEK);//返回的
        int hh = c.get(Calendar.HOUR_OF_DAY);
        int mm = c.get(Calendar.MINUTE);
        int ss = c.get(Calendar.SECOND);
        int ms = c.get(Calendar.MILLISECOND);
        System.out.println(y + "-" + m + "-" + d + " " + w + " " + hh + ":" + mm + ":" + ss + "." + ms);

        /*
        输出: 2024-11-23 7 17:28:33.62
         */
    }

    @Test
    public void testGetTime() {
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(currentTimeMillis);
    }

    @Test
    public void testDate() {
        Date date = new Date(System.currentTimeMillis());
        System.out.println(date);

        /*
         输出: Sat Nov 23 17:26:43 CST 2024
         */
    }
}
