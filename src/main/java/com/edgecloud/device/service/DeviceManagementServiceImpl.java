package com.edgecloud.device.service;

import com.edgecloud.device.dto.*;
import com.edgecloud.device.entity.*;
import com.edgecloud.device.exception.*;
import com.edgecloud.device.repository.*;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceManagementServiceImpl implements DeviceManagementService {
    private final EdgeDeviceRepository devices;
    private final DeviceLifecycleHistoryRepository history;
    public DeviceManagementServiceImpl(EdgeDeviceRepository devices, DeviceLifecycleHistoryRepository history) {
        this.devices = devices; this.history = history;
    }

    @Override @Transactional
    public DeviceManagementResponse register(DeviceManagementRequest request, UUID actor) {
        if (devices.existsByDeviceName(request.name())) throw duplicate(request.name());
        EdgeDevice device = new EdgeDevice(); apply(device, request);
        device = devices.save(device); record(device, "REGISTERED", actor, "Device registered");
        return response(device);
    }
    @Override @Transactional(readOnly = true)
    public DeviceManagementResponse get(UUID id) { return response(find(id)); }
    @Override @Transactional
    public DeviceManagementResponse update(UUID id, DeviceManagementRequest request, UUID actor) {
        EdgeDevice device = find(id);
        if (devices.existsByDeviceNameAndIdNot(request.name(), id)) throw duplicate(request.name());
        apply(device, request); device = devices.save(device); record(device, "UPDATED", actor, "Metadata updated");
        return response(device);
    }
    @Override @Transactional
    public DeviceManagementResponse deactivate(UUID id, UUID actor) {
        EdgeDevice device = find(id);
        if (!device.isActive()) throw lifecycle("Device is already inactive");
        device.setActive(false); device.setStatus(DeviceStatus.OFFLINE); device = devices.save(device);
        record(device, "DEACTIVATED", actor, "Device deactivated"); return response(device);
    }
    @Override @Transactional
    public DeviceManagementResponse reactivate(UUID id, UUID actor) {
        EdgeDevice device = find(id);
        if (device.isActive()) throw lifecycle("Device is already active");
        device.setActive(true); device.setStatus(DeviceStatus.OFFLINE); device = devices.save(device);
        record(device, "REACTIVATED", actor, "Device reactivated"); return response(device);
    }
    @Override @Transactional
    public void delete(UUID id, UUID actor) {
        EdgeDevice device = find(id);
        if (device.isActive()) throw lifecycle("Deactivate device before removal");
        record(device, "DELETED", actor, "Local device record removed; external history retained");
        devices.delete(device);
    }
    @Override @Transactional(readOnly = true)
    public List<DeviceLifecycleHistoryResponse> history(UUID id) {
        if (!devices.existsById(id) && history.findByDeviceIdOrderByOccurredAtDesc(id).isEmpty()) throw notFound(id);
        return history.findByDeviceIdOrderByOccurredAtDesc(id).stream().map(h -> new DeviceLifecycleHistoryResponse(
                h.getId(), h.getDeviceId(), h.getAction(), h.getOccurredAt(), h.getActorUserId(),
                h.getDeviceName(), h.isActive(), h.getDetails())).toList();
    }
    private EdgeDevice find(UUID id) { return devices.findById(id).orElseThrow(() -> notFound(id)); }
    private DeviceNotFoundException notFound(UUID id) { return new DeviceNotFoundException("Device not found: " + id); }
    private DuplicateDeviceException duplicate(String name) { return new DuplicateDeviceException("Device already registered: " + name); }
    private InvalidDeviceLifecycleException lifecycle(String message) { return new InvalidDeviceLifecycleException(message); }
    private void apply(EdgeDevice d, DeviceManagementRequest r) { d.setDeviceName(r.name().trim()); d.setDeviceType(r.type().trim()); d.setIpAddress(r.ipAddress().trim()); d.setDescription(r.description()); d.setPhysicalLocation(r.location()); d.setFirmwareVersion(r.firmwareVersion()); d.setOperatingSystem(r.operatingSystem()); }
    private void record(EdgeDevice d, String action, UUID actor, String details) { history.save(new DeviceLifecycleHistory(d.getId(), action, actor, d.getDeviceName(), d.isActive(), details)); }
    private DeviceManagementResponse response(EdgeDevice d) { return new DeviceManagementResponse(d.getId(), d.getDeviceName(), d.getDeviceType(), d.getIpAddress(), d.getDescription(), d.getPhysicalLocation(), d.getFirmwareVersion(), d.getOperatingSystem(), d.getStatus(), d.isActive(), d.getRegisteredAt(), d.getUpdatedAt(), d.getLastHeartbeat()); }
}
