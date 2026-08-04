package com.edgecloud.device.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "edgecloud.device")
@Validated
public record DeviceThresholdProperties(

        @Min(
                value = 1,
                message = "Device offline threshold must be greater than zero"
        )
        long offlineThresholdSeconds

) {
}
