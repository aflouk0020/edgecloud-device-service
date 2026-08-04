package com.edgecloud.device.dto;

import java.time.Instant;
import java.util.List;

public record DeviceHealthResponse(
        Instant generatedAt,
        List<DeviceHealthRecord> devices) {
}
