package com.edgecloud.device.repository;
import com.edgecloud.device.entity.DeviceTagAssignment; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceTagAssignmentRepository extends JpaRepository<DeviceTagAssignment,DeviceTagAssignment.Id>{List<DeviceTagAssignment> findByDeviceIdOrderByAssignedAtAsc(UUID deviceId);List<DeviceTagAssignment> findByDeviceIdIn(Collection<UUID> deviceIds);void deleteByTagIdAndDeviceId(UUID tagId,UUID deviceId);void deleteByTagId(UUID tagId);}
