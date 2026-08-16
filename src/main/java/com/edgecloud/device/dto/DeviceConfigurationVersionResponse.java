package com.edgecloud.device.dto;
import com.edgecloud.device.entity.*;
import java.time.LocalDateTime;
import java.util.*;
public record DeviceConfigurationVersionResponse(long version,int pollingIntervalSeconds,int heartbeatIntervalSeconds,int heartbeatTimeoutSeconds,int metricsCollectionIntervalSeconds,DeviceEnvironment environment,String apiEndpoint,DeviceLoggingLevel loggingLevel,List<String> tags,String action,List<String> changedFields,UUID changedBy,LocalDateTime changedAt) {}
