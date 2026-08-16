package com.edgecloud.device.repository;

import com.edgecloud.device.entity.DeviceLifecycleHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceLifecycleHistoryRepository extends JpaRepository<DeviceLifecycleHistory, Long> {
    List<DeviceLifecycleHistory> findByDeviceIdOrderByOccurredAtDesc(UUID deviceId);
}
