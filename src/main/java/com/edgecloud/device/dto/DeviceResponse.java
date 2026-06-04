package com.edgecloud.device.dto;

import com.edgecloud.device.entity.DeviceStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String deviceName,
        String deviceType,
        String ipAddress,
        DeviceStatus status,
        LocalDateTime registeredAt,
        LocalDateTime lastHeartbeat
) {
}
