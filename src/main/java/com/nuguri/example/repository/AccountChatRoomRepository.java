package com.nuguri.example.repository;

import com.nuguri.example.entity.AccountChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountChatRoomRepository extends JpaRepository<AccountChatRoom, Long> {
}
