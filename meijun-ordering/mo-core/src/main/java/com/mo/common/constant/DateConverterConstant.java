package com.mo.common.constant;

public class DateConverterConstant {
        /**
     * yyyy-MM-dd 时间格式的正则表达式
     */
    public static final String DATE_REGEX = "[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])";

    /**
     * HH:mm:ss 时间格式的正则表达式
     */
    public static final String TIME_REGEX = "(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d";

    /**
     * yyyy-MM-dd HH:mm:ss 时间格式的正则表达式
     */
    public static final String DATE_TIME_REGEX = DATE_REGEX + "\\s" + TIME_REGEX;

    /**
     * yyyy-MM-ddTHH:mm:ss 时间格式的正则表达式
     */
    public static final String DATE_T_TIME_REGEX = DATE_REGEX + "T" + TIME_REGEX;

    /**
     * yyyy-MM-ddTHH:mm:ss.SSS 时间格式的正则表达式
     */
    public static final String DATE_T_TIME_MS_REGEX = DATE_REGEX + "T" + TIME_REGEX + "\\.\\d{3}";

    /**
     * 13位时间戳正则表达式
     */
    public static final String TIME_STAMP_REGEX = "1\\d{12}";

    /**
     * yyyy-MM 时间格式的正则表达式
     */
    public static final String YEAR_MONTH_REGEX = "[1-9]\\d{3}-(0[1-9]|1[0-2])";

    /**
     * yyyy-MM 格式
     */
    public static final String YEAR_MONTH_PATTERN = "yyyy-MM";

    /**
     * DateTime格式化字符串
     */
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * DateTime格式化字符串 ISO 格式
     */
    public static final String DEFAULT_DATETIME_ISO_PATTERN = "yyyy-MM-ddTHH:mm:ss";

    /**
     * DateTime格式化字符串 带毫秒值的 ISO 格式
     */
    public static final String DEFAULT_DATETIME_MS_ISO_PATTERN = "yyyy-MM-ddTHH:mm:ss.SSS";

    /**
     * Date格式化字符串
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Time格式化字符串
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
}
