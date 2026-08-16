package com.edgecloud.device.repository;
import com.edgecloud.device.entity.DeviceGroup; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup,UUID>{List<DeviceGroup> findByProjectIdAndNameContainingIgnoreCaseOrderByNameAsc(UUID projectId,String search);boolean existsByProjectIdAndNormalisedName(UUID projectId,String name);boolean existsByProjectIdAndNormalisedNameAndIdNot(UUID projectId,String name,UUID id);Optional<DeviceGroup> findByIdAndProjectId(UUID id,UUID projectId);}
