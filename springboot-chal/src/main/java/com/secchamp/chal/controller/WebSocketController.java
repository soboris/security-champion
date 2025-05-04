package com.secchamp.chal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
  
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String processMessage(String message) {
        logger.info("Message received: {}", message);
        return message;
    }

    @MessageMapping("/status")
    @SendTo("/topic/status")
    public String sendStatusUpdate(String status) {
        logger.info("Status update received: {}", status);
        return status;
    }

    // New method for broadcasting random ticket information
    @MessageMapping("/tickets")
    @SendTo("/topic/tickets")
    public String sendTicketUpdate(String ticketInfo) {
        logger.info("Ticket update received: {}", ticketInfo);
        return ticketInfo;
    }
}
