package com.edgecloud.device.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "edge_devices")
public class EdgeDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String deviceName;

    @Column(nullable = false)
    private String deviceType;

    @Column(nullable = false)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime lastHeartbeat;
    private LocalDateTime lastRecoveryAt;
    private LocalDateTime onlineSince;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private HeartbeatStatus heartbeatState=HeartbeatStatus.UNKNOWN;
    @Column(nullable=false) private int consecutiveMissedHeartbeats;

    @Column(length = 1000)
    private String description;

    @Column(name = "physical_location")
    private String physicalLocation;

    @Column(name = "firmware_version", length = 100)
    private String firmwareVersion;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.status = DeviceStatus.OFFLINE;
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = this.registeredAt;
        this.active = true;
    }

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    public UUID getId() { return id; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public DeviceStatus getStatus() { return status; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    public LocalDateTime getLastRecoveryAt(){return lastRecoveryAt;} public void setLastRecoveryAt(LocalDateTime v){lastRecoveryAt=v;}
    public LocalDateTime getOnlineSince(){return onlineSince;} public void setOnlineSince(LocalDateTime v){onlineSince=v;}
    public HeartbeatStatus getHeartbeatState(){return heartbeatState;} public void setHeartbeatState(HeartbeatStatus v){heartbeatState=v;}
    public int getConsecutiveMissedHeartbeats(){return consecutiveMissedHeartbeats;} public void setConsecutiveMissedHeartbeats(int v){consecutiveMissedHeartbeats=v;}
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPhysicalLocation() { return physicalLocation; }
    public void setPhysicalLocation(String physicalLocation) { this.physicalLocation = physicalLocation; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
