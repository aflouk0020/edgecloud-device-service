package com.edgecloud.device.service;

import com.edgecloud.device.dto.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceMaintenanceService {
    DeviceMaintenanceResponse get(UUID deviceId);
    DeviceMaintenanceResponse enable(UUID deviceId, DeviceMaintenanceRequest request, UUID actor, LocalDateTime now);
    DeviceMaintenanceResponse disable(UUID deviceId, UUID actor, LocalDateTime now);
    List<DeviceMaintenanceHistoryResponse> history(UUID deviceId);
    void expireScheduled(LocalDateTime now);
}
