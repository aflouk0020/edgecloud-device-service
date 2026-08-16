package com.edgecloud.device.controller;
import com.edgecloud.device.dto.*; import com.edgecloud.device.security.EdgeCloudJwtAuthenticationToken; import com.edgecloud.device.service.DeviceConfigurationService;
import jakarta.validation.Valid; import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/management")
public class DeviceConfigurationController {
 private final DeviceConfigurationService service; public DeviceConfigurationController(DeviceConfigurationService service){this.service=service;}
 @GetMapping("/{deviceId}/configuration") public DeviceConfigurationResponse get(@PathVariable UUID deviceId){return service.get(deviceId);}
 @PutMapping("/{deviceId}/configuration") public DeviceConfigurationResponse update(@PathVariable UUID deviceId,@Valid @RequestBody DeviceConfigurationRequest request,EdgeCloudJwtAuthenticationToken auth){return service.update(deviceId,request,auth.getUserId());}
 @GetMapping("/{deviceId}/configuration/history") public List<DeviceConfigurationVersionResponse> history(@PathVariable UUID deviceId){return service.history(deviceId);}
 @PostMapping("/{deviceId}/configuration/restore/{version}") public DeviceConfigurationResponse restore(@PathVariable UUID deviceId,@PathVariable long version,EdgeCloudJwtAuthenticationToken auth){return service.restore(deviceId,version,auth.getUserId());}
 @PostMapping("/{deviceId}/configuration/template/{templateId}") public DeviceConfigurationResponse apply(@PathVariable UUID deviceId,@PathVariable UUID templateId,EdgeCloudJwtAuthenticationToken auth){return service.applyTemplate(deviceId,templateId,auth.getUserId());}
 @GetMapping("/configuration-templates") public List<DeviceConfigurationTemplateResponse> templates(){return service.templates();}
 @PostMapping("/configuration-templates") public ResponseEntity<DeviceConfigurationTemplateResponse> create(@Valid @RequestBody DeviceConfigurationTemplateRequest request,EdgeCloudJwtAuthenticationToken auth){return ResponseEntity.status(HttpStatus.CREATED).body(service.createTemplate(request,auth.getUserId()));}
 @PutMapping("/configuration-templates/{id}") public DeviceConfigurationTemplateResponse updateTemplate(@PathVariable UUID id,@Valid @RequestBody DeviceConfigurationTemplateRequest request,EdgeCloudJwtAuthenticationToken auth){return service.updateTemplate(id,request,auth.getUserId());}
}
