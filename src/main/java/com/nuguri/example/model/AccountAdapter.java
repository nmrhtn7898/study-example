package com.nuguri.example.model;

import com.nuguri.example.entity.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

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

    public Long getId() {
        return account.getId();
    }

    public String getEmail() {
        return account.getEmail();
    }

    public String getNickname() {
        return account.getNickname();
    }

    public String getProfileImage() {
        return account.getProfileImage();
    }

}
