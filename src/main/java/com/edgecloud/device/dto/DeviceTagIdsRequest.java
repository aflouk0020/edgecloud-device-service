package com.edgecloud.device.dto; import jakarta.validation.constraints.*; import java.util.*;
public record DeviceTagIdsRequest(@NotEmpty @Size(max=20) List<@NotNull UUID> tagIds){}
