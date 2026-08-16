package com.edgecloud.device.dto;import java.util.List;
public record HeartbeatHistoryResponse(List<HeartbeatHistoryItemResponse> events,int page,int size,long totalElements,int totalPages){}
