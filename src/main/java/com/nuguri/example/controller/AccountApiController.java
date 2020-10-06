package com.nuguri.example.controller;

import com.nuguri.example.repository.AccountRepository;
import com.nuguri.example.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    @GetMapping("/api/v1/account")
    public ResponseEntity queryAccount() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/v1/account/{id}")
    public ResponseEntity getAccount() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/api/v1/account")
    public ResponseEntity generateAccount() {
        return ResponseEntity.created(null).body(null);
    }

    @PatchMapping("/api/v1/account/{id}")
    public ResponseEntity updateAccount() {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/api/v1/account/{id}")
    public ResponseEntity deleteAccount() {
        return ResponseEntity.ok(null);
    }

}
