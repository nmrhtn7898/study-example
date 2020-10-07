package com.nuguri.example.config;

import com.nuguri.example.model.ChatMessage;
import com.nuguri.example.model.MessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpMessageSendingOperations sendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = (String) StompHeaderAccessor
                .wrap(event.getMessage())
                .getSessionAttributes()
                .get("username");
        if (StringUtils.hasText(username)) {
            logger.info("User Disconnected : {}", username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageType(MessageType.LEAVE);
            chatMessage.setSender(username);
            sendingOperations.convertAndSend("/topic/public", chatMessage);
        }
    }

}
