package com.nuguri.example.controller.api;

import com.nuguri.example.entity.Account;
import com.nuguri.example.entity.ProfileImage;
import com.nuguri.example.model.AccountAdapter;
import com.nuguri.example.model.Role;
import com.nuguri.example.repository.AccountRepository;
import com.nuguri.example.service.AccountService;
import com.nuguri.example.validator.AccountValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountValidator accountValidator;

    @GetMapping("/api/v1/account/me")
    public ResponseEntity getMe(@AuthenticationPrincipal AccountAdapter accountAdapter) {
        Long id = accountAdapter
                .getAccount()
                .getId();
        Optional<Account> byId = accountRepository.findById(id);
        if (!byId.isPresent()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        Account account = byId.get();
        GetAccountResponse response = new GetAccountResponse(account);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/account")
    public ResponseEntity queryAccount() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/v1/account/{id}")
    public ResponseEntity getAccount(@PathVariable Long id, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        if (!id.equals(accountAdapter.getAccount().getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
        Optional<Account> byId = accountRepository.findById(id);
        if (!byId.isPresent()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        Account account = byId.get();
        GetAccountResponse response = new GetAccountResponse(account);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/api/v1/account")
    public ResponseEntity generateAccount(@RequestBody @Valid GenerateAccountRequest request, Errors errors) throws URISyntaxException {
        if (errors.hasErrors() ||
                !accountValidator.isPassEqualsWithRePass(request.password, request.getRePassword(), errors)) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        if (accountRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        Account account = Account
                .builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .name(request.getName())
                .password(request.getPassword())
                .role(Role.USER)
                .profileImage(
                        ProfileImage
                                .builder()
                                .id(request.getProfileImageId())
                                .build()
                )
                .build();
        account = accountService.generateAccount(account);
        return ResponseEntity
                .created(new URI("/api/v1/account" + account.getId()))
                .build();
    }

    @PatchMapping("/api/v1/account/{id}")
    public ResponseEntity updateAccount(@RequestBody @Valid UpdateAccountRequest request, Errors errors,
                                        @PathVariable Long id, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        if (!id.equals(accountAdapter.getAccount().getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
        if (errors.hasErrors() ||
                !accountValidator.isPassEqualsWithRePass(request.password, request.getRePassword(), errors)) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        if (!accountRepository.existsById(id)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
        Account account = Account
                .builder()
                .nickname(request.getNickname())
                .name(request.getName())
                .password(request.getPassword())
                .profileImage(
                        ProfileImage
                                .builder()
                                .id(request.getProfileImageId())
                                .build()
                )
                .build();
        accountService.updateAccount(account);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/api/v1/account/{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id, @AuthenticationPrincipal AccountAdapter accountAdapter) {
        if (!id.equals(accountAdapter.getAccount().getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
        accountRepository.deleteById(id);
        return ResponseEntity
                .ok()
                .build();
    }

    /* dto class */
    @Getter
    public static class GetAccountResponse {
        private final String email;
        private final String nickname;
        private final String name;
        private final Role role;
        private final String profileImage;

        public GetAccountResponse(Account account) {
            this.email = account.getEmail();
            this.nickname = account.getNickname();
            this.name = account.getName();
            this.role = account.getRole();
            this.profileImage = WebMvcLinkBuilder
                    .linkTo(methodOn(FilesApiController.class).download(account.getProfileImage().getId()))
                    .toString();
        }
    }

    @Getter
    public static class GenerateAccountRequest {
        @Email
        private String email;
        @NotBlank
        private String nickname;
        @NotBlank
        private String name;
        @NotBlank
        private String password;
        @NotBlank
        private String rePassword;
        @NotNull
        private Long profileImageId;
    }

    @Getter
    public static class UpdateAccountRequest {
        private String nickname;
        private String name;
        private String password;
        private String rePassword;
        private Long profileImageId;
    }

}
