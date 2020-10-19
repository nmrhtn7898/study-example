package com.nuguri.example;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.entity.ChatSubscription;
import com.nuguri.example.model.Role;
import com.nuguri.example.model.RoomType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableWebSecurity
@EnableWebSocketMessageBroker
@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class ExampleApplication {

    private final EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        Map<String, List<String>> roleHierarchyMap = new HashMap<>();
        roleHierarchyMap.put(Role.PREFIX + Role.ADMIN, Collections.singletonList(Role.PREFIX + Role.USER));
        String hierarchy = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Component
    public class InitRunner implements ApplicationRunner {

        @Transactional
        @Override
        public void run(ApplicationArguments args) throws Exception {
            PasswordEncoder passwordEncoder = passwordEncoder();
            Account user = Account
                    .builder()
                    .email("user@naver.com")
                    .nickname("유저")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Collections.singletonList(Role.USER))
                    .profileImage("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRo2s5FzdZZhH7o5Ylzyc88y4lW1HmW-khXIQ&usqp=CAU")
                    .build();
            Account admin = Account
                    .builder()
                    .email("admin@naver.com")
                    .nickname("관리자")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Collections.singletonList(Role.ADMIN))
                    .profileImage("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRh9nDTzbOiZQsJ99PmLuHsBwV2h_zdofEgLA&usqp=CAU")
                    .build();
            Account testUser = Account
                    .builder()
                    .email("test@naver.com")
                    .nickname("테스트계정")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Collections.singletonList(Role.USER))
                    .profileImage("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR-J7QBqEmjThk60iOR_DvL3hjM3MPTub1Ezw&usqp=CAU")
                    .build();
            entityManager.persist(user);
            entityManager.persist(admin);
            entityManager.persist(testUser);
            ChatRoom chatRoom = ChatRoom
                    .builder()
                    .name("유저와 관리자의 채팅방")
                    .roomType(RoomType.DIRECT)
                    .build();
            ChatRoom chatRoom2 = ChatRoom
                    .builder()
                    .name("유저와 테스트유저의 테스트 채팅방")
                    .roomType(RoomType.DIRECT)
                    .build();
            entityManager.persist(chatRoom);
            entityManager.persist(chatRoom2);
            ChatSubscription subscription = ChatSubscription
                    .builder()
                    .account(user)
                    .chatRoom(chatRoom)
                    .build();
            ChatSubscription subscription2 = ChatSubscription
                    .builder()
                    .account(admin)
                    .chatRoom(chatRoom)
                    .build();
            ChatSubscription subscription3 = ChatSubscription
                    .builder()
                    .account(user)
                    .chatRoom(chatRoom2)
                    .build();
            ChatSubscription subscription4 = ChatSubscription
                    .builder()
                    .account(testUser)
                    .chatRoom(chatRoom2)
                    .build();
            entityManager.persist(subscription);
            entityManager.persist(subscription2);
            entityManager.persist(subscription3);
            entityManager.persist(subscription4);
        }

    }

}
