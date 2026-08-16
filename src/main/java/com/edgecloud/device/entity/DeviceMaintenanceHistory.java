package com.edgecloud.device.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_maintenance_history")
public class DeviceMaintenanceHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private UUID deviceId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private DeviceMaintenanceAction action;
    @Column(nullable = false)
    private LocalDateTime occurredAt;
    private UUID actorUserId;
    @Column(length = 500)
    private String reason;
    private LocalDateTime scheduledEndAt;

    protected DeviceMaintenanceHistory() {}

    public DeviceMaintenanceHistory(UUID deviceId, DeviceMaintenanceAction action, LocalDateTime occurredAt,
                                    UUID actorUserId, String reason, LocalDateTime scheduledEndAt) {
        this.deviceId = deviceId;
        this.action = action;
        this.occurredAt = occurredAt;
        this.actorUserId = actorUserId;
        this.reason = reason;
        this.scheduledEndAt = scheduledEndAt;
    }

    public Long getId() { return id; }
    public UUID getDeviceId() { return deviceId; }
    public DeviceMaintenanceAction getAction() { return action; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public UUID getActorUserId() { return actorUserId; }
    public String getReason() { return reason; }
    public LocalDateTime getScheduledEndAt() { return scheduledEndAt; }
}
