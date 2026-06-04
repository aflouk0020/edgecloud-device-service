package com.edgecloud.device.service;

import java.util.List;

import com.edgecloud.device.dto.DeviceHeartbeatRequest;
import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;

public interface DeviceRegistrationService {

    DeviceResponse registerDevice(DeviceRegistrationRequest request);

    DeviceResponse processHeartbeat(DeviceHeartbeatRequest request);
    List<DeviceResponse> getAllDevices();
}
