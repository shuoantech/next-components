/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
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
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.compute.core.util;



import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 类DateUtils.java的实现描述：<br/>
 * 时间工具类
 */
@Slf4j
public class DateUtil {

    /**
     * DATE_FORMAT_YYMMDD
     */
    public static String DATE_FORMAT_YYMMDD          = "yyMMdd";

    /**
     * DATE_FORMAT_YYYYMMDD
     */
    public static String DATE_FORMAT_YYYYMMDD        = "yyyyMMdd";

    /**
     * DATE_FORMAT_YYYY_MM_DD
     */
    public static String DATE_FORMAT_YYYY_MM_DD      = "yyyy-MM-dd";

    /**
     * DATE_FORMAT_YYYYMMDDHHMMSS
     */
    public static String DATE_FORMAT_YYYYMMDDHHMMSS  = "yyyyMMddHHmmss";

    /**
     * DATE_FORMAT_YYYYMMDDHHMMSS
     */
    public static String DATE_FORMAT_YYYYMMDD2       = "yyyy/MM/dd";

    /**
     * DATE_FORMAT_YYYYMMDD_HHMMSS
     */
    public static String DATE_FORMAT_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    
    /***
     * SHORT_DATE_GBK_FORMAT
     */
    public static final String SHORT_DATE_GBK_FORMAT        = "yyyy年MM月dd日";



