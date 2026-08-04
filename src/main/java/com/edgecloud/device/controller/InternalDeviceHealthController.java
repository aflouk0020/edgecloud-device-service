package com.edgecloud.device.controller;

import com.edgecloud.device.dto.DeviceHealthQueryRequest;
import com.edgecloud.device.dto.DeviceHealthResponse;
import com.edgecloud.device.service.DeviceHealthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/device-health")
public class InternalDeviceHealthController {

    private final DeviceHealthService deviceHealthService;

    public InternalDeviceHealthController(DeviceHealthService deviceHealthService) {
        this.deviceHealthService = deviceHealthService;
    }

    @PostMapping
    public ResponseEntity<DeviceHealthResponse> getDeviceHealth(@Valid @RequestBody(required = false) DeviceHealthQueryRequest request) {
        return ResponseEntity.ok(deviceHealthService.getDeviceHealth(request));
    }
}
