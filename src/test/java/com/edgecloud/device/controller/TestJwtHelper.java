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
        return Jwts.builder()
                .subject("edgecloud-test")
                .claim("userId", UUID.randomUUID().toString())
                .claim("role", "ADMIN")
                .issuedAt(Date.from(Instant.parse("2026-08-04T09:00:00Z")))
                .expiration(Date.from(Instant.parse("2026-08-05T12:00:00Z")))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
