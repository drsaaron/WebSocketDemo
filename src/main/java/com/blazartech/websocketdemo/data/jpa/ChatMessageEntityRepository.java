/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.blazartech.websocketdemo.data.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author scott
 */
@Repository
public interface ChatMessageEntityRepository extends JpaRepository<ChatMessageEntity, Long> {
    
    List<ChatMessageEntity> findByRecipientAndDeliveredFalse(String recipient);
}
