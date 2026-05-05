/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo.controller;

import com.blazartech.websocketdemo.data.ChatMessage;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author scott
 */
@RestController
@Slf4j
public class ChatController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Helper endpoint for the frontend to fetch the authenticated username
    @GetMapping("/api/username")
    public String currentUserName(Principal principal) {
        return principal.getName();
    }

    // Handles public messages
    @MessageMapping("/chat.public")
    @SendTo("/topic/public")
    public ChatMessage sendPublicMessage(@Payload ChatMessage message, Principal principal) {
        message.setSender(principal.getName());
        return message;
    }

    // Handles private messages
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage message, Principal principal) {
        message.setSender(principal.getName());
        
        // Route the message to the specific user's queue: /user/{recipient}/queue/private
        messagingTemplate.convertAndSendToUser(
                message.getRecipient(), 
                "/queue/private", 
                message
        );
    }
}
