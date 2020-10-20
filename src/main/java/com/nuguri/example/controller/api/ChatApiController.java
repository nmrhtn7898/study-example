package com.nuguri.example.controller.api;

import com.nuguri.example.annotation.WebSocketPrincipal;
import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatMessage;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.model.AccountAdapter;
import com.nuguri.example.model.Message;
import com.nuguri.example.model.RoomType;
import com.nuguri.example.repository.ChatMessageRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MessageMapping 애노테이션이 붙은 핸들러는 WebSocketConfig
 * setApplicationDestinationPrefixes("/publish") 로 설정한 접두어 app 붙은 경로로 접근하게 된다.
 * ex) publish/topic/1, publish/topic/2
 * enableSimpleBroker("/subscribe") 로 설정한 접두어 subscribe 붙은 경로로 destination 구독 접근하게 된다.
 * ex) subscribe/topic/1, subscribe/**
 */
@RestController
@RequiredArgsConstructor
public class ChatApiController {

    private final SimpMessageSendingOperations sendingOperations;

    private final ChatMessageRepository chatMessageRepository;

    private final EntityManager entityManager;

    @MessageMapping("/topic/{chatRoomId}")
    public void publicMessage(@Payload Message message, @DestinationVariable Long chatRoomId,
                              @WebSocketPrincipal AccountAdapter accountAdapter) {
        ChatMessage chatMessage = ChatMessage
                .builder()
                .content(message.getContent())
                .chatRoom(message.getChatRoom())
                .sender(accountAdapter.getAccount())
                .build();
        chatMessageRepository.save(chatMessage);
        message.setAccount(accountAdapter.getAccount());
        sendingOperations.convertAndSend("/subscribe/topic/" + chatRoomId, message);
    }

    @GetMapping("/api/v1/chatroom")
    public ResponseEntity queryChatRoom(@AuthenticationPrincipal AccountAdapter accountAdapter) {
        List<ChatRoomResponse> chatRooms = entityManager
                .createQuery("select distinct r from ChatRoom r join fetch r.subscriptions s join fetch s.account a where r.id in " +
                        "(select r.id from ChatRoom r where exists (select 1 from r.subscriptions s where s.account.id = ?1))", ChatRoom.class)
                .setParameter(1, accountAdapter.getAccount().getId())
                .getResultStream()
                .map(ChatRoomResponse::new)
                .sorted((a, b) -> {
                    List<ChatMessageResponse> m = a.getMessages();
                    List<ChatMessageResponse> m2 = b.getMessages();
                    if (m.isEmpty() && !m2.isEmpty()) {
                        return 1;
                    } else if (!m.isEmpty() && m2.isEmpty()) {
                        return -1;
                    } else if (m.isEmpty()) {
                        return 0;
                    } else if (m.get(m.size() - 1).getTimeStamp().isBefore(m2.get(m2.size() - 1).getTimeStamp())) {
                        return 1;
                    } else {
                        return -1;
                    }
                }).collect(Collectors.toList());
        return ResponseEntity.ok(chatRooms);
    }

    @Data
    public static class ChatRoomResponse {
        private Long id;
        private String name;
        private RoomType roomType;
        private List<AccountResponse> members;
        private List<ChatMessageResponse> messages;

        public ChatRoomResponse(ChatRoom chatRoom) {
            this.id = chatRoom.getId();
            this.name = chatRoom.getName();
            this.roomType = chatRoom.getRoomType();
            this.members = chatRoom
                    .getSubscriptions()
                    .stream()
                    .map(chatSubscription -> new AccountResponse(chatSubscription.getAccount()))
                    .collect(Collectors.toList());
            this.messages = chatRoom
                    .getMessages()
                    .stream()
                    .map(ChatMessageResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class ChatMessageResponse {
        private String content;
        private AccountResponse sender;
        private LocalDateTime timeStamp;

        public ChatMessageResponse(ChatMessage chatMessage) {
            this.content = chatMessage.getContent();
            this.sender = new AccountResponse(chatMessage.getSender());
            this.timeStamp = chatMessage.getCreated();
        }
    }

    @Data
    public static class AccountResponse {
        private Long id;
        private String email;
        private String nickname;
        private String profileImage;

        public AccountResponse(Account account) {
            this.id = account.getId();
            this.email = account.getEmail();
            this.nickname = account.getNickname();
            this.profileImage = account.getProfileImage();
        }
    }

}
