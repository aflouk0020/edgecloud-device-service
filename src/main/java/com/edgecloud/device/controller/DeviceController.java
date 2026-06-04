package com.edgecloud.device.controller;

import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;
import com.edgecloud.device.service.DeviceRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class DeviceController {

    private final DeviceRegistrationService deviceRegistrationService;

    public DeviceController(DeviceRegistrationService deviceRegistrationService) {
        this.deviceRegistrationService = deviceRegistrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceResponse> registerDevice(
            @Valid @RequestBody DeviceRegistrationRequest request) {

        DeviceResponse response = deviceRegistrationService.registerDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
