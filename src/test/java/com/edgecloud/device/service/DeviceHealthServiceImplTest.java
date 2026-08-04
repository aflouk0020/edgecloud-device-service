package com.edgecloud.device.service;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.dto.DeviceHealthDataState;
import com.edgecloud.device.dto.DeviceHealthQueryRequest;
import com.edgecloud.device.dto.DeviceHealthRecord;
import com.edgecloud.device.dto.DeviceHealthResponse;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import com.edgecloud.device.service.impl.DeviceHealthServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceHealthServiceImplTest {

    @Mock
    private EdgeDeviceRepository edgeDeviceRepository;

    private DeviceHealthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeviceHealthServiceImpl(edgeDeviceRepository, new DeviceThresholdProperties(60));
    }

    @Test
    void onlineDeviceIsHealthy() {
        UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        LocalDateTime heartbeat = LocalDateTime.now();
        EdgeDevice device = device(deviceId, "Alpha", DeviceStatus.ONLINE, heartbeat);
        when(edgeDeviceRepository.findAllById(List.of(deviceId))).thenReturn(List.of(device));

        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(deviceId)));

        assertThat(response.devices()).singleElement().satisfies(record -> {
            assertThat(record.deviceId()).isEqualTo(deviceId);
            assertThat(record.currentStatus()).isEqualTo("ONLINE");
            assertThat(record.latestHeartbeat()).isEqualTo(heartbeat.toInstant(java.time.ZoneOffset.UTC));
            assertThat(record.dataState()).isEqualTo(DeviceHealthDataState.COMPLETE);
        });
    }

    @Test
    void offlineDeviceIsOffline() {
        UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111112");
        EdgeDevice device = device(deviceId, "Bravo", DeviceStatus.OFFLINE, LocalDateTime.parse("2026-08-04T09:59:00"));
        when(edgeDeviceRepository.findAllById(List.of(deviceId))).thenReturn(List.of(device));

        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(deviceId)));

        assertThat(response.devices()).singleElement().satisfies(record -> assertThat(record.currentStatus()).isEqualTo("OFFLINE"));
    }

    @Test
    void staleHeartbeatMarksOnlineDeviceOffline() {
        UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111113");
        EdgeDevice device = device(deviceId, "Charlie", DeviceStatus.ONLINE, LocalDateTime.now().minusHours(2));
        when(edgeDeviceRepository.findAllById(List.of(deviceId))).thenReturn(List.of(device));

        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(deviceId)));

        assertThat(response.devices()).singleElement().satisfies(record -> assertThat(record.currentStatus()).isEqualTo("OFFLINE"));
    }

    @Test
    void unknownDeviceIdReturnsUnavailable() {
        UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111114");
        when(edgeDeviceRepository.findAllById(List.of(deviceId))).thenReturn(List.of());

        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(deviceId)));

        assertThat(response.devices()).singleElement().satisfies(record -> {
            assertThat(record.currentStatus()).isEqualTo("UNAVAILABLE");
            assertThat(record.dataState()).isEqualTo(DeviceHealthDataState.NO_DATA);
        });
    }

    @Test
    void multipleApprovedIdsAreSortedByNameThenId() {
        UUID alphaId = UUID.fromString("11111111-1111-1111-1111-111111111115");
        UUID betaId = UUID.fromString("11111111-1111-1111-1111-111111111116");
        EdgeDevice beta = device(betaId, "Beta", DeviceStatus.OFFLINE, LocalDateTime.parse("2026-08-04T09:59:00"));
        EdgeDevice alpha = device(alphaId, "alpha", DeviceStatus.ONLINE, LocalDateTime.parse("2026-08-04T09:59:00"));
        when(edgeDeviceRepository.findAllById(List.of(betaId, alphaId))).thenReturn(List.of(beta, alpha));

        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(betaId, alphaId)));

        assertThat(response.devices()).extracting(DeviceHealthRecord::deviceId).containsExactly(alphaId, betaId);
    }

    @Test
    void emptyIdListReturnsEmptyResponse() {
        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of()));

        assertThat(response.devices()).isEmpty();
        verifyNoInteractions(edgeDeviceRepository);
    }

    @Test
    void oneUnresolvedDeviceDoesNotFailResponse() {
        UUID resolvedId = UUID.fromString("11111111-1111-1111-1111-111111111117");
        UUID unresolvedId = UUID.fromString("11111111-1111-1111-1111-111111111118");
        EdgeDevice resolved = device(resolvedId, "Zulu", DeviceStatus.ONLINE, LocalDateTime.parse("2026-08-04T09:59:00"));
        when(edgeDeviceRepository.findAllById(List.of(resolvedId, unresolvedId))).thenReturn(List.of(resolved));

        DeviceHealthResponse response = service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(resolvedId, unresolvedId)));

        assertThat(response.devices()).hasSize(2);
        assertThat(response.devices()).anySatisfy(record -> assertThat(record.currentStatus()).isEqualTo("UNAVAILABLE"));
    }

    @Test
    void noGlobalFallbackIsUsed() {
        UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111119");
        when(edgeDeviceRepository.findAllById(List.of(deviceId))).thenReturn(List.of());

        service.getDeviceHealth(new DeviceHealthQueryRequest(List.of(deviceId)));

        ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);
        verify(edgeDeviceRepository).findAllById(captor.capture());
        assertThat(captor.getValue()).containsExactly(deviceId);
    }

    private EdgeDevice device(UUID id, String name, DeviceStatus status, LocalDateTime heartbeat) {
        EdgeDevice device = new EdgeDevice();
        try {
            var idField = EdgeDevice.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(device, id);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        device.setDeviceName(name);
        device.setDeviceType("sensor");
        device.setIpAddress("10.0.0.1");
        device.setStatus(status);
        device.setLastHeartbeat(heartbeat);
        return device;
    }
}
