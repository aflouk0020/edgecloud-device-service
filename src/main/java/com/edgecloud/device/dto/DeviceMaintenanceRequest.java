package com.edgecloud.device.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record DeviceMaintenanceRequest(
        @Size(max = 500) String reason,
        LocalDateTime scheduledEndAt) {}
