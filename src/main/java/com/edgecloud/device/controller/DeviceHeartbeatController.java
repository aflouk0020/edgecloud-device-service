package com.edgecloud.device.controller;
import com.edgecloud.device.dto.*;import com.edgecloud.device.security.EdgeCloudJwtAuthenticationToken;import com.edgecloud.device.service.*;import java.time.LocalDateTime;import java.util.UUID;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/management") public class DeviceHeartbeatController {
 private final DeviceHeartbeatService service;private final ProjectScopeClient projects;public DeviceHeartbeatController(DeviceHeartbeatService s,ProjectScopeClient p){service=s;projects=p;}
 @GetMapping("/{deviceId}/heartbeat") public HeartbeatStateResponse state(@PathVariable UUID deviceId){return service.state(deviceId,LocalDateTime.now());}
 @GetMapping("/{deviceId}/heartbeat/history") public HeartbeatHistoryResponse history(@PathVariable UUID deviceId,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return service.history(deviceId,page,size);}
 @GetMapping("/{deviceId}/heartbeat/statistics") public HeartbeatStatisticsResponse statistics(@PathVariable UUID deviceId){return service.statistics(deviceId);}
 @GetMapping("/projects/{projectId}/devices/{deviceId}/heartbeat") public HeartbeatStateResponse projectState(@PathVariable UUID projectId,@PathVariable UUID deviceId,EdgeCloudJwtAuthenticationToken auth){scope(projectId,deviceId,auth);return service.state(deviceId,LocalDateTime.now());}
 @GetMapping("/projects/{projectId}/devices/{deviceId}/heartbeat/history") public HeartbeatHistoryResponse projectHistory(@PathVariable UUID projectId,@PathVariable UUID deviceId,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size,EdgeCloudJwtAuthenticationToken auth){scope(projectId,deviceId,auth);return service.history(deviceId,page,size);}
 @GetMapping("/projects/{projectId}/devices/{deviceId}/heartbeat/statistics") public HeartbeatStatisticsResponse projectStatistics(@PathVariable UUID projectId,@PathVariable UUID deviceId,EdgeCloudJwtAuthenticationToken auth){scope(projectId,deviceId,auth);return service.statistics(deviceId);}
 private void scope(UUID p,UUID d,EdgeCloudJwtAuthenticationToken a){projects.requireDeviceAssociation(p,d,(String)a.getCredentials());}
}
