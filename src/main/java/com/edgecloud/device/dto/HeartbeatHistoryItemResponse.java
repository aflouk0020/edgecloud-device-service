package com.edgecloud.device.dto;import com.edgecloud.device.entity.*;import java.time.LocalDateTime;
public record HeartbeatHistoryItemResponse(long id,HeartbeatEventType eventType,LocalDateTime heartbeatAt,HeartbeatStatus previousStatus,HeartbeatStatus resultingStatus,int missedHeartbeats,LocalDateTime recordedAt){}
