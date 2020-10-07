package com.nuguri.example;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.entity.ChatSubscription;
import com.nuguri.example.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
@SpringBootApplication
@RequiredArgsConstructor
public class ExampleApplication {

    private final EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
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
                    .build();
            Account admin = Account
                    .builder()
                    .email("admin@naver.com")
                    .nickname("관리자")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Collections.singletonList(Role.ADMIN))
                    .build();
            entityManager.persist(user);
            entityManager.persist(admin);
            ChatRoom chatRoom = ChatRoom
                    .builder()
                    .name("유저와 관리자의 채팅방")
                    .build();
            entityManager.persist(chatRoom);
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
            entityManager.persist(subscription);
            entityManager.persist(subscription2);
        }

    }

}
