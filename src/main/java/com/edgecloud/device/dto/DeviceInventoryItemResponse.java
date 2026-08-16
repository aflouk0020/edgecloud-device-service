package com.edgecloud.device.dto;

import com.edgecloud.device.entity.DeviceStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeviceInventoryItemResponse(
        UUID deviceId,
        String name,
        String type,
        DeviceStatus operationalStatus,
        String heartbeatStatus,
        LocalDateTime latestHeartbeat,
        String firmwareVersion,
        String assignedProject,
        LocalDateTime registrationDate,
        LocalDateTime lastSeen,
        List<String> tags,
        String location) {
}
