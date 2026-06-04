package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceHeartbeatRequest;
import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.exception.DeviceNotFoundException;
import com.edgecloud.device.exception.DuplicateDeviceException;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceRegistrationServiceImpl implements DeviceRegistrationService {

    private final EdgeDeviceRepository repository;
    private static final Logger LOGGER = Logger.getLogger(DeviceRegistrationServiceImpl.class.getName());

    @Value("${edgecloud.device.offline-threshold-seconds:60}")
    private long offlineThresholdSeconds;
    
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
    
    @Override
    public List<DeviceResponse> getAllDevices() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Override
    public void evaluateDeviceStatuses() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(offlineThresholdSeconds);

        repository.findAll().forEach(device -> {
            if (device.getLastHeartbeat() != null
                    && device.getLastHeartbeat().isBefore(thresholdTime)
                    && device.getStatus() == DeviceStatus.ONLINE) {

                device.setStatus(DeviceStatus.OFFLINE);
                repository.save(device);

                LOGGER.info("Device marked OFFLINE due to missed heartbeat: " + device.getDeviceName());
            }
        });
    }

    @Override
    public DeviceResponse processHeartbeat(DeviceHeartbeatRequest request) {
        EdgeDevice device = repository.findById(request.deviceId())
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + request.deviceId()));

        LocalDateTime heartbeatTime = request.timestamp() != null
                ? request.timestamp()
                : LocalDateTime.now();

        device.setLastHeartbeat(heartbeatTime);
        device.setStatus(DeviceStatus.ONLINE);

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
