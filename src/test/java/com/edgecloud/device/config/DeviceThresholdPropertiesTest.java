package com.edgecloud.device.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceThresholdPropertiesTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory()
                    .getValidator();

    @Test
    void acceptsPositiveOfflineThreshold() {
        DeviceThresholdProperties properties =
                new DeviceThresholdProperties(60);

        assertThat(validator.validate(properties)).isEmpty();
    }

    @Test
    void rejectsZeroOfflineThreshold() {
        DeviceThresholdProperties properties =
                new DeviceThresholdProperties(0);

        assertThat(validator.validate(properties))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly(
                        "Device offline threshold must be greater than zero"
                );
    }

    @Test
    void rejectsNegativeOfflineThreshold() {
        DeviceThresholdProperties properties =
                new DeviceThresholdProperties(-10);

        assertThat(validator.validate(properties))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly(
                        "Device offline threshold must be greater than zero"
                );
    }
}
