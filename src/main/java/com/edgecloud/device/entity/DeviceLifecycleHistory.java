package com.edgecloud.device.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_lifecycle_history")
public class DeviceLifecycleHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private UUID deviceId;
    @Column(nullable = false, length = 32) private String action;
    @Column(nullable = false) private LocalDateTime occurredAt;
    @Column(nullable = false) private UUID actorUserId;
    @Column(nullable = false) private String deviceName;
    @Column(nullable = false) private boolean active;
    @Column(length = 1000) private String details;

    protected DeviceLifecycleHistory() {}
    public DeviceLifecycleHistory(UUID deviceId, String action, UUID actorUserId, String deviceName,
                                  boolean active, String details) {
        this.deviceId = deviceId; this.action = action; this.actorUserId = actorUserId;
        this.deviceName = deviceName; this.active = active; this.details = details;
        this.occurredAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public UUID getDeviceId() { return deviceId; }
    public String getAction() { return action; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public UUID getActorUserId() { return actorUserId; }
    public String getDeviceName() { return deviceName; }
    public boolean isActive() { return active; }
    public String getDetails() { return details; }
}
