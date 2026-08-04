package com.edgecloud.device.service;

import com.edgecloud.device.config.DeviceThresholdProperties;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceRegistrationServiceImplTest {

    @Mock
    private EdgeDeviceRepository repository;

    @Test
    void marksDeviceOfflineWhenConfiguredThresholdIsExceeded() {
        EdgeDevice device = createOnlineDevice(
                LocalDateTime.now().minusSeconds(61)
        );

        when(repository.findAll()).thenReturn(List.of(device));

        DeviceRegistrationServiceImpl service =
                new DeviceRegistrationServiceImpl(
                        repository,
                        new DeviceThresholdProperties(60)
                );

        service.evaluateDeviceStatuses();

        assertThat(device.getStatus())
                .isEqualTo(DeviceStatus.OFFLINE);

        verify(repository).save(device);
    }

    @Test
    void keepsDeviceOnlineWhenHeartbeatIsWithinConfiguredThreshold() {
        EdgeDevice device = createOnlineDevice(
                LocalDateTime.now().minusSeconds(30)
        );

        when(repository.findAll()).thenReturn(List.of(device));

        DeviceRegistrationServiceImpl service =
                new DeviceRegistrationServiceImpl(
                        repository,
                        new DeviceThresholdProperties(60)
                );

        service.evaluateDeviceStatuses();

        assertThat(device.getStatus())
                .isEqualTo(DeviceStatus.ONLINE);

        verify(repository, never())
                .save(any(EdgeDevice.class));
    }

    @Test
    void configuredThresholdChangesOfflineDetectionBehaviour() {
        EdgeDevice device = createOnlineDevice(
                LocalDateTime.now().minusSeconds(90)
        );

        when(repository.findAll()).thenReturn(List.of(device));

        DeviceRegistrationServiceImpl service =
                new DeviceRegistrationServiceImpl(
                        repository,
                        new DeviceThresholdProperties(120)
                );

        service.evaluateDeviceStatuses();

        assertThat(device.getStatus())
                .isEqualTo(DeviceStatus.ONLINE);

        verify(repository, never())
                .save(any(EdgeDevice.class));
    }

    private EdgeDevice createOnlineDevice(
            LocalDateTime heartbeatTime) {

        EdgeDevice device = new EdgeDevice();
        device.setDeviceName("pi-node-01");
        device.setDeviceType("RASPBERRY_PI");
        device.setIpAddress("192.168.1.20");
        device.setStatus(DeviceStatus.ONLINE);
        device.setLastHeartbeat(heartbeatTime);

        return device;
    }
}
