package com.nuguri.example.controller.api;

import com.nuguri.example.entity.Account;
import com.nuguri.example.model.AccountAdapter;
import com.nuguri.example.model.AccountDto;
import com.nuguri.example.model.Role;
import com.nuguri.example.repository.AccountRepository;
import com.nuguri.example.service.AccountService;
import com.nuguri.example.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountValidator accountValidator;

    @GetMapping("/api/v1/account")
    public ResponseEntity queryAccount() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/v1/account/{id}")
    public ResponseEntity getAccount(@PathVariable Long id, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        if (!id.equals(accountAdapter.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/api/v1/account")
    public ResponseEntity generateAccount(@RequestBody @Valid AccountDto accountDto, Errors errors) throws URISyntaxException {
        if (errors.hasErrors() || !accountValidator.isPassEqualsWithRePass(accountDto, errors)) {
            return ResponseEntity.badRequest().build();
        }
        if (accountRepository.existsByEmail(accountDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Account account = Account
                .builder()
                .email(accountDto.getEmail())
                .nickname(accountDto.getNickname())
                .name(accountDto.getName())
                .password(accountDto.getPassword())
                .role(Role.USER)
                .build();
        account = accountService.generateAccount(account);
        return ResponseEntity.created(new URI("/api/v1/account" + account.getId())).body(null);
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
