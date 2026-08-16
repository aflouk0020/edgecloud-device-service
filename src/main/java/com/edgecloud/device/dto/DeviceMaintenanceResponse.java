package com.edgecloud.device.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceMaintenanceResponse(
        UUID deviceId,
        boolean maintenanceMode,
        String reason,
        LocalDateTime enabledAt,
        UUID enabledBy,
        LocalDateTime scheduledEndAt,
        LocalDateTime disabledAt,
        UUID disabledBy) {}
