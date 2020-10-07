package com.nuguri.example.entity;

import com.nuguri.example.model.Role;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@Entity
public class Account extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Role> roles;

    private String profileImage;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<ChatSubscription> subscriptions = new ArrayList<>();

    @Builder
    public Account(String email, String nickname, String password, List<Role> roles, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.roles = roles;
        this.profileImage = profileImage;
    }

}
