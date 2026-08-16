package com.edgecloud.device.service; import java.util.UUID;
public interface ProjectScopeClient { void requireProjectAccess(UUID projectId,String bearerToken); void requireDeviceAssociation(UUID projectId,UUID deviceId,String bearerToken); java.util.Set<UUID> projectDeviceIds(UUID projectId,String bearerToken); }
