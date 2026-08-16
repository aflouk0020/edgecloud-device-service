package com.edgecloud.device.dto; import jakarta.validation.constraints.*;
public record DeviceGroupRequest(@NotBlank @Size(max=100) String name,@Size(max=500) String description){}
