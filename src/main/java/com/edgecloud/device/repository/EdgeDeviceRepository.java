package com.edgecloud.device.repository;

import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EdgeDeviceRepository extends JpaRepository<EdgeDevice, UUID> {
    boolean existsByDeviceName(String deviceName);
    Optional<EdgeDevice> findByDeviceName(String deviceName);
    long countByStatus(DeviceStatus status);
}
