package com.edgecloud.device.dto; import java.time.LocalDateTime; import java.util.*;
public record DeviceGroupMembershipResponse(UUID groupId,UUID deviceId,LocalDateTime assignedAt,UUID assignedBy){}
