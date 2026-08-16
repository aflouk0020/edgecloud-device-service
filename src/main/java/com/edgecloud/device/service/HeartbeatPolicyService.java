package com.edgecloud.device.service; import com.edgecloud.device.dto.HeartbeatPolicyResult; import java.time.*;
public interface HeartbeatPolicyService {HeartbeatPolicyResult calculate(LocalDateTime lastHeartbeat,LocalDateTime onlineSince,int intervalSeconds,int timeoutSeconds,LocalDateTime now);}
