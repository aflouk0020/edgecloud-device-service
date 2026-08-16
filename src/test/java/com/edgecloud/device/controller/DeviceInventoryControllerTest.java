package com.edgecloud.device.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgecloud.device.dto.DeviceInventoryResponse;
import com.edgecloud.device.security.DeviceSecurityConfig;
import com.edgecloud.device.security.EdgeCloudJwtAuthenticationFilter;
import com.edgecloud.device.security.JwtService;
import com.edgecloud.device.service.DeviceAnalyticsService;
import com.edgecloud.device.service.DeviceInventoryService;
import com.edgecloud.device.service.DeviceRegistrationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DeviceController.class)
@Import({DeviceSecurityConfig.class, EdgeCloudJwtAuthenticationFilter.class, JwtService.class})
class DeviceInventoryControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean DeviceRegistrationService registrationService;
    @MockBean DeviceAnalyticsService analyticsService;
    @MockBean DeviceInventoryService inventoryService;

    @Test
    void rejectsUnauthenticatedInventoryAccess() throws Exception {
        mockMvc.perform(get("/inventory")).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsViewerRole() throws Exception {
        mockMvc.perform(get("/inventory").header("Authorization", bearer("VIEWER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void permitsAdminAndReturnsStablePageShape() throws Exception {
        when(inventoryService.getInventory(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new DeviceInventoryResponse(List.of(), 0, 20, 0, 0, "name", "asc"));

        mockMvc.perform(get("/inventory").header("Authorization", bearer("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.devices").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void reportsInvalidParametersAsBadRequest() throws Exception {
        when(inventoryService.getInventory(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Unsupported sort field: unknown"));

        mockMvc.perform(get("/inventory?sort=unknown").header("Authorization", bearer("OPERATOR")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported sort field: unknown"));
    }

    private String bearer(String role) {
        return "Bearer " + TestJwtHelper.jwt(role);
    }
}
