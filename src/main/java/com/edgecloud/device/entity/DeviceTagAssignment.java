package com.edgecloud.device.entity;
import jakarta.persistence.*; import java.io.Serializable; import java.time.LocalDateTime; import java.util.*;
@Entity @Table(name="device_tag_assignments") @IdClass(DeviceTagAssignment.Id.class) public class DeviceTagAssignment {
 @jakarta.persistence.Id private UUID tagId; @jakarta.persistence.Id private UUID deviceId; private LocalDateTime assignedAt; private UUID assignedBy;
 protected DeviceTagAssignment(){} public DeviceTagAssignment(UUID tagId,UUID deviceId,UUID actor){this.tagId=tagId;this.deviceId=deviceId;assignedBy=actor;assignedAt=LocalDateTime.now();}
 public UUID getTagId(){return tagId;} public UUID getDeviceId(){return deviceId;} public LocalDateTime getAssignedAt(){return assignedAt;} public UUID getAssignedBy(){return assignedBy;}
 public static class Id implements Serializable { private UUID tagId; private UUID deviceId; public Id(){} public Id(UUID t,UUID d){tagId=t;deviceId=d;} @Override public boolean equals(Object o){return o instanceof Id i&&Objects.equals(tagId,i.tagId)&&Objects.equals(deviceId,i.deviceId);}@Override public int hashCode(){return Objects.hash(tagId,deviceId);} }
}
