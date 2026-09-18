package com.ai.travel.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期时间格式化器（默认格式）
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

    /**
     * 日期格式化器（默认格式）
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);

    /**
     * 时间格式化器（默认格式）
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);

    // ==================== LocalDateTime 相关 ====================

    /**
     * 获取当前 LocalDateTime
     */
    public static LocalDateTime getNow() {
        return LocalDateTime.now();
    }

    /**
     * LocalDateTime 转字符串（默认格式）
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * LocalDateTime 转字符串（自定义格式）
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 字符串转 LocalDateTime（默认格式）
     */
    public static LocalDateTime parseDateTime(String str) {
        return str != null ? LocalDateTime.parse(str, DATETIME_FORMATTER) : null;
    }

    /**
     * 字符串转 LocalDateTime（自定义格式）
     */
    public static LocalDateTime parseDateTime(String str, String pattern) {
        return str != null ? LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern)) : null;
    }

    // ==================== LocalDate 相关 ====================

    /**
     * 获取当前 LocalDate
     */
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    /**
     * LocalDate 转字符串（默认格式）
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * LocalDate 转字符串（自定义格式）
     */
    public static String formatDate(LocalDate date, String pattern) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 字符串转 LocalDate（默认格式）
     */
    public static LocalDate parseDate(String str) {
        return str != null ? LocalDate.parse(str, DATE_FORMATTER) : null;
    }

    /**
     * 字符串转 LocalDate（自定义格式）
     */
    public static LocalDate parseDate(String str, String pattern) {
        return str != null ? LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern)) : null;
    }

    // ==================== LocalTime 相关 ====================

    /**
     * 获取当前 LocalTime
     */
    public static LocalTime getNowTime() {
        return LocalTime.now();
    }

    /**
     * LocalTime 转字符串（默认格式）
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : null;
    }

    /**
     * LocalTime 转字符串（自定义格式）
     */
    public static String formatTime(LocalTime time, String pattern) {
        return time != null ? time.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 字符串转 LocalTime（默认格式）
     */
    public static LocalTime parseTime(String str) {
        return str != null ? LocalTime.parse(str, TIME_FORMATTER) : null;
    }

    /**
     * 字符串转 LocalTime（自定义格式）
     */
    public static LocalTime parseTime(String str, String pattern) {
        return str != null ? LocalTime.parse(str, DateTimeFormatter.ofPattern(pattern)) : null;
    }

    // ==================== 日期计算 ====================

    /**
     * 获取某天的开始时间
     */
    public static LocalDateTime beginOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * 获取某天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX) : null;
    }

    /**
     * 获取当月第一天
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(1) : null;
    }

    /**
     * 获取当月最后一天
     */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date != null ? date.withDayOfMonth(date.lengthOfMonth()) : null;
    }

    /**
     * 获取当年第一天
     */
    public static LocalDate firstDayOfYear(LocalDate date) {
        return date != null ? date.withDayOfYear(1) : null;
    }

    /**
     * 获取当年最后一天
     */
    public static LocalDate lastDayOfYear(LocalDate date) {
        return date != null ? date.withDayOfYear(date.lengthOfYear()) : null;
    }

    /**
     * 日期加天数
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return date != null ? date.plusDays(days) : null;
    }

    /**
     * 日期加月数
     */
    public static LocalDate plusMonths(LocalDate date, long months) {
        return date != null ? date.plusMonths(months) : null;
    }

    /**
     * 日期加年数
     */
    public static LocalDate plusYears(LocalDate date, long years) {
        return date != null ? date.plusYears(years) : null;
    }

    /**
     * 日期减天数
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        return date != null ? date.minusDays(days) : null;
    }

    /**
     * 日期减月数
     */
    public static LocalDate minusMonths(LocalDate date, long months) {
        return date != null ? date.minusMonths(months) : null;
    }

    /**
     * 日期减年数
     */
    public static LocalDate minusYears(LocalDate date, long years) {
        return date != null ? date.minusYears(years) : null;
    }

    // ==================== 日期比较 ====================

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的小时差
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算两个日期时间之间的分钟差
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 判断日期是否在指定范围内（包含边界）
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * 判断日期是否在指定范围内（包含边界）
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        if (dateTime == null || start == null || end == null) {
            return false;
        }
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }

    /**
     * 判断是否是今天
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * 判断是否是昨天
     */
    public static boolean isYesterday(LocalDate date) {
        return date != null && date.equals(LocalDate.now().minusDays(1));
    }

    /**
     * 判断是否是明天
     */
    public static boolean isTomorrow(LocalDate date) {
        return date != null && date.equals(LocalDate.now().plusDays(1));
    }

}