package com.edgecloud.device.repository;

import com.edgecloud.device.entity.DeviceMaintenanceHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceMaintenanceHistoryRepository extends JpaRepository<DeviceMaintenanceHistory, Long> {
    List<DeviceMaintenanceHistory> findByDeviceIdOrderByOccurredAtDescIdDesc(UUID deviceId);
}
