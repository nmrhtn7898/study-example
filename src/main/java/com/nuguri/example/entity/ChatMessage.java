package com.nuguri.example.entity;

import lombok.*;

import javax.persistence.*;

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

    @Builder
    public ChatMessage(String content, ChatRoom chatRoom, Account sender) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.sender = sender;
        chatRoom.getMessages().add(this);
    }

}
