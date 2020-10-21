package com.nuguri.example.repository.impl;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ProfileImage;
import com.nuguri.example.entity.QAccount;
import com.nuguri.example.model.Role;
import com.nuguri.example.repository.custom.AccountRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import static com.nuguri.example.entity.QAccount.account;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public void updateAccount(Account account) {
        String email = account.getEmail();
        String nickname = account.getNickname();
        String name = account.getName();
        String password = account.getPassword();
        ProfileImage profileImage = account.getProfileImage();
        Role role = account.getRole();
        JPAUpdateClause updateQuery = jpaQueryFactory.update(QAccount.account);
        if (StringUtils.hasText(email)) {
            updateQuery.set(QAccount.account.email, email);
        }
        if (StringUtils.hasText(nickname)) {
            updateQuery.set(QAccount.account.nickname, nickname);
        }
        if (StringUtils.hasText(name)) {
            updateQuery.set(QAccount.account.name, name);
        }
        if (StringUtils.hasText(password)) {
            updateQuery.set(QAccount.account.password, password);
        }
        if (profileImage != null) {
            updateQuery.set(QAccount.account.profileImage, profileImage);
        }
        if (role != null) {
            updateQuery.set(QAccount.account.role, role);
        }
        updateQuery.execute();
    }


}
