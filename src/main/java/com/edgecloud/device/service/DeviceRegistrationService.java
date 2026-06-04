package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;

public interface DeviceRegistrationService {

    DeviceResponse registerDevice(DeviceRegistrationRequest request);
}
