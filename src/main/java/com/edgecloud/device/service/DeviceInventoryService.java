package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceInventoryResponse;

public interface DeviceInventoryService {
    DeviceInventoryResponse getInventory(String search, int page, int size, String sort, String direction);
}
