package com.edgecloud.device.controller;

import com.edgecloud.device.dto.*;
import com.edgecloud.device.security.EdgeCloudJwtAuthenticationToken;
import com.edgecloud.device.service.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
public class DeviceMaintenanceController {
    private final DeviceMaintenanceService service;
    private final ProjectScopeClient projects;

    public DeviceMaintenanceController(DeviceMaintenanceService service, ProjectScopeClient projects) {
        this.service = service;
        this.projects = projects;
    }

    @GetMapping("/{deviceId}/maintenance")
    public DeviceMaintenanceResponse get(@PathVariable UUID deviceId) {
        return service.get(deviceId);
    }

    @PostMapping("/{deviceId}/maintenance")
    public DeviceMaintenanceResponse enable(@PathVariable UUID deviceId,
                                            @Valid @RequestBody DeviceMaintenanceRequest request,
                                            EdgeCloudJwtAuthenticationToken auth) {
        return service.enable(deviceId, request, auth.getUserId(), LocalDateTime.now());
    }

    @DeleteMapping("/{deviceId}/maintenance")
    public ResponseEntity<Void> disable(@PathVariable UUID deviceId, EdgeCloudJwtAuthenticationToken auth) {
        service.disable(deviceId, auth.getUserId(), LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deviceId}/maintenance/history")
    public List<DeviceMaintenanceHistoryResponse> history(@PathVariable UUID deviceId) {
        return service.history(deviceId);
    }

    @GetMapping("/projects/{projectId}/devices/{deviceId}/maintenance")
    public DeviceMaintenanceResponse projectGet(@PathVariable UUID projectId, @PathVariable UUID deviceId,
                                                EdgeCloudJwtAuthenticationToken auth) {
        requireAssociation(projectId, deviceId, auth);
        return service.get(deviceId);
    }

    @PostMapping("/projects/{projectId}/devices/{deviceId}/maintenance")
    public DeviceMaintenanceResponse projectEnable(@PathVariable UUID projectId, @PathVariable UUID deviceId,
                                                   @Valid @RequestBody DeviceMaintenanceRequest request,
                                                   EdgeCloudJwtAuthenticationToken auth) {
        requireAssociation(projectId, deviceId, auth);
        return service.enable(deviceId, request, auth.getUserId(), LocalDateTime.now());
    }

    @DeleteMapping("/projects/{projectId}/devices/{deviceId}/maintenance")
    public ResponseEntity<Void> projectDisable(@PathVariable UUID projectId, @PathVariable UUID deviceId,
                                               EdgeCloudJwtAuthenticationToken auth) {
        requireAssociation(projectId, deviceId, auth);
        service.disable(deviceId, auth.getUserId(), LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/devices/{deviceId}/maintenance/history")
    public List<DeviceMaintenanceHistoryResponse> projectHistory(@PathVariable UUID projectId,
                                                                 @PathVariable UUID deviceId,
                                                                 EdgeCloudJwtAuthenticationToken auth) {
        requireAssociation(projectId, deviceId, auth);
        return service.history(deviceId);
    }

    private void requireAssociation(UUID projectId, UUID deviceId, EdgeCloudJwtAuthenticationToken auth) {
        projects.requireDeviceAssociation(projectId, deviceId, (String) auth.getCredentials());
    }
}
