package com.nuguri.example;

import com.nuguri.example.entity.*;
import com.nuguri.example.model.Role;
import com.nuguri.example.model.RoomType;
import com.nuguri.example.service.FilesService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

@EnableWebSecurity
@EnableWebSocketMessageBroker
@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class ExampleApplication {

    private final EntityManager entityManager;

    private final FilesService filesService;

    private final ResourceLoader resourceLoader;

    @Value("${file.path}")
    private String filePath;

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public Tika tika() {
        return new Tika();
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
            File fileDir = new File(filePath);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            String tempDirPath = new File(".")
                    .getAbsoluteFile()
                    .getParentFile()
                    .getParentFile()
                    .getParent() + "/resources/static/images/temp";
            File tempDir = new File(tempDirPath);
            List<ProfileImage> profileImages = new ArrayList<>();
            for (File file : tempDir.listFiles()) {
                File copy = new File(filePath + "/" + System.currentTimeMillis() + "/" + file.getName());
                String absolutePath = copy.getAbsolutePath();
                ProfileImage profileImage = ProfileImage
                        .builder()
                        .filePath(absolutePath)
                        .name(file.getName())
                        .build();
                entityManager.persist(profileImage);
                profileImages.add(profileImage);
                try (FileInputStream fis = new FileInputStream(file); FileOutputStream fos = new FileOutputStream(copy)) {
                    byte[] buffer = new byte[4096];
                    while (fis.read(buffer) != -1) {
                        fos.write(buffer);
                    }
                } catch (Exception e) {
                    //
                }
            }
            Account user = Account
                    .builder()
                    .email("user@naver.com")
                    .nickname("유저")
                    .name("홍길동")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.USER)
                    .profileImage(profileImages.get(0))
                    .build();
            Account admin = Account
                    .builder()
                    .email("admin@naver.com")
                    .name("개똥이")
                    .nickname("관리자")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.ADMIN)
                    .profileImage(profileImages.get(1))
                    .build();
            Account testUser = Account
                    .builder()
                    .email("test@naver.com")
                    .name("아무개")
                    .nickname("테스트계정")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.USER)
                    .profileImage(profileImages.get(2))
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
