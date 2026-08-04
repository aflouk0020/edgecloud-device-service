package com.edgecloud.device.controller;

import com.edgecloud.device.dto.DeviceHealthDataState;
import com.edgecloud.device.dto.DeviceHealthQueryRequest;
import com.edgecloud.device.dto.DeviceHealthRecord;
import com.edgecloud.device.dto.DeviceHealthResponse;
import com.edgecloud.device.security.DeviceSecurityConfig;
import com.edgecloud.device.security.EdgeCloudJwtAuthenticationFilter;
import com.edgecloud.device.security.JwtService;
import com.edgecloud.device.service.DeviceHealthService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalDeviceHealthController.class)
@org.springframework.context.annotation.Import({DeviceSecurityConfig.class, EdgeCloudJwtAuthenticationFilter.class, JwtService.class})
class InternalDeviceHealthControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean DeviceHealthService deviceHealthService;

    @Test
    void rejectsMissingJwt() throws Exception {
        mockMvc.perform(post("/internal/device-health")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsDeviceHealthWhenAuthorized() throws Exception {
        UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(deviceHealthService.getDeviceHealth(any()))
                .thenReturn(new DeviceHealthResponse(
                        Instant.parse("2026-08-04T10:00:00Z"),
                        List.of(new DeviceHealthRecord(deviceId, "Alpha", "sensor", "10.0.0.1", "ONLINE", Instant.parse("2026-08-04T09:59:00Z"), Instant.parse("2026-08-04T09:59:00Z"), DeviceHealthDataState.COMPLETE))));

        mockMvc.perform(post("/internal/device-health")
                        .header("Authorization", bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceIds\":[\"11111111-1111-1111-1111-111111111111\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devices[0].deviceId").value(deviceId.toString()))
                .andExpect(jsonPath("$.devices[0].currentStatus").value("ONLINE"));
    }

    private String bearerToken() {
        return "Bearer " + TestJwtHelper.jwt();
    }
}
