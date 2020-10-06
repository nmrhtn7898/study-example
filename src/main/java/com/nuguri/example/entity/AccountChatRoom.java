package com.nuguri.example.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class AccountChatRoom extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Builder
    public AccountChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

}
