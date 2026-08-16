package com.edgecloud.device.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name="device_configuration_versions", uniqueConstraints=@UniqueConstraint(columnNames={"device_id","version"}))
public class DeviceConfigurationVersion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private UUID deviceId; private long version;
    private int pollingIntervalSeconds; private int heartbeatIntervalSeconds; private int heartbeatTimeoutSeconds; private int metricsCollectionIntervalSeconds;
    @Enumerated(EnumType.STRING) private DeviceEnvironment environment;
    @Column(length=500) private String apiEndpoint;
    @Enumerated(EnumType.STRING) private DeviceLoggingLevel loggingLevel;
    @Column(nullable=false,length=2000) private String tagsJson;
    @Column(nullable=false,length=32) private String action;
    @Column(nullable=false,length=1000) private String changedFields;
    private UUID changedBy; private LocalDateTime changedAt;
    protected DeviceConfigurationVersion() {}
    public DeviceConfigurationVersion(DeviceConfiguration c,String action,String fields,UUID actor){deviceId=c.getDeviceId();version=c.getVersion();pollingIntervalSeconds=c.getPollingIntervalSeconds();heartbeatIntervalSeconds=c.getHeartbeatIntervalSeconds();heartbeatTimeoutSeconds=c.getHeartbeatTimeoutSeconds();metricsCollectionIntervalSeconds=c.getMetricsCollectionIntervalSeconds();environment=c.getEnvironment();apiEndpoint=c.getApiEndpoint();loggingLevel=c.getLoggingLevel();tagsJson=c.getTagsJson();this.action=action;changedFields=fields;changedBy=actor;changedAt=LocalDateTime.now();}
    public Long getId(){return id;} public UUID getDeviceId(){return deviceId;} public long getVersion(){return version;} public int getPollingIntervalSeconds(){return pollingIntervalSeconds;} public int getHeartbeatIntervalSeconds(){return heartbeatIntervalSeconds;} public int getMetricsCollectionIntervalSeconds(){return metricsCollectionIntervalSeconds;} public DeviceEnvironment getEnvironment(){return environment;} public String getApiEndpoint(){return apiEndpoint;} public DeviceLoggingLevel getLoggingLevel(){return loggingLevel;} public String getTagsJson(){return tagsJson;} public String getAction(){return action;} public String getChangedFields(){return changedFields;} public UUID getChangedBy(){return changedBy;} public LocalDateTime getChangedAt(){return changedAt;}
    public int getHeartbeatTimeoutSeconds(){return heartbeatTimeoutSeconds;}
}
