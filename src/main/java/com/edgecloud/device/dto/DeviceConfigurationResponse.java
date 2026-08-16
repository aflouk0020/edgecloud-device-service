package com.edgecloud.device.dto;
import com.edgecloud.device.entity.*;
import java.time.LocalDateTime;
import java.util.*;
public record DeviceConfigurationResponse(UUID deviceId,int pollingIntervalSeconds,int heartbeatIntervalSeconds,int heartbeatTimeoutSeconds,int metricsCollectionIntervalSeconds,DeviceEnvironment environment,String apiEndpoint,DeviceLoggingLevel loggingLevel,List<String> tags,long version,LocalDateTime createdAt,LocalDateTime updatedAt,UUID updatedBy,String distributionStatus) {}
