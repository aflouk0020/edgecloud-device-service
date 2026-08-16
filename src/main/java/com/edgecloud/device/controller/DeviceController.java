package com.edgecloud.device.controller;

import com.edgecloud.device.dto.DeviceHeartbeatRequest;
import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;
import com.edgecloud.device.dto.DeviceSummaryResponse;
import com.edgecloud.device.dto.DeviceInventoryResponse;
import com.edgecloud.device.service.DeviceAnalyticsService;
import com.edgecloud.device.service.DeviceInventoryService;
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
    private final DeviceInventoryService deviceInventoryService;

    public DeviceController(
            DeviceRegistrationService deviceRegistrationService,
            DeviceAnalyticsService deviceAnalyticsService,
            DeviceInventoryService deviceInventoryService) {
        this.deviceRegistrationService = deviceRegistrationService;
        this.deviceAnalyticsService = deviceAnalyticsService;
        this.deviceInventoryService = deviceInventoryService;
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

    @GetMapping("/inventory")
    public ResponseEntity<DeviceInventoryResponse> getInventory(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(deviceInventoryService.getInventory(search, page, size, sort, direction));
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
