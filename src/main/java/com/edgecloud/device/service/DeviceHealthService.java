package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceHealthQueryRequest;
import com.edgecloud.device.dto.DeviceHealthResponse;

public interface DeviceHealthService {
    DeviceHealthResponse getDeviceHealth(DeviceHealthQueryRequest request);
}
