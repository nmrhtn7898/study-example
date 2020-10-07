package com.nuguri.example.repository;

import com.nuguri.example.entity.ChatSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSubscriptionRepository extends JpaRepository<ChatSubscription, Long> {
}
