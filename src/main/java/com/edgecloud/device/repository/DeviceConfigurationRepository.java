package com.edgecloud.device.repository;
import com.edgecloud.device.entity.DeviceConfiguration;
import java.util.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
public interface DeviceConfigurationRepository extends JpaRepository<DeviceConfiguration,UUID> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from DeviceConfiguration c where c.deviceId=:id") Optional<DeviceConfiguration> findForUpdate(UUID id);
}
