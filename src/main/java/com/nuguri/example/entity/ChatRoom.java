package com.nuguri.example.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Builder
    public ChatRoom(String name) {
        this.name = name;
    }

}
