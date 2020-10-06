package com.nuguri.example.entity;

import com.nuguri.example.model.Role;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class AccountRole {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public AccountRole(Account account, Role role) {
        this.account = account;
        this.role = role;
    }
}
