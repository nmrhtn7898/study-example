package com.nuguri.example.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
public class Account extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountRole> accountRoles;

    @Builder
    public Account(String email, String password, List<AccountRole> accountRoles) {
        this.email = email;
        this.password = password;
        this.accountRoles = accountRoles;
    }

}
