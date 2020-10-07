package com.nuguri.example.repository;

import com.nuguri.example.entity.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @EntityGraph(attributePaths = "subscriptions", type = EntityGraph.EntityGraphType.FETCH)
    Optional<ChatRoom> findByUuid(String uuid);

}
