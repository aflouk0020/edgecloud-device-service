package com.edgecloud.device.security;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class EdgeCloudJwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UUID userId;
    private final String platformRole;
    private final String token;

    public EdgeCloudJwtAuthenticationToken(UUID userId, String platformRole, String token) {
        super(authorities(platformRole));
        this.userId = userId;
        this.platformRole = platformRole;
        this.token = token;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> authorities(String platformRole) {
        return java.util.List.of(new SimpleGrantedAuthority("ROLE_" + platformRole));
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPlatformRole() {
        return platformRole;
    }
}
