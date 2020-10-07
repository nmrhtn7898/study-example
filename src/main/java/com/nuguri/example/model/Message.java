package com.nuguri.example.model;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatRoom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private MessageType messageType;

    private String content;

    private ChatRoom chatRoom;

    private Account recipient;

}
