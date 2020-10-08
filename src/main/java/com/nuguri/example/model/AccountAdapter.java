package com.nuguri.example.model;

import com.nuguri.example.entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

@Getter
public class AccountAdapter extends User {

    private final Account account;

    public AccountAdapter(Account account) {
        super(
                account.getEmail(),
                account.getPassword(),
                account
                        .getRoles()
                        .stream()
                        .map(r -> new SimpleGrantedAuthority(r.getFullName()))
                        .collect(Collectors.toList())
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
