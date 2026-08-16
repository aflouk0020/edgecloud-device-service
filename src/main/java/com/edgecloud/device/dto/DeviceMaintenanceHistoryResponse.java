package com.edgecloud.device.dto;

import com.edgecloud.device.entity.DeviceMaintenanceAction;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceMaintenanceHistoryResponse(
        Long id,
        UUID deviceId,
        DeviceMaintenanceAction action,
        LocalDateTime occurredAt,
        UUID actorUserId,
        String reason,
        LocalDateTime scheduledEndAt) {}
