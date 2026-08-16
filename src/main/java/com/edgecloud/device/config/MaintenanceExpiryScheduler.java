package com.edgecloud.device.config;

import com.edgecloud.device.service.DeviceMaintenanceService;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceExpiryScheduler {
    private final DeviceMaintenanceService service;

    public MaintenanceExpiryScheduler(DeviceMaintenanceService service) {
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${edgecloud.device.maintenance-expiry-delay-ms:10000}")
    public void expire() {
        service.expireScheduled(LocalDateTime.now());
    }
}
