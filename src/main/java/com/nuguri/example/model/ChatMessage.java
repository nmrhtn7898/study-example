package com.nuguri.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    private MessageType messageType;

    private String content;

    private String sender;

}
