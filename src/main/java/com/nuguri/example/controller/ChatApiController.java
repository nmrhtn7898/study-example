package com.nuguri.example.controller;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatMessage;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.entity.ChatSubscription;
import com.nuguri.example.model.AccountAdapter;
import com.nuguri.example.model.Message;
import com.nuguri.example.repository.ChatMessageRepository;
import com.nuguri.example.repository.ChatRoomRepository;
import com.nuguri.example.service.ChatService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MessageMapping 애노테이션이 붙은 핸들러는 WebSocketConfig
 * setApplicationDestinationPrefixes("/app") 로 설정한 접두어 app 붙은 경로로 접근하게 된다.
 * ex) app/chat/send, app/chat/add
 */
@RestController
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    private final ChatMessageRepository chatMessageRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final EntityManager entityManager;

    @MessageMapping("/topic")
    @SendTo("/topic/public")
    public Message publicMessage(@Payload Message message, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        ChatMessage chatMessage = ChatMessage
                .builder()
                .content(message.getContent())
                .chatRoom(message.getChatRoom())
                .sender(accountAdapter.getAccount())
                .recipient(message.getRecipient())
                .build();
        return message;
    }

    @MessageMapping("/queue")
    @SendToUser("/queue/direct")
    public Message directMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        return message;
    }

    @GetMapping("/api/v1/chatroom")
    public ResponseEntity queryChatRoom(@AuthenticationPrincipal AccountAdapter accountAdapter) {
        List<ChatRoomResponse> chatRooms = entityManager
                .createQuery("select r from ChatRoom r join fetch r.subscriptions s join fetch s.account a where r.id in " +
                        "(select r.id from ChatRoom r where exists (select 1 from r.subscriptions s where s.account.id = ?1))", ChatRoom.class)
                .setParameter(1, accountAdapter.getAccount().getId())
                .getResultStream()
                .map(ChatRoomResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatRooms);
    }

    @Data
    public static class ChatRoomResponse {
        private String uuid;
        private String name;
        private List<AccountResponse> members;
        private List<ChatMessageResponse> messages;
        public ChatRoomResponse(ChatRoom chatRoom) {
            this.uuid = chatRoom.getUuid();
            this.name = chatRoom.getName();
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
        private AccountResponse recipient;
        public ChatMessageResponse(ChatMessage chatMessage) {
            this.content = chatMessage.getContent();
            this.sender = new AccountResponse(chatMessage.getSender());
            this.recipient = new AccountResponse(chatMessage.getRecipient());
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
