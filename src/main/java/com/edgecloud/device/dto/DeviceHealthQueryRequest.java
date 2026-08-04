package com.edgecloud.device.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record DeviceHealthQueryRequest(
        @NotNull(message = "Device IDs are required")
        List<UUID> deviceIds) {
}
