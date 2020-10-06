package com.nuguri.example;

import com.nuguri.example.model.Role;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.*;

@EnableWebSecurity
@EnableWebSocketMessageBroker
@SpringBootApplication
public class ExampleApplication {

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

}
