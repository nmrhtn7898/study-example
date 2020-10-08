package com.nuguri.example.entity;

import com.nuguri.example.model.RoomType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<ChatSubscription> subscriptions = new ArrayList<>();

    @OrderBy("created asc")
    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();

    @Builder
    public ChatRoom(String name, RoomType roomType) {
        this.name = name;
        this.roomType = roomType;
    }

}
