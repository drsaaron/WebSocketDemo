/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo.controller;

import com.blazartech.websocketdemo.NotificationService;
import com.blazartech.websocketdemo.data.ChatMessage;
import com.blazartech.websocketdemo.data.jpa.ChatMessageEntity;
import com.blazartech.websocketdemo.data.jpa.ChatMessageEntityRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author scott
 */
@RestController
@Slf4j
public class ChatController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ChatMessageEntityRepository messageRepository;

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
        
        // notify the auditor
        log.info("notifying auditor");
        saveAndSendPrivateMessage("auditor", message);
        
        return message;
    }
    
    private void saveAndSendPrivateMessage(String recipient, ChatMessage message) {
        
        // 1. Save to Database as UNDELIVERED
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setSender(message.getSender());
        entity.setRecipient(recipient);
        entity.setContent(message.getContent());
        entity.setDelivered(false);
        entity = messageRepository.save(entity);

        // Attach the DB ID to the payload
        message.setId(entity.getId());
        
        // send
        notificationService.sendPrivateMessage(recipient, message);
    }

    // Handles private messages
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage message, Principal principal) {
        message.setSender(principal.getName());
        
        // 1. Save to Database as UNDELIVERED
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setSender(message.getSender());
        entity.setRecipient(message.getRecipient());
        entity.setContent(message.getContent());
        entity.setDelivered(false);
        entity = messageRepository.save(entity);

        // Attach the DB ID to the payload
        message.setId(entity.getId());
        
        // Route the message to the specific user's queue: /user/{recipient}/queue/private
        saveAndSendPrivateMessage(message.getRecipient(), message);
    }
    
    // NEW: Fetch missed messages upon login
    @GetMapping("/api/messages/offline")
    public List<ChatMessage> getOfflineMessages(Principal principal) {
        log.info("getting offline messages for {}", principal);
        List<ChatMessageEntity> entities = messageRepository.findByRecipientAndDeliveredFalse(principal.getName());
        List<ChatMessage> messages = new ArrayList<>();
        
        for (ChatMessageEntity e : entities) {
            ChatMessage msg = new ChatMessage();
            msg.setId(e.getId());
            msg.setSender(e.getSender());
            msg.setRecipient(e.getRecipient());
            msg.setContent(e.getContent());
            messages.add(msg);

            // Mark as delivered now that they've been fetched
            e.setDelivered(true);
            messageRepository.save(e);
        }
        return messages;
    }

    // NEW: Acknowledge a live message was received
    @PostMapping("/api/messages/{id}/delivered")
    public void markDelivered(@PathVariable Long id) {
        log.info("marking message {} delivered", id);
        messageRepository.findById(id).ifPresent(e -> {
            e.setDelivered(true);
            messageRepository.save(e);
        });
    }
}
