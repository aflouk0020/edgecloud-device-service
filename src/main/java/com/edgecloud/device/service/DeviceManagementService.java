package com.edgecloud.device.service;

import com.edgecloud.device.dto.*;
import java.util.List;
import java.util.UUID;

public interface DeviceManagementService {
    DeviceManagementResponse register(DeviceManagementRequest request, UUID actor);
    DeviceManagementResponse get(UUID id);
    DeviceManagementResponse update(UUID id, DeviceManagementRequest request, UUID actor);
    DeviceManagementResponse deactivate(UUID id, UUID actor);
    DeviceManagementResponse reactivate(UUID id, UUID actor);
    void delete(UUID id, UUID actor);
    List<DeviceLifecycleHistoryResponse> history(UUID id);
}
