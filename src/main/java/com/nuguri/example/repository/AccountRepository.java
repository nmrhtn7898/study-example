package com.nuguri.example.repository;

import com.nuguri.example.entity.Account;
import com.nuguri.example.repository.custom.AccountRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {

    @EntityGraph(attributePaths = "profileImage", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Account> findById(Long id);

    @EntityGraph(attributePaths = "profileImage", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

}
