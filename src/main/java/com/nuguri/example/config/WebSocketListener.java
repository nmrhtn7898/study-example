package com.nuguri.example.config;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.entity.ChatSubscription;
import com.nuguri.example.model.AccountAdapter;
import com.nuguri.example.model.Message;
import com.nuguri.example.model.MessageType;
import com.nuguri.example.repository.ChatRoomRepository;
import com.nuguri.example.repository.ChatSubscriptionRepository;
import com.nuguri.example.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebSocketListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpMessageSendingOperations sendingOperations;

    private final ChatService chatService;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatSubscriptionRepository chatSubscriptionRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleSubscribeListener(SessionSubscribeEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String roomUuid = headerAccessor.getFirstNativeHeader("room-uuid");
        Account sender = ((AccountAdapter) headerAccessor.getUser()).getAccount();

        Optional<ChatRoom> byUuid = chatRoomRepository.findByUuid(roomUuid);
        boolean isPresent = byUuid.isPresent();

        if (isPresent) {
            ChatRoom chatRoom = byUuid.get();
            boolean isContains = chatRoom
                    .getSubscriptions()
                    .stream()
                    .anyMatch(chatSubscription -> chatSubscription.getAccount().equals(sender));
            if (!isContains) {
                // 채팅방 구독 권한 없음 예외 처리, 구독 해제제
           }
        } else {
            ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom(""));
            List<Account> recipients = headerAccessor
                    .getNativeHeader("recipient-id")
                    .stream()
                    .map(id -> {
                        Account account = new Account();
                        account.setId(Long.parseLong(id));
                        return account;
                    })
                    .collect(Collectors.toList());
            recipients.add(sender);
            recipients
                    .forEach(account -> {
                        ChatSubscription subscription = ChatSubscription
                                .builder()
                                .chatRoom(chatRoom)
                                .account(account)
                                .build();
                        chatSubscriptionRepository.save(subscription);
                    });
        }
        logger.info("subscribe destination");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = (String) StompHeaderAccessor
                .wrap(event.getMessage())
                .getSessionAttributes()
                .get("username");
        if (StringUtils.hasText(username)) {
            logger.info("User Disconnected : {}", username);
            Message message = new Message();
            message.setMessageType(MessageType.LEAVE);
            sendingOperations.convertAndSend("/topic/public", message);
        }
    }

}
