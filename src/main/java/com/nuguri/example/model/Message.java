package com.nuguri.example.model;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatRoom;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Message {

    private MessageType messageType;

    private String content;

    private AccountDto sender;

    private ChatRoomDto chatRoom;

    public ChatRoom getChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(this.chatRoom.getId());
        return chatRoom;
    }

    public void setAccount(Account account) {
        this.sender = new AccountDto();
        sender.setId(account.getId());
        sender.setEmail(account.getEmail());
        sender.setNickname(account.getNickname());
        sender.setProfileImage(account.getProfileImage());
    }

    @Data
    public static class AccountDto {
        private Long id;
        private String email;
        private String nickname;
        private String profileImage;
    }

    @Data
    public static class ChatRoomDto {
        private Long id;
        private String name;
        private List<AccountDto> members = new ArrayList<>();
    }

}
