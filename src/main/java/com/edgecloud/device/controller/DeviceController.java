package com.edgecloud.device.controller;

import com.edgecloud.device.dto.DeviceHeartbeatRequest;
import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;
import com.edgecloud.device.dto.DeviceSummaryResponse;
import com.edgecloud.device.service.DeviceAnalyticsService;
import com.edgecloud.device.service.DeviceRegistrationService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class DeviceController {

    private final DeviceRegistrationService deviceRegistrationService;
    private final DeviceAnalyticsService deviceAnalyticsService;

    public DeviceController(
            DeviceRegistrationService deviceRegistrationService,
            DeviceAnalyticsService deviceAnalyticsService) {
        this.deviceRegistrationService = deviceRegistrationService;
        this.deviceAnalyticsService = deviceAnalyticsService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceResponse> registerDevice(
            @Valid @RequestBody DeviceRegistrationRequest request) {

        DeviceResponse response = deviceRegistrationService.registerDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<DeviceResponse> receiveHeartbeat(
            @Valid @RequestBody DeviceHeartbeatRequest request) {

        DeviceResponse response = deviceRegistrationService.processHeartbeat(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        return ResponseEntity.ok(deviceRegistrationService.getAllDevices());
    }

    @GetMapping("/summary")
    public ResponseEntity<DeviceSummaryResponse> getDeviceSummary() {
        return ResponseEntity.ok(deviceAnalyticsService.getSummary());
    }
    
    @PostMapping("/status/evaluate")
    public ResponseEntity<Void> evaluateDeviceStatuses() {
        deviceRegistrationService.evaluateDeviceStatuses();
        return ResponseEntity.noContent().build();
    }
}
