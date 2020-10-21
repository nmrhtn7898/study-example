package com.nuguri.example;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ChatRoom;
import com.nuguri.example.entity.ChatSubscription;
import com.nuguri.example.entity.ProfileImage;
import com.nuguri.example.model.Role;
import com.nuguri.example.model.RoomType;
import com.nuguri.example.util.FilesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Profile("default")
@Component
@RequiredArgsConstructor
public class InitRunner implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;

    private final EntityManager entityManager;

    private final FilesUtil filesUtil;

    @Value("${file.path}")
    private String filePath;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String tempDirPath = new File(".")
                .getAbsoluteFile()
                .getParentFile()
                .getAbsolutePath() + "/src/main/resources/static/images/temp";
        File tempDir = new File(tempDirPath);
        List<ProfileImage> profileImages = new ArrayList<>();
        for (File file : tempDir.listFiles()) {
            File copy = new File(filePath + "/" + System.currentTimeMillis() + "." + filesUtil.detectExtension(file.getName()));
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