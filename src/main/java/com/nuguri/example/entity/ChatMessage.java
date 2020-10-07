package com.nuguri.example.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String content;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account recipient;

    @Builder
    public ChatMessage(String content, ChatRoom chatRoom, Account sender, Account recipient) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.recipient = recipient;
    }

}
