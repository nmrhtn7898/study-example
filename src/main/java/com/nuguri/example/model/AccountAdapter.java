package com.nuguri.example.model;

import com.nuguri.example.controller.api.FilesApiController;
import com.nuguri.example.entity.Account;
import lombok.Getter;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter
public class AccountAdapter extends User {

    private final Account account;

    public AccountAdapter(Account account) {
        super(
                account.getEmail(),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(account.getRole().getFullName()))
        );
        this.account = account;
    }

}
