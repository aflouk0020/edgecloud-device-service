package com.edgecloud.device.entity;
import jakarta.persistence.*; import java.time.LocalDateTime; import java.util.UUID;
@Entity @Table(name="device_groups") public class DeviceGroup {
 @Id private UUID id; private UUID projectId; @Column(length=100) private String name; @Column(length=100) private String normalisedName; @Column(length=500) private String description; private LocalDateTime createdAt; private LocalDateTime updatedAt; private UUID createdBy; private UUID updatedBy;
 protected DeviceGroup(){} public DeviceGroup(UUID id,UUID projectId){this.id=id;this.projectId=projectId;}
 public UUID getId(){return id;} public UUID getProjectId(){return projectId;} public String getName(){return name;} public void setName(String v){name=v;} public String getNormalisedName(){return normalisedName;} public void setNormalisedName(String v){normalisedName=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;} public UUID getCreatedBy(){return createdBy;} public void setCreatedBy(UUID v){createdBy=v;} public UUID getUpdatedBy(){return updatedBy;} public void setUpdatedBy(UUID v){updatedBy=v;}
}
