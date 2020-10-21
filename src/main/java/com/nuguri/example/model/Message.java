package com.nuguri.example.model;

import lombok.Data;

@Data
public class Message {

    private MessageType messageType;

    private String content;

    private MessageAccountDto sender;

    private Long chatRoomId;

    public void setAccountAdapter(AccountAdapter accountAdapter) {
        this.sender = new MessageAccountDto(accountAdapter);
    }

    @Data
    public static class MessageAccountDto {
        private Long id;
        private String email;
        ;

        public MessageAccountDto(AccountAdapter accountAdapter) {
            this.id = accountAdapter.getAccount().getId();
            this.email = accountAdapter.getAccount().getEmail();
        }
    }

}
