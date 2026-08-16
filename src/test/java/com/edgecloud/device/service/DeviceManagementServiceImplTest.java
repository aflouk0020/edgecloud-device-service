package com.edgecloud.device.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.edgecloud.device.dto.DeviceManagementRequest;
import com.edgecloud.device.entity.EdgeDevice;
import com.edgecloud.device.exception.*;
import com.edgecloud.device.repository.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

class DeviceManagementServiceImplTest {
    @Mock EdgeDeviceRepository devices;
    @Mock DeviceLifecycleHistoryRepository history;
    DeviceManagementServiceImpl service;
    final UUID id = UUID.randomUUID(), actor = UUID.randomUUID();
    @BeforeEach void setup() { MockitoAnnotations.openMocks(this); service = new DeviceManagementServiceImpl(devices, history); }
    DeviceManagementRequest request(String name) { return new DeviceManagementRequest(name, "SENSOR", "10.0.0.1", "Edge sensor", "Lab", "1.2", "Linux"); }
    EdgeDevice device(boolean active) { EdgeDevice d = new EdgeDevice(); ReflectionTestUtils.setField(d, "id", id); d.setDeviceName("alpha"); d.setDeviceType("SENSOR"); d.setIpAddress("10.0.0.1"); d.prePersist(); d.setActive(active); return d; }

    @Test void registersDeviceAndHistory() { when(devices.save(any())).thenAnswer(i -> { EdgeDevice d=i.getArgument(0); ReflectionTestUtils.setField(d,"id",id); d.prePersist(); return d; }); var result=service.register(request("alpha"),actor); assertEquals(id,result.deviceId()); assertTrue(result.active()); verify(history).save(any()); }
    @Test void rejectsDuplicateRegistration() { when(devices.existsByDeviceName("alpha")).thenReturn(true); assertThrows(DuplicateDeviceException.class,()->service.register(request("alpha"),actor)); }
    @Test void updatesMetadata() { EdgeDevice d=device(true); when(devices.findById(id)).thenReturn(Optional.of(d)); when(devices.save(d)).thenReturn(d); assertEquals("bravo",service.update(id,request("bravo"),actor).name()); verify(history).save(any()); }
    @Test void deactivatesAndReactivates() { EdgeDevice d=device(true); when(devices.findById(id)).thenReturn(Optional.of(d)); when(devices.save(d)).thenReturn(d); assertFalse(service.deactivate(id,actor).active()); assertTrue(service.reactivate(id,actor).active()); verify(history,times(2)).save(any()); }
    @Test void rejectsInvalidTransitionsAndActiveDeletion() { EdgeDevice d=device(true); when(devices.findById(id)).thenReturn(Optional.of(d)); assertThrows(InvalidDeviceLifecycleException.class,()->service.reactivate(id,actor)); assertThrows(InvalidDeviceLifecycleException.class,()->service.delete(id,actor)); }
    @Test void deletesOnlyInactiveAndRetainsHistory() { EdgeDevice d=device(false); when(devices.findById(id)).thenReturn(Optional.of(d)); service.delete(id,actor); verify(history).save(any()); verify(devices).delete(d); }
    @Test void returnsHistoryAfterDeviceRemoval() { when(history.findByDeviceIdOrderByOccurredAtDesc(id)).thenReturn(List.of(mock(com.edgecloud.device.entity.DeviceLifecycleHistory.class))); assertEquals(1,service.history(id).size()); }
    @Test void reportsMissingDevice() { when(devices.findById(id)).thenReturn(Optional.empty()); assertThrows(DeviceNotFoundException.class,()->service.get(id)); }
}
