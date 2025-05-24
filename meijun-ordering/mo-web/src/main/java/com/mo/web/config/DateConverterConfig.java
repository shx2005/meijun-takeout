package com.mo.web.config;

import com.mo.common.constant.DateConverterConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Configuration
public class DateConverterConfig {

    private static final Map<String, DateTimeFormatter> FORMATTER_MAP = Map.of(
        DateConverterConstant.DEFAULT_DATETIME_PATTERN, DateTimeFormatter.ofPattern(DateConverterConstant.DEFAULT_DATETIME_PATTERN).withZone(ZoneId.of("GMT")),
        DateConverterConstant.DEFAULT_DATETIME_ISO_PATTERN, DateTimeFormatter.ofPattern(DateConverterConstant.DEFAULT_DATETIME_ISO_PATTERN).withZone(ZoneId.of("GMT")),
        DateConverterConstant.DEFAULT_DATETIME_MS_ISO_PATTERN, DateTimeFormatter.ofPattern(DateConverterConstant.DEFAULT_DATETIME_MS_ISO_PATTERN).withZone(ZoneId.of("GMT")),
        DateConverterConstant.DEFAULT_DATE_FORMAT, DateTimeFormatter.ofPattern(DateConverterConstant.DEFAULT_DATE_FORMAT).withZone(ZoneId.of("GMT")),
        DateConverterConstant.DEFAULT_TIME_FORMAT, DateTimeFormatter.ofPattern(DateConverterConstant.DEFAULT_TIME_FORMAT).withZone(ZoneId.of("GMT")),
        DateConverterConstant.YEAR_MONTH_PATTERN, DateTimeFormatter.ofPattern(DateConverterConstant.YEAR_MONTH_PATTERN).withZone(ZoneId.of("GMT"))
    );

    @Bean
    public Converter<String, Date> dateConverter() {
        return source -> {
            if (source == null || source.isEmpty()) {
                return null;
            }
            try {
                if (source.matches(DateConverterConstant.TIME_STAMP_REGEX)) {
                    return new Date(Long.parseLong(source));
                }
                if (source.matches(DateConverterConstant.DATE_TIME_REGEX)) {
                    Instant instant = Instant.from(FORMATTER_MAP.get(DateConverterConstant.DEFAULT_DATETIME_PATTERN).parse(source));
                    return Date.from(instant);
                }
                if (source.matches(DateConverterConstant.DATE_REGEX)) {
                    Instant instant = LocalDate.parse(source, FORMATTER_MAP.get(DateConverterConstant.DEFAULT_DATE_FORMAT)).atStartOfDay(ZoneId.of("GMT")).toInstant();
                    return Date.from(instant);
                }
                if (source.matches(DateConverterConstant.YEAR_MONTH_REGEX)) {
                    LocalDate localDate = LocalDate.parse(source + "-01", FORMATTER_MAP.get(DateConverterConstant.YEAR_MONTH_PATTERN).withLocale(java.util.Locale.getDefault()));
                    Instant instant = localDate.atStartOfDay(ZoneId.of("GMT")).toInstant();
                    return Date.from(instant);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        };
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return source -> {
            if (source == null || source.isEmpty()) {
                return null;
            }
            try {
                if (source.matches(DateConverterConstant.TIME_STAMP_REGEX)) {
                    Instant instant = Instant.ofEpochMilli(Long.parseLong(source));
                    ZoneId zoneId = ZoneId.systemDefault();
                    return LocalDateTime.ofInstant(instant, zoneId);
                }
                if (source.matches(DateConverterConstant.DATE_TIME_REGEX)) {
                    return LocalDateTime.parse(source, FORMATTER_MAP.get(DateConverterConstant.DEFAULT_DATETIME_PATTERN));
                }
                if (source.matches(DateConverterConstant.DATE_T_TIME_REGEX)) {
                    return LocalDateTime.parse(source, FORMATTER_MAP.get(DateConverterConstant.DEFAULT_DATETIME_ISO_PATTERN));
                }
                if (source.matches(DateConverterConstant.DATE_T_TIME_MS_REGEX)) {
                    return LocalDateTime.parse(source, FORMATTER_MAP.get(DateConverterConstant.DEFAULT_DATETIME_MS_ISO_PATTERN));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        };
    }

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return source -> {
            if (source == null || source.isEmpty()) {
                return null;
            }
            try {
                if (source.matches(DateConverterConstant.TIME_STAMP_REGEX)) {
                    Instant instant = Instant.ofEpochMilli(Long.parseLong(source));
                    ZoneId zone = ZoneId.systemDefault();
                    return LocalDateTime.ofInstant(instant, zone).toLocalDate();
                }
                if (source.matches(DateConverterConstant.DATE_REGEX)) {
                    return LocalDate.parse(source, FORMATTER_MAP.get(DateConverterConstant.DEFAULT_DATE_FORMAT));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        };
    }

    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return source -> {
            if (source == null || source.isEmpty()) {
                return null;
            }
            return LocalTime.parse(source, FORMATTER_MAP.get(DateConverterConstant.DEFAULT_TIME_FORMAT));
        };
    }

}
