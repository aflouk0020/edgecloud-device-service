package com.edgecloud.device.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.edgecloud.device.dto.DeviceMaintenanceRequest;
import com.edgecloud.device.entity.*;
import com.edgecloud.device.exception.*;
import com.edgecloud.device.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DeviceMaintenanceServiceImplTest {
    @Mock EdgeDeviceRepository devices;
    @Mock DeviceMaintenanceHistoryRepository history;
    DeviceMaintenanceServiceImpl service;
    EdgeDevice device;
    UUID deviceId = UUID.randomUUID();
    UUID actor = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.of(2026, 8, 16, 14, 0);

    @BeforeEach
    void setUp() {
        service = new DeviceMaintenanceServiceImpl(devices, history);
        device = new EdgeDevice();
        ReflectionTestUtils.setField(device, "id", deviceId);
        device.setDeviceName("edge-node");
        device.setDeviceType("GATEWAY");
        device.setIpAddress("10.0.0.8");
        device.setDescription("Preserve me");
        device.prePersist();
        lenient().when(devices.findById(deviceId)).thenReturn(Optional.of(device));
        lenient().when(devices.findByIdForUpdate(deviceId)).thenReturn(Optional.of(device));
        lenient().when(devices.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void enablesMaintenanceAndPreservesMetadata() {
        LocalDateTime end = now.plusHours(2);
        var response = service.enable(deviceId, new DeviceMaintenanceRequest("  Planned work  ", end), actor, now);

        assertThat(response.maintenanceMode()).isTrue();
        assertThat(response.reason()).isEqualTo("Planned work");
        assertThat(response.enabledBy()).isEqualTo(actor);
        assertThat(device.getDescription()).isEqualTo("Preserve me");
        assertThat(device.isActive()).isTrue();
        verify(history).save(argThat(event -> event.getAction() == DeviceMaintenanceAction.ENABLED));
    }

    @Test
    void exactDuplicateEnableAndRepeatedDisableAreIdempotent() {
        DeviceMaintenanceRequest request = new DeviceMaintenanceRequest("Planned work", now.plusHours(1));
        service.enable(deviceId, request, actor, now);
        service.enable(deviceId, request, actor, now.plusMinutes(1));
        verify(history, times(1)).save(any());

        service.disable(deviceId, actor, now.plusMinutes(2));
        service.disable(deviceId, actor, now.plusMinutes(3));
        verify(history, times(2)).save(any());
    }

    @Test
    void rejectsDifferentDuplicateInvalidEndAndInactiveDevice() {
        service.enable(deviceId, new DeviceMaintenanceRequest("A", null), actor, now);
        assertThatThrownBy(() -> service.enable(deviceId, new DeviceMaintenanceRequest("B", null), actor, now))
                .isInstanceOf(InvalidDeviceLifecycleException.class);
        service.disable(deviceId, actor, now);
        assertThatThrownBy(() -> service.enable(deviceId, new DeviceMaintenanceRequest(null, now), actor, now))
                .isInstanceOf(IllegalArgumentException.class);
        device.setActive(false);
        assertThatThrownBy(() -> service.enable(deviceId, new DeviceMaintenanceRequest(null, null), actor, now))
                .isInstanceOf(InvalidDeviceLifecycleException.class);
    }

    @Test
    void disablesMaintenanceAndRetainsAuditValues() {
        LocalDateTime end = now.plusHours(1);
        service.enable(deviceId, new DeviceMaintenanceRequest("Upgrade", end), actor, now);
        UUID disablingActor = UUID.randomUUID();
        var response = service.disable(deviceId, disablingActor, now.plusMinutes(10));

        assertThat(response.maintenanceMode()).isFalse();
        assertThat(response.disabledAt()).isEqualTo(now.plusMinutes(10));
        assertThat(response.disabledBy()).isEqualTo(disablingActor);
        verify(history).save(argThat(event -> event.getAction() == DeviceMaintenanceAction.DISABLED
                && event.getReason().equals("Upgrade") && event.getScheduledEndAt().equals(end)));
    }

    @Test
    void expiresScheduledMaintenanceAutomatically() {
        device.setMaintenanceMode(true);
        device.setMaintenanceReason("Scheduled");
        device.setMaintenanceScheduledEndAt(now);
        when(devices.findByMaintenanceModeTrueAndMaintenanceScheduledEndAtLessThanEqual(now)).thenReturn(List.of(device));

        service.expireScheduled(now);

        assertThat(device.isMaintenanceMode()).isFalse();
        assertThat(device.getMaintenanceDisabledBy()).isNull();
        verify(history).save(argThat(event -> event.getAction() == DeviceMaintenanceAction.EXPIRED
                && event.getActorUserId() == null));
    }

    @Test
    void retrievesNewestFirstDurableHistoryAndHandlesMissingDevice() {
        DeviceMaintenanceHistory event = new DeviceMaintenanceHistory(deviceId, DeviceMaintenanceAction.ENABLED,
                now, actor, "Work", null);
        when(history.findByDeviceIdOrderByOccurredAtDescIdDesc(deviceId)).thenReturn(List.of(event));
        assertThat(service.history(deviceId)).extracting("action").containsExactly(DeviceMaintenanceAction.ENABLED);

        when(devices.existsById(deviceId)).thenReturn(false);
        assertThat(service.history(deviceId)).hasSize(1);

        UUID missing = UUID.randomUUID();
        when(history.findByDeviceIdOrderByOccurredAtDescIdDesc(missing)).thenReturn(List.of());
        assertThatThrownBy(() -> service.history(missing)).isInstanceOf(DeviceNotFoundException.class);
    }
}
