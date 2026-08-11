package com.edgecloud.device.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

final class TestJwtHelper {

    private TestJwtHelper() {
    }

    static String jwt() {
        SecretKey key = Keys.hmacShaKeyFor("edgecloud-monitor-development-secret-key-for-jwt-token-generation".getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("edgecloud-test")
                .claim("userId", UUID.randomUUID().toString())
                .claim("role", "ADMIN")
                .issuedAt(Date.from(now.minusSeconds(60)))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
