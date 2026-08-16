package com.edgecloud.device.entity;
import jakarta.persistence.*;import java.time.LocalDateTime;import java.util.UUID;
@Entity @Table(name="device_heartbeat_history") public class DeviceHeartbeatHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private UUID deviceId;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private HeartbeatEventType eventType;
 @Column(nullable=false) private LocalDateTime heartbeatAt;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private HeartbeatStatus previousStatus;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private HeartbeatStatus resultingStatus;
 @Column(nullable=false) private int missedHeartbeats;
 @Column(nullable=false) private LocalDateTime recordedAt;
 protected DeviceHeartbeatHistory(){}
 public DeviceHeartbeatHistory(UUID deviceId,HeartbeatEventType type,LocalDateTime at,HeartbeatStatus previous,HeartbeatStatus resulting,int missed){this.deviceId=deviceId;eventType=type;heartbeatAt=at;previousStatus=previous;resultingStatus=resulting;missedHeartbeats=missed;recordedAt=LocalDateTime.now();}
 public Long getId(){return id;} public UUID getDeviceId(){return deviceId;} public HeartbeatEventType getEventType(){return eventType;} public LocalDateTime getHeartbeatAt(){return heartbeatAt;} public HeartbeatStatus getPreviousStatus(){return previousStatus;} public HeartbeatStatus getResultingStatus(){return resultingStatus;} public int getMissedHeartbeats(){return missedHeartbeats;} public LocalDateTime getRecordedAt(){return recordedAt;}
}
