package com.edgecloud.device.service;

import com.edgecloud.device.dto.*;
import com.edgecloud.device.entity.*;
import com.edgecloud.device.exception.*;
import com.edgecloud.device.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceMaintenanceServiceImpl implements DeviceMaintenanceService {
    private final EdgeDeviceRepository devices;
    private final DeviceMaintenanceHistoryRepository history;

    public DeviceMaintenanceServiceImpl(EdgeDeviceRepository devices, DeviceMaintenanceHistoryRepository history) {
        this.devices = devices;
        this.history = history;
    }

    @Override @Transactional(readOnly = true)
    public DeviceMaintenanceResponse get(UUID deviceId) {
        return response(find(deviceId));
    }

    @Override @Transactional
    public DeviceMaintenanceResponse enable(UUID deviceId, DeviceMaintenanceRequest request, UUID actor, LocalDateTime now) {
        EdgeDevice device = locked(deviceId);
        if (!device.isActive()) throw new InvalidDeviceLifecycleException("Inactive device cannot enter maintenance mode");
        LocalDateTime end = request.scheduledEndAt();
        if (end != null && !end.isAfter(now)) throw new IllegalArgumentException("Scheduled maintenance end must be in the future");
        String reason = normalise(request.reason());
        if (device.isMaintenanceMode()
                && Objects.equals(device.getMaintenanceReason(), reason)
                && Objects.equals(device.getMaintenanceScheduledEndAt(), end)) return response(device);
        if (device.isMaintenanceMode()) {
            throw new InvalidDeviceLifecycleException("Device is already in maintenance mode with a different policy");
        }

        device.setMaintenanceMode(true);
        device.setMaintenanceReason(reason);
        device.setMaintenanceEnabledAt(now);
        device.setMaintenanceEnabledBy(actor);
        device.setMaintenanceScheduledEndAt(end);
        device.setMaintenanceDisabledAt(null);
        device.setMaintenanceDisabledBy(null);
        devices.save(device);
        history.save(new DeviceMaintenanceHistory(deviceId, DeviceMaintenanceAction.ENABLED, now, actor, reason, end));
        return response(device);
    }

    @Override @Transactional
    public DeviceMaintenanceResponse disable(UUID deviceId, UUID actor, LocalDateTime now) {
        EdgeDevice device = locked(deviceId);
        if (!device.isMaintenanceMode()) return response(device);
        end(device, DeviceMaintenanceAction.DISABLED, actor, now);
        return response(device);
    }

    @Override @Transactional(readOnly = true)
    public List<DeviceMaintenanceHistoryResponse> history(UUID deviceId) {
        List<DeviceMaintenanceHistory> events = history.findByDeviceIdOrderByOccurredAtDescIdDesc(deviceId);
        if (!devices.existsById(deviceId) && events.isEmpty()) {
            throw new DeviceNotFoundException("Device not found: " + deviceId);
        }
        return events.stream()
                .map(event -> new DeviceMaintenanceHistoryResponse(event.getId(), event.getDeviceId(), event.getAction(),
                        event.getOccurredAt(), event.getActorUserId(), event.getReason(), event.getScheduledEndAt()))
                .toList();
    }

    @Override @Transactional
    public void expireScheduled(LocalDateTime now) {
        for (EdgeDevice device : devices.findByMaintenanceModeTrueAndMaintenanceScheduledEndAtLessThanEqual(now)) {
            end(device, DeviceMaintenanceAction.EXPIRED, null, now);
        }
    }

    private void end(EdgeDevice device, DeviceMaintenanceAction action, UUID actor, LocalDateTime now) {
        String reason = device.getMaintenanceReason();
        LocalDateTime scheduledEnd = device.getMaintenanceScheduledEndAt();
        device.setMaintenanceMode(false);
        device.setMaintenanceReason(null);
        device.setMaintenanceScheduledEndAt(null);
        device.setMaintenanceDisabledAt(now);
        device.setMaintenanceDisabledBy(actor);
        devices.save(device);
        history.save(new DeviceMaintenanceHistory(device.getId(), action, now, actor, reason, scheduledEnd));
    }

    private EdgeDevice find(UUID id) {
        return devices.findById(id).orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
    }

    private EdgeDevice locked(UUID id) {
        return devices.findByIdForUpdate(id).orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
    }

    private String normalise(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private DeviceMaintenanceResponse response(EdgeDevice device) {
        return new DeviceMaintenanceResponse(device.getId(), device.isMaintenanceMode(), device.getMaintenanceReason(),
                device.getMaintenanceEnabledAt(), device.getMaintenanceEnabledBy(), device.getMaintenanceScheduledEndAt(),
                device.getMaintenanceDisabledAt(), device.getMaintenanceDisabledBy());
    }
}
