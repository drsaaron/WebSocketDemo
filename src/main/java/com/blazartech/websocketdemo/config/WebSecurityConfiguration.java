/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 *
 * @author aar1069
 */
@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
@Slf4j
public class WebSecurityConfiguration {

    private final AuthenticationEntryPoint aep = (request, response, authException) -> {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    };

/*    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        log.info("setting global");
        auth
                .inMemoryAuthentication()
                .withUser("user1")
                .password(passwordEncoder.encode("user1Pass"))
                .authorities("ROLE_USER");
    }*/

 /*  @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(expressionInterceptUrlRegistry
                -> expressionInterceptUrlRegistry.requestMatchers("/securityNone").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.authenticationEntryPoint(aep));
        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }*/
  /*  @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers("/", "/login").permitAll()
                                .anyRequest().authenticated()
                )
                //   .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(f -> f.disable())
                .httpBasic(b -> b.authenticationEntryPoint(aep));

        return http.build();

    }*/
   /* @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        // Authorize messages with specific destinations
        messages
                .simpDestMatchers("/topic/system/notifications").permitAll() // Allow anyone to subscribe to public notifications
                .simpDestMatchers("/user/**", "/topic/users/{user}/**").authenticated() // User-specific destinations require auth
                .simpDestMatchers("/app/**").authenticated() // Messages to application handlers require auth
                .anyMessage().denyAll(); // Deny all other message types/destinations by default
        return messages.build();
    }*/

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        // Authorize messages with specific destinations
        messages
                .simpDestMatchers("/topic/system/notifications").permitAll() // Allow anyone to subscribe to public notifications
                .simpDestMatchers("/user/**", "/topic/users/{user}/**").authenticated() // User-specific destinations require auth
                .simpDestMatchers("/app/**").authenticated() // Messages to application handlers require auth
                .simpDestMatchers("/topic/messages").authenticated()
                .anyMessage().denyAll(); // Deny all other message types/destinations by default
        return messages.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user
                = User.builder()
                        .username("user1")
                        .password(encoder.encode("user1Pass"))
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }

}
