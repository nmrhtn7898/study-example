package com.nuguri.example.model;

import lombok.Data;

@Data
public class Message {

    private MessageType messageType;

    private String content;

    private Long senderId;

    private Long chatRoomId;

}
