package com.edgecloud.device.repository;

import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EdgeDeviceRepository extends JpaRepository<EdgeDevice, UUID>, JpaSpecificationExecutor<EdgeDevice> {
    boolean existsByDeviceName(String deviceName);
    boolean existsByDeviceNameAndIdNot(String deviceName, UUID id);
    Optional<EdgeDevice> findByDeviceName(String deviceName);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from EdgeDevice d where d.id = :id")
    Optional<EdgeDevice> findByIdForUpdate(UUID id);
    long countByStatus(DeviceStatus status);

    Page<EdgeDevice> findByDeviceNameContainingIgnoreCase(String deviceName, Pageable pageable);
    Page<EdgeDevice> findByIdOrDeviceNameContainingIgnoreCase(UUID id, String deviceName, Pageable pageable);
}
