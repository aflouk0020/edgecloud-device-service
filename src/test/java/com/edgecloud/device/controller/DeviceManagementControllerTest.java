package com.edgecloud.device.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edgecloud.device.security.*;
import com.edgecloud.device.service.DeviceManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeviceManagementController.class)
@Import({DeviceSecurityConfig.class, EdgeCloudJwtAuthenticationFilter.class, JwtService.class})
class DeviceManagementControllerTest {
    @Autowired MockMvc mvc; @MockBean DeviceManagementService service;
    final String body="{\"name\":\"alpha\",\"type\":\"SENSOR\",\"ipAddress\":\"10.0.0.1\"}";
    @Test void unauthenticatedIsRejected() throws Exception { mvc.perform(post("/management").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isUnauthorized()); }
    @Test void viewerIsForbidden() throws Exception { mvc.perform(post("/management").header("Authorization", bearer("VIEWER")).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden()); }
    @Test void operatorCannotRegisterOrDeleteButCanUpdate() throws Exception { String token=bearer("OPERATOR"); mvc.perform(post("/management").header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden()); mvc.perform(put("/management/11111111-1111-1111-1111-111111111111").header("Authorization",token).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()); mvc.perform(delete("/management/11111111-1111-1111-1111-111111111111").header("Authorization",token)).andExpect(status().isForbidden()); }
    @Test void adminCanRegister() throws Exception { mvc.perform(post("/management").header("Authorization",bearer("ADMIN")).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()); }
    private String bearer(String role) { return "Bearer "+TestJwtHelper.jwt(role); }
}
