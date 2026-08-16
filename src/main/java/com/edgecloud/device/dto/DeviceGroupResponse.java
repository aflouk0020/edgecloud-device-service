package com.edgecloud.device.dto; import java.time.LocalDateTime; import java.util.*;
public record DeviceGroupResponse(UUID id,UUID projectId,String name,String description,long memberCount,LocalDateTime createdAt,LocalDateTime updatedAt,UUID createdBy,UUID updatedBy){}
