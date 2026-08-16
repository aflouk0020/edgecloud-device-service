package com.edgecloud.device.service;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.dto.DeviceInventoryItemResponse;
import com.edgecloud.device.dto.DeviceInventoryResponse;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceInventoryServiceImpl implements DeviceInventoryService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "name", "deviceName",
            "status", "status",
            "lastSeen", "lastHeartbeat",
            "registrationDate", "registeredAt");

    private final EdgeDeviceRepository repository;
    private final DeviceThresholdProperties thresholdProperties;

    public DeviceInventoryServiceImpl(EdgeDeviceRepository repository,
                                      DeviceThresholdProperties thresholdProperties) {
        this.repository = repository;
        this.thresholdProperties = thresholdProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceInventoryResponse getInventory(
            String search, int page, int size, String sort, String direction) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be at least 0 and size must be between 1 and 100");
        }

        String sortProperty = SORT_FIELDS.get(sort);
        if (sortProperty == null) {
            throw new IllegalArgumentException("Unsupported sort field: " + sort);
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Direction must be asc or desc");
        }

        var pageable = PageRequest.of(page, size,
                Sort.by(sortDirection, sortProperty).and(Sort.by(Sort.Direction.ASC, "id")));
        String query = search == null ? "" : search.trim();
        var result = query.isEmpty() ? repository.findAll(pageable) : findByNameOrId(query, pageable);
        LocalDateTime staleBefore = LocalDateTime.now()
                .minusSeconds(thresholdProperties.offlineThresholdSeconds());

        return new DeviceInventoryResponse(
                result.getContent().stream().map(device -> toItem(device, staleBefore)).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(),
                sort, sortDirection.name().toLowerCase(Locale.ROOT));
    }

    private org.springframework.data.domain.Page<EdgeDevice> findByNameOrId(
            String query, org.springframework.data.domain.Pageable pageable) {
        try {
            return repository.findByIdOrDeviceNameContainingIgnoreCase(UUID.fromString(query), query, pageable);
        } catch (IllegalArgumentException ignored) {
            return repository.findByDeviceNameContainingIgnoreCase(query, pageable);
        }
    }

    private DeviceInventoryItemResponse toItem(EdgeDevice device, LocalDateTime staleBefore) {
        String heartbeatStatus;
        if (device.getLastHeartbeat() == null) {
            heartbeatStatus = "NEVER_RECEIVED";
        } else if (device.getLastHeartbeat().isBefore(staleBefore)) {
            heartbeatStatus = "STALE";
        } else {
            heartbeatStatus = "CURRENT";
        }

        return new DeviceInventoryItemResponse(
                device.getId(), device.getDeviceName(), device.getDeviceType(), device.getStatus(),
                heartbeatStatus, device.getLastHeartbeat(), null, null, device.getRegisteredAt(),
                device.getLastHeartbeat(), List.of(), null);
    }
}
