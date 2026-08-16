package com.edgecloud.device.dto;
import com.edgecloud.device.entity.*;
import java.time.LocalDateTime;
import java.util.*;
public record DeviceConfigurationTemplateResponse(UUID id,String name,String description,int pollingIntervalSeconds,int heartbeatIntervalSeconds,int metricsCollectionIntervalSeconds,DeviceEnvironment environment,String apiEndpoint,DeviceLoggingLevel loggingLevel,List<String> tags,LocalDateTime createdAt,LocalDateTime updatedAt,UUID createdBy,UUID updatedBy) {}
