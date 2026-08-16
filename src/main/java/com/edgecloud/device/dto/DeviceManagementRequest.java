package com.edgecloud.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceManagementRequest(
        @NotBlank(message = "Device name is required") String name,
        @NotBlank(message = "Device type is required") String type,
        @NotBlank(message = "IP address is required") String ipAddress,
        @Size(max = 1000) String description,
        @Size(max = 255) String location,
        @Size(max = 100) String firmwareVersion,
        @Size(max = 100) String operatingSystem) {}
