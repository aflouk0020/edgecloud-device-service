package com.edgecloud.device.dto;

public record DeviceSummaryResponse(
        long totalDevices,
        long onlineDevices,
        long offlineDevices,
        double availabilityPercentage
) {
}
