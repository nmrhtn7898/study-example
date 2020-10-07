package com.nuguri.example.service;

import com.nuguri.example.entity.ChatMessage;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.repository.ChatMessageRepository;
import com.nuguri.example.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    private final ChatRoomRepository chatRoomRepository;


}
