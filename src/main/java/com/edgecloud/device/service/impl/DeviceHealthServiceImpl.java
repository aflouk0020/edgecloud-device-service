package com.edgecloud.device.service.impl;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.dto.DeviceHealthDataState;
import com.edgecloud.device.dto.DeviceHealthQueryRequest;
import com.edgecloud.device.dto.DeviceHealthRecord;
import com.edgecloud.device.dto.DeviceHealthResponse;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import com.edgecloud.device.service.DeviceHealthService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DeviceHealthServiceImpl implements DeviceHealthService {

    private final EdgeDeviceRepository edgeDeviceRepository;
    private final DeviceThresholdProperties thresholdProperties;

    public DeviceHealthServiceImpl(EdgeDeviceRepository edgeDeviceRepository,
                                   DeviceThresholdProperties thresholdProperties) {
        this.edgeDeviceRepository = edgeDeviceRepository;
        this.thresholdProperties = thresholdProperties;
    }

    @Override
    public DeviceHealthResponse getDeviceHealth(DeviceHealthQueryRequest request) {
        List<UUID> deviceIds = request == null || request.deviceIds() == null
                ? List.of()
                : request.deviceIds().stream().filter(Objects::nonNull).distinct().toList();

        if (deviceIds.isEmpty()) {
            return new DeviceHealthResponse(Instant.now(), List.of());
        }

        List<EdgeDevice> resolvedDevices = edgeDeviceRepository.findAllById(deviceIds);
        var resolvedById = resolvedDevices.stream()
                .collect(java.util.stream.Collectors.toMap(EdgeDevice::getId, device -> device, (left, right) -> left, LinkedHashMap::new));

        Instant now = Instant.now();
        long offlineThresholdSeconds = thresholdProperties.offlineThresholdSeconds();
        List<DeviceHealthRecord> records = deviceIds.stream()
                .map(deviceId -> toRecord(resolvedById.get(deviceId), deviceId, now, offlineThresholdSeconds))
                .sorted(Comparator.comparing(DeviceHealthRecord::deviceName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(DeviceHealthRecord::deviceId))
                .toList();

        return new DeviceHealthResponse(Instant.now(), records);
    }

    private DeviceHealthRecord toRecord(EdgeDevice device, UUID fallbackDeviceId, Instant now, long offlineThresholdSeconds) {
        if (device == null) {
            return new DeviceHealthRecord(
                    fallbackDeviceId,
                    null,
                    null,
                    null,
                    "UNAVAILABLE",
                    null,
                    null,
                    DeviceHealthDataState.NO_DATA);
        }

        DeviceStatus status = device.getStatus();
        LocalDateTime lastHeartbeat = device.getLastHeartbeat();
        String currentStatus = status == null ? "UNKNOWN" : status.name();

        if (status == DeviceStatus.ONLINE && isHeartbeatStale(lastHeartbeat, now, offlineThresholdSeconds)) {
            currentStatus = DeviceStatus.OFFLINE.name();
        }

        Instant latestHeartbeat = toInstant(lastHeartbeat);
        Instant lastUpdatedAt = latestHeartbeat != null ? latestHeartbeat : toInstant(device.getRegisteredAt());

        return new DeviceHealthRecord(
                device.getId(),
                device.getDeviceName(),
                device.getDeviceType(),
                device.getIpAddress(),
                currentStatus,
                latestHeartbeat,
                lastUpdatedAt,
                DeviceHealthDataState.COMPLETE);
    }

    private boolean isHeartbeatStale(LocalDateTime lastHeartbeat, Instant now, long offlineThresholdSeconds) {
        if (lastHeartbeat == null) {
            return true;
        }
        Instant heartbeatInstant = lastHeartbeat.toInstant(ZoneOffset.UTC);
        return heartbeatInstant.plus(offlineThresholdSeconds, ChronoUnit.SECONDS).isBefore(now);
    }

    private Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
