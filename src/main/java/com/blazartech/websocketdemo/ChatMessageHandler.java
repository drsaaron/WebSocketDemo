/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo;

import com.blazartech.websocketdemo.data.ChatMessage;
import com.blazartech.websocketdemo.data.OutputMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 *
 * @author scott
 */
@Controller
@Slf4j
public class ChatMessageHandler {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(ChatMessage message) throws Exception {
        log.info("handling message {}", message);
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }
}
