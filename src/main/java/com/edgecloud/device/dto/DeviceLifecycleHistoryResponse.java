package com.edgecloud.device.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceLifecycleHistoryResponse(Long id, UUID deviceId, String action,
        LocalDateTime occurredAt, UUID actorUserId, String deviceName, boolean active, String details) {}
