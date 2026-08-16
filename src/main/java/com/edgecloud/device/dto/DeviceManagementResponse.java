package com.edgecloud.device.dto;

import com.edgecloud.device.entity.DeviceStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceManagementResponse(UUID deviceId, String name, String type, String ipAddress,
        String description, String location, String firmwareVersion, String operatingSystem,
        DeviceStatus status, boolean active, LocalDateTime registrationDate,
        LocalDateTime updatedAt, LocalDateTime lastSeen) {}
