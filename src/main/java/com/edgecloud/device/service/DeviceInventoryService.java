package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceInventoryResponse;
import java.util.*;

public interface DeviceInventoryService {
    DeviceInventoryResponse getInventory(String search, int page, int size, String sort, String direction);
    DeviceInventoryResponse getInventory(String search,int page,int size,String sort,String direction,UUID projectId,UUID groupId,List<UUID> tagIds,String bearerToken);
}
