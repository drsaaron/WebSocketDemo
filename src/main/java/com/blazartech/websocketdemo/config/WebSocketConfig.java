/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 *
 * @author scott
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("configuring broker");
        
        // Enable a STOMP broker relay to forward messages to RabbitMQ
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost") // Your RabbitMQ host
                .setRelayPort(61613)     // Default STOMP port
                .setClientLogin("guest")
                .setClientPasscode("guest");
        
        config.setApplicationDestinationPrefixes("/app");
        
        // Use the "/user" prefix for all user-specific destinations
        config.setUserDestinationPrefix("/user"); 
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("addeding chat endpoint");
//        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();
    }
}
