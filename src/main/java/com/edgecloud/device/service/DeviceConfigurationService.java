package com.edgecloud.device.service;
import com.edgecloud.device.dto.*;
import java.util.*;
public interface DeviceConfigurationService {
 DeviceConfigurationResponse get(UUID deviceId); DeviceConfigurationResponse update(UUID deviceId,DeviceConfigurationRequest request,UUID actor);
 List<DeviceConfigurationVersionResponse> history(UUID deviceId); DeviceConfigurationResponse restore(UUID deviceId,long version,UUID actor);
 List<DeviceConfigurationTemplateResponse> templates(); DeviceConfigurationTemplateResponse createTemplate(DeviceConfigurationTemplateRequest request,UUID actor); DeviceConfigurationTemplateResponse updateTemplate(UUID id,DeviceConfigurationTemplateRequest request,UUID actor); DeviceConfigurationResponse applyTemplate(UUID deviceId,UUID templateId,UUID actor);
}
