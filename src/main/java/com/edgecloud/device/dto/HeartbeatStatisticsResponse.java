package com.edgecloud.device.dto;import java.util.UUID;
public record HeartbeatStatisticsResponse(UUID deviceId,long totalRecorded,long receivedLast24Hours,long recoveryCount,long offlineTransitionCount){}
