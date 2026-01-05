/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo;

import com.blazartech.websocketdemo.data.OutputMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author aar1069
 */
@Component
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Override
    public void sendPrivateMessage(String userId, OutputMessage message) {
        log.info("sending private message {} to user {}", message, userId);
        
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/private-messages", 
            message
        );
    }
    
}
