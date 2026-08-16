package com.edgecloud.device.dto; import java.time.LocalDateTime; import java.util.*;
public record DeviceTagResponse(UUID id,UUID projectId,String name,String description,LocalDateTime createdAt,LocalDateTime updatedAt,UUID createdBy,UUID updatedBy){}
