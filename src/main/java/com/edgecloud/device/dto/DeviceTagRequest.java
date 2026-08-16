package com.edgecloud.device.dto; import jakarta.validation.constraints.*;
public record DeviceTagRequest(@NotBlank @Size(max=50) String name,@Size(max=500) String description){}
