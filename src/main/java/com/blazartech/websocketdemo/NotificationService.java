/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.blazartech.websocketdemo;

import com.blazartech.websocketdemo.data.OutputMessage;

/**
 *
 * @author aar1069
 */
public interface NotificationService {
    
    public void sendPrivateMessage(String userId, OutputMessage message);
}
