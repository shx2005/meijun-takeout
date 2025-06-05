package com.mo.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mo.mp")
public class MpProperties {
    private String appid;
    private String secret;
}
