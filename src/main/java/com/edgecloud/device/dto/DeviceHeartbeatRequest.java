package com.edgecloud.device.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceHeartbeatRequest(
        @NotNull(message = "Device ID is required")
        UUID deviceId,

        LocalDateTime timestamp
) {
}
