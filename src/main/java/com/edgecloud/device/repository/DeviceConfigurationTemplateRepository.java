package com.edgecloud.device.repository;
import com.edgecloud.device.entity.DeviceConfigurationTemplate;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceConfigurationTemplateRepository extends JpaRepository<DeviceConfigurationTemplate,UUID>{ boolean existsByNameIgnoreCase(String name); boolean existsByNameIgnoreCaseAndIdNot(String name,UUID id); List<DeviceConfigurationTemplate> findAllByOrderByNameAsc(); }
