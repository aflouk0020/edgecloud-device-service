package com.edgecloud.device.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class DeviceSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, EdgeCloudJwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/management").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/management/configuration-templates").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/management/configuration-templates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/management/*/maintenance", "/management/projects/*/devices/*/maintenance").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers(HttpMethod.DELETE, "/management/*/maintenance", "/management/projects/*/devices/*/maintenance").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/management/projects/**").hasAnyRole("ADMIN", "OPERATOR", "PROJECT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/management/**").hasRole("ADMIN")
                        .requestMatchers("/management/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers(HttpMethod.GET, "/inventory").hasAnyRole("ADMIN", "OPERATOR", "PROJECT_ADMIN")
                        .anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                com.edgecloud.device.web.SecurityExceptionHandler.writeUnauthorized(response, "Authentication required"))
                        .accessDeniedHandler((request, response, exception) ->
                                com.edgecloud.device.web.SecurityExceptionHandler.writeForbidden(response, "Insufficient role")))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
