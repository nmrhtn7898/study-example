package com.nuguri.example;

import com.nuguri.example.entity.Account;
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
import javax.persistence.EntityTransaction;
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
            Account user = new Account("user@naver.com", passwordEncoder.encode("1234"), Collections.singletonList(Role.USER));
            Account admin = new Account("admin@naver.com", passwordEncoder.encode("1234"), Collections.singletonList(Role.ADMIN));
            entityManager.persist(user);
            entityManager.persist(admin);
        }

    }

}
