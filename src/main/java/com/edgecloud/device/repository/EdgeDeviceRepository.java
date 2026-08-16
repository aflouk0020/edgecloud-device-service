package com.edgecloud.device.repository;

import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EdgeDeviceRepository extends JpaRepository<EdgeDevice, UUID>, JpaSpecificationExecutor<EdgeDevice> {
    boolean existsByDeviceName(String deviceName);
    boolean existsByDeviceNameAndIdNot(String deviceName, UUID id);
    Optional<EdgeDevice> findByDeviceName(String deviceName);
    long countByStatus(DeviceStatus status);

    Page<EdgeDevice> findByDeviceNameContainingIgnoreCase(String deviceName, Pageable pageable);
    Page<EdgeDevice> findByIdOrDeviceNameContainingIgnoreCase(UUID id, String deviceName, Pageable pageable);
}
