package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceSummaryResponse;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceAnalyticsServiceImplTest {

    @Mock
    private EdgeDeviceRepository edgeDeviceRepository;

    @InjectMocks
    private DeviceAnalyticsServiceImpl analyticsService;

    @Test
    void calculatesDeviceAvailabilitySummary() {
        when(edgeDeviceRepository.count()).thenReturn(5L);
        when(edgeDeviceRepository.countByStatus(DeviceStatus.ONLINE)).thenReturn(4L);
        when(edgeDeviceRepository.countByStatus(DeviceStatus.OFFLINE)).thenReturn(1L);

        DeviceSummaryResponse response = analyticsService.getSummary();

        assertThat(response.totalDevices()).isEqualTo(5);
        assertThat(response.onlineDevices()).isEqualTo(4);
        assertThat(response.offlineDevices()).isEqualTo(1);
        assertThat(response.availabilityPercentage()).isEqualTo(80.0);
    }

    @Test
    void returnsZeroAvailabilityWhenNoDevicesExist() {
        when(edgeDeviceRepository.count()).thenReturn(0L);
        when(edgeDeviceRepository.countByStatus(DeviceStatus.ONLINE)).thenReturn(0L);
        when(edgeDeviceRepository.countByStatus(DeviceStatus.OFFLINE)).thenReturn(0L);

        DeviceSummaryResponse response = analyticsService.getSummary();

        assertThat(response.totalDevices()).isZero();
        assertThat(response.availabilityPercentage()).isZero();
    }
}
