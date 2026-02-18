package com.github.alphafoxz.foxden.common.core.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

/**
 * 日期工具类
 *
 * @author FoxDen Team
 */
object DateUtils {

    /** 年月格式 */
    const val YYYY_MM = "yyyy-MM"

    /** 年月日格式 */
    const val YYYY_MM_DD = "yyyy-MM-dd"

    /** 年月日时分秒格式 */
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    /** 默认日期格式 */
    const val DEFAULT_DATE_FORMAT = YYYY_MM_DD

    /** 默认时间格式 */
    const val DEFAULT_TIME_FORMAT = YYYY_MM_DD_HH_MM_SS

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    @JvmStatic
    fun now(): Date {
        return Date()
    }

    /**
     * 获取当前日期时间
     *
     * @return 当前日期时间
     */
    @JvmStatic
    fun getNowDate(): Date {
        return Date()
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳（毫秒）
     */
    @JvmStatic
    fun currentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date 日期
     * @return LocalDateTime
     */
    @JvmStatic
    fun toLocalDateTime(date: Date?): LocalDateTime {
        if (date == null) {
            return LocalDateTime.now()
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime 日期时间
     * @return Date
     */
    @JvmStatic
    fun toDate(localDateTime: LocalDateTime?): Date {
        if (localDateTime == null) {
            return Date()
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    /**
     * 格式化日期
     *
     * @param date 日期
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    @JvmStatic
    fun format(date: Date?, pattern: String): String {
        if (date == null) {
            return ""
        }
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(toLocalDateTime(date))
    }

    /**
     * 格式化日期（默认格式）
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    @JvmStatic
    fun format(date: Date?): String {
        return format(date, DEFAULT_DATE_FORMAT)
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return Date
     */
    @JvmStatic
    fun parse(dateStr: String?, pattern: String): Date? {
        if (StringUtils.isBlank(dateStr)) {
            return null
        }
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            val localDateTime = LocalDateTime.parse(dateStr, formatter)
            toDate(localDateTime)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 解析日期字符串（默认格式）
     *
     * @param dateStr 日期字符串
     * @return Date
     */
    @JvmStatic
    fun parseDate(dateStr: String?): Date? {
        return parse(dateStr, DEFAULT_DATE_FORMAT)
    }

    /**
     * 获取某天的开始时间
     *
     * @param date 日期
     * @return 开始时间
     */
    @JvmStatic
    fun getStartOfDay(date: Date?): Date {
        val localDateTime = toLocalDateTime(date).toLocalDate().atStartOfDay()
        return toDate(localDateTime)
    }

    /**
     * 获取某天的结束时间
     *
     * @param date 日期
     * @return 结束时间
     */
    @JvmStatic
    fun getEndOfDay(date: Date?): Date {
        val localDateTime = toLocalDateTime(date).toLocalDate().atTime(23, 59, 59, 999999999)
        return toDate(localDateTime)
    }

    /**
     * 日期加减天数
     *
     * @param date 日期
     * @param days 天数
     * @return 计算后的日期
     */
    @JvmStatic
    fun addDays(date: Date?, days: Long): Date {
        val localDateTime = toLocalDateTime(date).plusDays(days)
        return toDate(localDateTime)
    }

    /**
     * 日期加月数
     *
     * @param date 日期
     * @param months 月数
     * @return 计算后的日期
     */
    @JvmStatic
    fun addMonths(date: Date?, months: Long): Date {
        val localDateTime = toLocalDateTime(date).plusMonths(months)
        return toDate(localDateTime)
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数差
     */
    @JvmStatic
    fun betweenDays(startDate: Date?, endDate: Date?): Long {
        val start = toLocalDateTime(startDate).toLocalDate()
        val end = toLocalDateTime(endDate).toLocalDate()
        return java.time.temporal.ChronoUnit.DAYS.between(start, end)
    }

    /**
     * 获取当前时间的时间戳（秒）
     *
     * @return 时间戳（秒）
     */
    @JvmStatic
    fun getSecondTimestamp(): Long {
        return System.currentTimeMillis() / 1000
    }

    /**
     * 获取时间段描述
     *
     * @param date 日期时间
     * @return 时间段描述
     */
    @JvmStatic
    fun getTodayHour(date: Date): String {
        val localDateTime = toLocalDateTime(date)
        val hour = localDateTime.hour
        return when {
            hour <= 6 -> "凌晨"
            hour < 12 -> "上午"
            hour == 12 -> "中午"
            hour <= 18 -> "下午"
            else -> "晚上"
        }
    }
}
