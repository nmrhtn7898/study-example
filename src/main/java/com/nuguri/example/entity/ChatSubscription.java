package com.nuguri.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChatSubscription extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    @Builder
    public ChatSubscription(ChatRoom chatRoom, Account account) {
        this.chatRoom = chatRoom;
        this.account = account;
    }

}
