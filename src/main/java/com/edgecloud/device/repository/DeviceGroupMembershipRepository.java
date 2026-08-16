package com.edgecloud.device.repository;
import com.edgecloud.device.entity.DeviceGroupMembership; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceGroupMembershipRepository extends JpaRepository<DeviceGroupMembership,DeviceGroupMembership.Id>{long countByGroupId(UUID groupId);List<DeviceGroupMembership> findByGroupIdOrderByAssignedAtAsc(UUID groupId);List<DeviceGroupMembership> findByDeviceIdIn(Collection<UUID> deviceIds);void deleteByGroupIdAndDeviceId(UUID groupId,UUID deviceId);}
