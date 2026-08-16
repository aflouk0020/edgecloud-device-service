package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceInventoryResponse;
import java.util.*;
import com.edgecloud.device.entity.HeartbeatStatus;

public interface DeviceInventoryService {
    DeviceInventoryResponse getInventory(String search, int page, int size, String sort, String direction);
    DeviceInventoryResponse getInventory(String search,int page,int size,String sort,String direction,UUID projectId,UUID groupId,List<UUID> tagIds,String bearerToken);
    DeviceInventoryResponse getInventory(String search,int page,int size,String sort,String direction,UUID projectId,UUID groupId,List<UUID> tagIds,HeartbeatStatus heartbeatStatus,String bearerToken);
}
