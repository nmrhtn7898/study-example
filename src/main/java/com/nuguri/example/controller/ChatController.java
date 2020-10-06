package com.nuguri.example.controller;

import com.nuguri.example.model.ChatMessage;
import com.nuguri.example.model.MessageType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MessageMapping 애노테이션이 붙은 핸들러는 WebSocketConfig
 * setApplicationDestinationPrefixes("/app") 로 설정한 접두어 app 붙은 경로로 접근하게 된다.
 * ex) app/chat/send, app/chat/add
 */
@Controller
public class ChatController {

    @MessageMapping("/topic")
    @SendTo("/topic/public")
    public ChatMessage publicMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        if (chatMessage.getMessageType().equals(MessageType.JOIN)) {
            headerAccessor
                    .getSessionAttributes()
                    .put("username", chatMessage.getSender());
        }
        return chatMessage;
    }

    @MessageMapping("/queue")
    @SendToUser("/queue/direct")
    public ChatMessage directMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        if (chatMessage.getMessageType().equals(MessageType.JOIN)) {
            headerAccessor
                    .getSessionAttributes()
                    .put("username", chatMessage.getSender());
        }
        return chatMessage;
    }

}
