package com.nuguri.example.service;

import com.nuguri.example.entity.Account;
import com.nuguri.example.model.Role;
import com.nuguri.example.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return User
                .builder()
                .username(email)
                .password(account.getPassword())
                .authorities(
                        account
                                .getAccountRoles()
                                .stream()
                                .map(a -> new SimpleGrantedAuthority(a.getRole().getFullName()))
                                .collect(Collectors.toList())
                )
                .build();
    }

}
