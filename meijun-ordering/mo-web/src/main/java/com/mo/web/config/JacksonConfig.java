package com.mo.web.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // LocalDate 和 LocalDateTime 的格式化方式
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            // 设置 LocalDate 和 LocalDateTime 反序列化器
            builder.deserializers(new LocalDateDeserializer(dateFormatter));
            builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
            builder.deserializers(new LocalDateTimeDeserializer(timeFormatter));

            // 设置 LocalDate 和 LocalDateTime 序列化器
            builder.serializers(new LocalDateSerializer(dateFormatter));
            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
            builder.serializers(new LocalDateTimeSerializer(timeFormatter));
        };
    }
}
