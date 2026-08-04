package com.edgecloud.device.dto;

import java.time.Instant;
import java.util.UUID;

public record DeviceHealthRecord(
        UUID deviceId,
        String deviceName,
        String deviceType,
        String ipAddress,
        String currentStatus,
        Instant latestHeartbeat,
        Instant lastUpdatedAt,
        DeviceHealthDataState dataState) {
}
