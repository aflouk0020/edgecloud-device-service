package com.edgecloud.device.dto; import jakarta.validation.constraints.*; import java.util.*;
public record DeviceIdsRequest(@NotEmpty @Size(max=100) List<@NotNull UUID> deviceIds){}
