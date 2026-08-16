package com.edgecloud.device.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgecloud.device.security.*;
import com.edgecloud.device.service.*;
import com.edgecloud.device.exception.ProjectScopeAccessException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeviceMaintenanceController.class)
@Import({DeviceSecurityConfig.class, EdgeCloudJwtAuthenticationFilter.class, JwtService.class})
class DeviceMaintenanceControllerTest {
    @Autowired MockMvc mvc;
    @MockBean DeviceMaintenanceService service;
    @MockBean ProjectScopeClient projects;
    UUID deviceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    UUID projectId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    void rejectsUnauthenticatedAndViewer() throws Exception {
        mvc.perform(get(global())).andExpect(status().isUnauthorized());
        mvc.perform(post(global()).header("Authorization", bearer("VIEWER"))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAndOperatorCanEnableDisableAndRead() throws Exception {
        for (String role : new String[]{"ADMIN", "OPERATOR"}) {
            String token = bearer(role);
            mvc.perform(post(global()).header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"reason\":\"Planned\",\"scheduledEndAt\":\"2099-01-01T00:00:00\"}"))
                    .andExpect(status().isOk());
            mvc.perform(get(global()).header("Authorization", token)).andExpect(status().isOk());
            mvc.perform(get(global() + "/history").header("Authorization", token)).andExpect(status().isOk());
            mvc.perform(delete(global()).header("Authorization", token)).andExpect(status().isNoContent());
        }
    }

    @Test
    void validatesReasonLength() throws Exception {
        String body = "{\"reason\":\"" + "x".repeat(501) + "\"}";
        mvc.perform(post(global()).header("Authorization", bearer("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void projectEndpointsEnforceAssociationAndMutationRoles() throws Exception {
        String projectPath = "/management/projects/" + projectId + "/devices/" + deviceId + "/maintenance";
        String projectAdmin = bearer("PROJECT_ADMIN");
        mvc.perform(get(projectPath).header("Authorization", projectAdmin)).andExpect(status().isOk());
        verify(projects).requireDeviceAssociation(eq(projectId), eq(deviceId), anyString());
        mvc.perform(post(projectPath).header("Authorization", projectAdmin)
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());

        mvc.perform(post(projectPath).header("Authorization", bearer("OPERATOR"))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        verify(projects, times(2)).requireDeviceAssociation(eq(projectId), eq(deviceId), anyString());
    }

    @Test
    void crossProjectAccessIsForbidden() throws Exception {
        doThrow(new ProjectScopeAccessException("Device is not assigned to project"))
                .when(projects).requireDeviceAssociation(eq(projectId), eq(deviceId), anyString());
        String projectPath = "/management/projects/" + projectId + "/devices/" + deviceId + "/maintenance";
        mvc.perform(get(projectPath).header("Authorization", bearer("PROJECT_ADMIN")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    private String global() { return "/management/" + deviceId + "/maintenance"; }
    private String bearer(String role) { return "Bearer " + TestJwtHelper.jwt(role); }
}
