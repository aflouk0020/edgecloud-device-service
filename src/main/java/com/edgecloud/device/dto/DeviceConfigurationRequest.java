package com.edgecloud.device.dto;
import com.edgecloud.device.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record DeviceConfigurationRequest(
 @Min(5) @Max(86400) int pollingIntervalSeconds,
 @Min(5) @Max(3600) int heartbeatIntervalSeconds,
 @Min(10) @Max(86400) int heartbeatTimeoutSeconds,
 @Min(5) @Max(86400) int metricsCollectionIntervalSeconds,
 @NotNull DeviceEnvironment environment,
 @Size(max=500) @Pattern(regexp="^$|https?://[^\\s]+$",message="API endpoint must be an HTTP(S) URL") String apiEndpoint,
 @NotNull DeviceLoggingLevel loggingLevel,
 @Size(max=20) List<@NotBlank @Size(max=50) String> tags) {}
