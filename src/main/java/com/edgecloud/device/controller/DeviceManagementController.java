package com.edgecloud.device.controller;

import com.edgecloud.device.dto.*;
import com.edgecloud.device.security.EdgeCloudJwtAuthenticationToken;
import com.edgecloud.device.service.DeviceManagementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
public class DeviceManagementController {
    private final DeviceManagementService service;
    public DeviceManagementController(DeviceManagementService service) { this.service = service; }
    @PostMapping public ResponseEntity<DeviceManagementResponse> register(@Valid @RequestBody DeviceManagementRequest request, EdgeCloudJwtAuthenticationToken auth) { return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request, auth.getUserId())); }
    @GetMapping("/{id}") public DeviceManagementResponse get(@PathVariable UUID id) { return service.get(id); }
    @PutMapping("/{id}") public DeviceManagementResponse update(@PathVariable UUID id, @Valid @RequestBody DeviceManagementRequest request, EdgeCloudJwtAuthenticationToken auth) { return service.update(id, request, auth.getUserId()); }
    @PostMapping("/{id}/deactivate") public DeviceManagementResponse deactivate(@PathVariable UUID id, EdgeCloudJwtAuthenticationToken auth) { return service.deactivate(id, auth.getUserId()); }
    @PostMapping("/{id}/reactivate") public DeviceManagementResponse reactivate(@PathVariable UUID id, EdgeCloudJwtAuthenticationToken auth) { return service.reactivate(id, auth.getUserId()); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id, EdgeCloudJwtAuthenticationToken auth) { service.delete(id, auth.getUserId()); return ResponseEntity.noContent().build(); }
    @GetMapping("/{id}/history") public List<DeviceLifecycleHistoryResponse> history(@PathVariable UUID id) { return service.history(id); }
}
