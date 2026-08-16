package com.edgecloud.device.entity;
import jakarta.persistence.*; import java.io.Serializable; import java.time.LocalDateTime; import java.util.*;
@Entity @Table(name="device_group_memberships") @IdClass(DeviceGroupMembership.Id.class) public class DeviceGroupMembership {
 @jakarta.persistence.Id private UUID groupId; @jakarta.persistence.Id private UUID deviceId; private LocalDateTime assignedAt; private UUID assignedBy;
 protected DeviceGroupMembership(){} public DeviceGroupMembership(UUID groupId,UUID deviceId,UUID actor){this.groupId=groupId;this.deviceId=deviceId;assignedBy=actor;assignedAt=LocalDateTime.now();}
 public UUID getGroupId(){return groupId;} public UUID getDeviceId(){return deviceId;} public LocalDateTime getAssignedAt(){return assignedAt;} public UUID getAssignedBy(){return assignedBy;}
 public static class Id implements Serializable { private UUID groupId; private UUID deviceId; public Id(){} public Id(UUID g,UUID d){groupId=g;deviceId=d;} @Override public boolean equals(Object o){return o instanceof Id i&&Objects.equals(groupId,i.groupId)&&Objects.equals(deviceId,i.deviceId);}@Override public int hashCode(){return Objects.hash(groupId,deviceId);} }
}
