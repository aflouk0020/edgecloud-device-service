package com.edgecloud.device.service;

import com.edgecloud.device.dto.DeviceSummaryResponse;
import com.edgecloud.device.entity.DeviceStatus;
import com.edgecloud.device.repository.EdgeDeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceAnalyticsServiceImpl implements DeviceAnalyticsService {

    private final EdgeDeviceRepository edgeDeviceRepository;

    public DeviceAnalyticsServiceImpl(EdgeDeviceRepository edgeDeviceRepository) {
        this.edgeDeviceRepository = edgeDeviceRepository;
    }

    @Override
    public DeviceSummaryResponse getSummary() {
        long totalDevices = edgeDeviceRepository.count();
        long onlineDevices = edgeDeviceRepository.countByStatus(DeviceStatus.ONLINE);
        long offlineDevices = edgeDeviceRepository.countByStatus(DeviceStatus.OFFLINE);

        return new DeviceSummaryResponse(
                totalDevices,
                onlineDevices,
                offlineDevices,
                percentage(onlineDevices, totalDevices)
        );
    }

    private double percentage(long value, long total) {
        if (total == 0) {
            return 0.0;
        }

        return Math.round(((value * 100.0) / total) * 100.0) / 100.0;
    }
}
