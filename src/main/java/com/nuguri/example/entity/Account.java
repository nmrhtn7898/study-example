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
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private ProfileImage profileImage;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<ChatSubscription> subscriptions = new ArrayList<>();

    @Builder
    public Account(Long id, String email, String nickname, String name,
                   String password, Role role, ProfileImage profileImage) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.password = password;
        this.role = role;
        this.profileImage = profileImage;
    }

}
