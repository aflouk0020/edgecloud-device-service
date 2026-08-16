package com.edgecloud.device.dto;

import com.edgecloud.device.entity.DeviceStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeviceInventoryItemResponse(
        UUID deviceId,
        String name,
        String type,
        String ipAddress,
        DeviceStatus operationalStatus,
        String heartbeatStatus,
        LocalDateTime latestHeartbeat,
        LocalDateTime nextExpectedHeartbeat,
        int heartbeatIntervalSeconds,
        int heartbeatTimeoutSeconds,
        int consecutiveMissedHeartbeats,
        LocalDateTime lastRecoveryAt,
        Long connectionDurationSeconds,
        String firmwareVersion,
        String assignedProject,
        LocalDateTime registrationDate,
        LocalDateTime lastSeen,
        List<String> tags,
        List<DeviceOrganisationLabelResponse> groups,
        String location,
        String description,
        String operatingSystem,
        boolean active,
        boolean maintenanceMode,
        String maintenanceReason,
        LocalDateTime maintenanceEnabledAt,
        UUID maintenanceEnabledBy,
        LocalDateTime maintenanceScheduledEndAt,
        LocalDateTime updatedAt) {
}