    /**
     * 时间转化
     * 
     * @param time 需要转换的时间字符串
     * @return 转换后的时间
     */
    public static Date parseDateByYYYY_MM_DD(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            log.error("时间转化异常。", e);
        }
        return date;
    }

    /**
     * 时间转化
     * 
     * @param time 需要转换的时间字符串
     * @return 转换后的时间
     */
    public static Date parseDateByYYYYMMDD(String time) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD);
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            log.error("时间转化异常。", e);
        }
        return date;
    }

    /**
     * 将String转换成YYYYMMddHHmmss的Date
     * 
     * @param time 需要转换的字符串
     * @return 转换之后的时间
     */
    public static Date parseDateByYYYYMMDDHHmmss(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            log.error("时间转化异常。", e);
        }

        return null;
    }

    /**
     * 将String转换成yyyy-MM-dd HH:mm:ss的Date
     * 
     * @param time 需要转换的字符串
     * @return 转换之后的时间
     */
    public static Date parseDateByYYYYMMDD_HHmmss(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD_HHMMSS);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            log.error("时间转化异常。", e);
        }

        return null;
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static Date getSystemByYYYYMMDD() {
        Date systime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD);
        return parseDateByYYYYMMDD(sdf.format(systime));
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static Date getSystemByYYYYMMddHHmmss() {
        Date systime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS);
        return parseDateByYYYYMMDDHHmmss(sdf.format(systime));
    }

    /**
     * 将Date转换成指定格式的字符串
     * @param time
     * @param pattern
     * @return
     */
    public static String formatDate(Date time, String pattern) {
        if (null == time) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(time);
    }



    /**
     * 将Date转换成YYYYMMddHHmmss的字符串
     * 
     * @param time 需要转换的时间
     * @return 转换之后的时间字符串
     */
    public static String formatDateByYYYYMMddHHmmss(Date time) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDDHHMMSS);
        return sdf.format(time);
    }

    /**
     * 将Date转换成DATE_FORMAT_YYYYMMDD_HHMMSS的字符串
     * 
     * @param time 需要转换的时间
     * @return 转换之后的时间字符串
     */
    public static String formatDateByYYYYMMdd_HHmmss(Date time) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD_HHMMSS);
        return sdf.format(time);
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static String formatDateByYYYYMMDD(Date time) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD);
        return sdf.format(time);
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static String formatDateByYYMMDD(Date time) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYMMDD);
        return sdf.format(time);
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static String formatDateByYYYY_MM_DD(Date time) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
        return sdf.format(time);
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static int getDay(Date time, int type) {
        if (null == time) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(type);
    }

    /**
     * 增加天数
     * 
     * @param time 增加 前的时间
     * @param days 增加的天数
     * @return 增加之后的时间
     */
    public static Date addDays(Date time, int days) {
        if (null == time) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * 增加天数
     * 
     * @param time 增加 前的时间
     * @param days 增加时间实际数字
     * @param addType 添加的类型（1：天数、2：月数、3：年数）
     * @return 增加后的时间
     */
    public static Date addDays(Date time, int days, int addType) {
        if (null == time) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        if (1 == addType) {
            // 添加天数
            calendar.add(Calendar.DAY_OF_MONTH, days);
        } else if (2 == addType) {
            // 添加月数
            calendar.add(Calendar.MONTH, days);
        } else if (3 == addType) {
            // 添加年数
            calendar.add(Calendar.YEAR, days);
        } else {
            return null;
        }
        return calendar.getTime();
    }

    /**
     * 系统时间精确到天
     * 
     * @return 精确到天的系统时间
     */
    public static String formatDateByYYYYMMDD2(Date time) {
        if (null == time) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD2);
        return sdf.format(time);
    }

    /**
     * 时间转化
     * 
     * @param time 需要转换的时间字符串
     * @return 转换后的时间
     */
    public static Date parseDateByPattern(String time, String pattern) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            log.error("时间转化异常。", e);
        }
        return date;
    }

    /**
     * 计算两个日期相隔的天数（比较年月日）
     * 
     * @param date1 当前时期
     * @param date2 数据库字段日期
     * @return
     */
    public static int getDays(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String s1 = sdf.format(date1);
        String s2 = sdf.format(date2);
        /*
         * System.err.println(s1); System.err.println(s2);
         */

        s1 = s1.substring(0, 11);
        s2 = s2.substring(0, 11);

        s1 = s1 + "00-00-00";
        s2 = s2 + "00-00-00";

        try {
            Date d1 = sdf.parse(s1);
            Date d2 = sdf.parse(s2);

            long days = (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);
            return (int) days;
        } catch (ParseException e) {
            log.error("", e);
            return 999999;
        }
    }

    /**
     * 增加月数
     * 
     * @param time 增加前的时间
     * @param months 增加的月数
     * @return 增加后的时间
     */
    public static Date addMonths(Date time, int months) {
        if (null == time) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * 增加分钟
     * 
     * @param time 增加前的时间
     * @param minute 增加分钟数
     * @return 增加后的时间
     */
    public static Date addMinute(Date time, int minute) {
        if (null == time) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /**
     * 获取某天的开始时间.
     *
     * @param someDay the some day
     * @return the one day begin
     */
    public static Date getOneDayBegin(Date someDay) {
        if (someDay == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(someDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取某天的末尾时间.
     *
     * @param someDay the some day
     * @return the one day end
     */
    public static Date getOneDayEnd(Date someDay) {
        if (someDay == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(someDay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getTermDate(Date beginDate, String value, String type) {
        if (value == null || type == null) {
            return null;
        }
        type = type.toUpperCase().trim();
        if (!type.equals("DAY") && !type.equals("MONTH") && !type.equals("YEAR")) {
            return null;
        }

        Calendar end = Calendar.getInstance();
        end.setTime(beginDate);
        end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        if ("DAY".equals(type)) {
            end.add(Calendar.DAY_OF_YEAR, Integer.valueOf(value));
        } else if ("MONTH".equals(type)) {
            end.add(Calendar.MONTH, Integer.valueOf(value));
        } else if ("YEAR".equals(type)) {
            end.add(Calendar.YEAR, Integer.valueOf(value));
        }
        return end.getTime();
    }

    /**
     * 将日期转化为yyyyMMdd 00:00:00
     * 
     * @param date
     * @return
     */
    public static Date formatDateYYYMMDD(Date date) {
        if (null == date) {
            return null;
        }
        date = DateUtils.setHours(date, 0);
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setMilliseconds(date, 0);
        return date;
    }

    /**
     * @Description:比较两个时间点 如果secondDate表示的时间等于此 firstDate 表示的时间，则返回 0 值； 如果此
     *                      firstDate 的时间在参数<secondDate>表示的时间之前，则返回小于 0 的值； 如果此
     *                      firstDate 的时间在参数<secondDate>表示的时间之后，则返回大于 0 的值
     * @param firstDate
     * @param secondDate
     * @ReturnType int
     * @author:
     */
    public static int compare(Date firstDate, Date secondDate) {

        Calendar firstCalendar = null;
        /** 使用给定的 Date 设置此 Calendar 的时间。 **/
        if (firstDate != null) {
            firstCalendar = Calendar.getInstance();
            firstCalendar.setTime(firstDate);
        }

        Calendar secondCalendar = null;
        /** 使用给定的 Date 设置此 Calendar 的时间。 **/
        if (secondDate != null) {
            secondCalendar = Calendar.getInstance();
            secondCalendar.setTime(secondDate);
        }

        try {
            /**
             * 比较两个 Calendar 对象表示的时间值（从历元至现在的毫秒偏移量）。 如果参数表示的时间等于此 Calendar
             * 表示的时间，则返回 0 值； 如果此 Calendar 的时间在参数表示的时间之前，则返回小于 0 的值； 如果此
             * Calendar 的时间在参数表示的时间之后，则返回大于 0 的值
             **/
            return firstCalendar.compareTo(secondCalendar);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 获得指定月的第一天
     * 
     * @param nowTime
     * @return
     */
    public static Date getMonthFirstDay(Date nowTime) {
        if (nowTime == null) {
            nowTime = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(nowTime);
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        return cal.getTime();
    }

    /**
     * 比较两个时间在指定单位上的差异
     * @param srcDate 源时间
     * @param tarDate 目标时间
     * @param unit 时间单位
     * @return 返回单位时间上的差异值。
     */
    @SuppressWarnings("incomplete-switch")
    public static long compareDate(Date srcDate, Date tarDate, TimeUnit unit) {
        if (srcDate == null || tarDate == null) throw new NullPointerException("date is Null");
        if (unit == null) unit = TimeUnit.DAYS;
        Calendar srcCal = Calendar.getInstance();
        srcCal.setTime(srcDate);
        Calendar tarCal = Calendar.getInstance();
        tarCal.setTime(tarDate);

        switch (unit) {
            case DAYS:
                srcCal.set(Calendar.HOUR_OF_DAY, 23);
                tarCal.set(Calendar.HOUR_OF_DAY, 23);
            case HOURS:
                srcCal.set(Calendar.MINUTE, 59);
                tarCal.set(Calendar.MINUTE, 59);
            case MINUTES:
                srcCal.set(Calendar.SECOND, 59);
                tarCal.set(Calendar.SECOND, 59);
            case SECONDS:
                srcCal.set(Calendar.MILLISECOND, 999);
                tarCal.set(Calendar.MILLISECOND, 999);
        }


        long srcNum = unit.convert(srcCal.getTimeInMillis(), TimeUnit.MILLISECONDS);
        long tarNum = unit.convert(tarCal.getTimeInMillis(), TimeUnit.MILLISECONDS);
        return srcNum - tarNum;
    }
    
    /***
     * 日期格式化成带年月日格式
     * @param time
     * @return 例子：2019年08月09日
     */
    public static String toShortDateGBKString(Date time) {  
    	 if (null == time) {
             return null;
         }
        SimpleDateFormat sdf = new SimpleDateFormat(SHORT_DATE_GBK_FORMAT);
        return sdf.format(time);
    }
    
    /**
     * 日期格式化，毫秒数设置为0
     * @param someDay
     * @return
     */
    public static Date formatDateForMillisecond(Date someDay){
        if (someDay == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(someDay);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
