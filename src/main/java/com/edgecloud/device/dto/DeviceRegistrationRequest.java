package com.edgecloud.device.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceRegistrationRequest(
        @NotBlank(message = "Device name is required")
        String deviceName,

        @NotBlank(message = "Device type is required")
        String deviceType,

        @NotBlank(message = "IP address is required")
        String ipAddress
) {
}
