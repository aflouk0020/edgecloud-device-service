package com.edgecloud.device.dto;

import java.util.List;

public record DeviceInventoryResponse(
        List<DeviceInventoryItemResponse> devices,
        int page,
        int size,
        long totalElements,
        int totalPages,
        String sort,
        String direction) {
}
