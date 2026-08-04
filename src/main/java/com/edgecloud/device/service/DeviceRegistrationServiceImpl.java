package com.edgecloud.device.service;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.dto.DeviceHeartbeatRequest;
import com.edgecloud.device.dto.DeviceRegistrationRequest;
import com.edgecloud.device.dto.DeviceResponse;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.exception.DeviceNotFoundException;
import com.edgecloud.device.exception.DuplicateDeviceException;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class DeviceRegistrationServiceImpl
        implements DeviceRegistrationService {

    private static final Logger LOGGER =
            Logger.getLogger(
                    DeviceRegistrationServiceImpl.class.getName()
            );

    private final EdgeDeviceRepository repository;
    private final DeviceThresholdProperties thresholdProperties;

    public DeviceRegistrationServiceImpl(
            EdgeDeviceRepository repository,
            DeviceThresholdProperties thresholdProperties) {

        this.repository = repository;
        this.thresholdProperties = thresholdProperties;
    }

    @Override
    public DeviceResponse registerDevice(
            DeviceRegistrationRequest request) {

        if (repository.existsByDeviceName(request.deviceName())) {
            throw new DuplicateDeviceException(
                    "Device already registered: "
                            + request.deviceName()
            );
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
        LocalDateTime thresholdTime = LocalDateTime.now()
                .minusSeconds(
                        thresholdProperties
                                .offlineThresholdSeconds()
                );

        repository.findAll().forEach(device -> {
            if (device.getLastHeartbeat() != null
                    && device.getLastHeartbeat()
                    .isBefore(thresholdTime)
                    && device.getStatus()
                    == DeviceStatus.ONLINE) {

                device.setStatus(DeviceStatus.OFFLINE);
                repository.save(device);

                LOGGER.info(
                        "Device marked OFFLINE due to missed heartbeat: "
                                + device.getDeviceName()
                );
            }
        });
    }

    @Override
    public DeviceResponse processHeartbeat(
            DeviceHeartbeatRequest request) {

        EdgeDevice device = repository
                .findById(request.deviceId())
                .orElseThrow(
                        () -> new DeviceNotFoundException(
                                "Device not found: "
                                        + request.deviceId()
                        )
                );

        LocalDateTime heartbeatTime =
                request.timestamp() != null
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
