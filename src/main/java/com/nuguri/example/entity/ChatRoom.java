package com.nuguri.example.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid = UUID.randomUUID().toString();

    private String name;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<ChatSubscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();

    @Builder
    public ChatRoom(String name) {
        this.name = name;
    }

}
