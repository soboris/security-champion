package com.secchamp.chal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.secchamp.chal.util.JwtTokenUtil;

@Configuration
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    
    @Value("${app.security.whitelist}")
    private String[] whitelistedEndpoints;

    public SecurityConfig(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/files/**", "/uploads/**", "/images/**", "/css/**", "/js/**", "/pages/**", "/ws/**").permitAll()
                .requestMatchers(whitelistedEndpoints).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, whitelistedEndpoints), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}