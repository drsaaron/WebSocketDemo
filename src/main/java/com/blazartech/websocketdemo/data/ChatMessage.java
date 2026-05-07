/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.blazartech.websocketdemo.data;

import lombok.Data;

/**
 *
 * @author scott
 */
@Data
public class ChatMessage {
    
    private Long id;
    private String sender;
    private String content;
    private String recipient; // Null/empty if public message
    
    private String clientMessageId;
}
