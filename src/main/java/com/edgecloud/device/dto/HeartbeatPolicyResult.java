package com.edgecloud.device.dto; import com.edgecloud.device.entity.HeartbeatStatus; import java.time.*;
public record HeartbeatPolicyResult(HeartbeatStatus status,LocalDateTime nextExpectedHeartbeat,int consecutiveMissedHeartbeats,Duration connectionDuration){}
