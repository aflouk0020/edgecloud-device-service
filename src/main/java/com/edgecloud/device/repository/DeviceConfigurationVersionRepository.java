package com.edgecloud.device.repository;
import com.edgecloud.device.entity.DeviceConfigurationVersion;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceConfigurationVersionRepository extends JpaRepository<DeviceConfigurationVersion,Long>{ List<DeviceConfigurationVersion> findByDeviceIdOrderByVersionDesc(UUID id); Optional<DeviceConfigurationVersion> findByDeviceIdAndVersion(UUID id,long version); }
