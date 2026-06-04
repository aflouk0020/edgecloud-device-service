package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.exception.DuplicateDeviceException;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceRegistrationServiceImpl implements DeviceRegistrationService {

    private final EdgeDeviceRepository repository;

    public DeviceRegistrationServiceImpl(EdgeDeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceResponse registerDevice(DeviceRegistrationRequest request) {
        if (repository.existsByDeviceName(request.deviceName())) {
            throw new DuplicateDeviceException("Device already registered: " + request.deviceName());
        }

        EdgeDevice device = new EdgeDevice();
        device.setDeviceName(request.deviceName());
        device.setDeviceType(request.deviceType());
        device.setIpAddress(request.ipAddress());

        EdgeDevice saved = repository.save(device);
        return toResponse(saved);
    }

    private DeviceResponse toResponse(EdgeDevice device) {
        return new DeviceResponse(
                device.getId(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getIpAddress(),
                device.getStatus(),
                device.getRegisteredAt(),
                device.getLastHeartbeat()
        );
    }
}
