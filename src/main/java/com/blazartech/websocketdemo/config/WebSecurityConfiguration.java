/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author aar1069
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // Create 3 users for authentication
        UserDetails alice = User.withUsername("alice").password("{noop}password").roles("USER").build();
        UserDetails bob = User.withUsername("bob").password("{noop}password").roles("USER").build();
        UserDetails charlie = User.withUsername("charlie").password("{noop}password").roles("USER").build();
        UserDetails auditor = User.withUsername("auditor").password("{noop}password").roles("USER").build();
        
        return new InMemoryUserDetailsManager(alice, bob, charlie, auditor);
    }

    private final AuthenticationEntryPoint aep = (request, response, authException) -> {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // Disabled for local testing simplicity
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

}
