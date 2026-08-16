package com.edgecloud.device.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name="device_configurations")
public class DeviceConfiguration {
    @Id private UUID deviceId;
    private int pollingIntervalSeconds;
    private int heartbeatIntervalSeconds;
    private int heartbeatTimeoutSeconds;
    private int metricsCollectionIntervalSeconds;
    @Enumerated(EnumType.STRING) private DeviceEnvironment environment;
    @Column(length=500) private String apiEndpoint;
    @Enumerated(EnumType.STRING) private DeviceLoggingLevel loggingLevel;
    @Column(nullable=false,length=2000) private String tagsJson;
    private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    protected DeviceConfiguration() {}
    public DeviceConfiguration(UUID deviceId) { this.deviceId=deviceId; }
    public UUID getDeviceId(){return deviceId;} public int getPollingIntervalSeconds(){return pollingIntervalSeconds;} public void setPollingIntervalSeconds(int v){pollingIntervalSeconds=v;}
    public int getHeartbeatIntervalSeconds(){return heartbeatIntervalSeconds;} public void setHeartbeatIntervalSeconds(int v){heartbeatIntervalSeconds=v;}
    public int getHeartbeatTimeoutSeconds(){return heartbeatTimeoutSeconds;} public void setHeartbeatTimeoutSeconds(int v){heartbeatTimeoutSeconds=v;}
    public int getMetricsCollectionIntervalSeconds(){return metricsCollectionIntervalSeconds;} public void setMetricsCollectionIntervalSeconds(int v){metricsCollectionIntervalSeconds=v;}
    public DeviceEnvironment getEnvironment(){return environment;} public void setEnvironment(DeviceEnvironment v){environment=v;}
    public String getApiEndpoint(){return apiEndpoint;} public void setApiEndpoint(String v){apiEndpoint=v;}
    public DeviceLoggingLevel getLoggingLevel(){return loggingLevel;} public void setLoggingLevel(DeviceLoggingLevel v){loggingLevel=v;}
    public String getTagsJson(){return tagsJson;} public void setTagsJson(String v){tagsJson=v;}
    public long getVersion(){return version;} public void setVersion(long v){version=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
    public UUID getUpdatedBy(){return updatedBy;} public void setUpdatedBy(UUID v){updatedBy=v;}
}
