package com.nuguri.example.repository;

import com.nuguri.example.entity.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = "subscriptions", type = EntityGraph.EntityGraphType.FETCH)
    Optional<ChatRoom> findById(Long id);

}
