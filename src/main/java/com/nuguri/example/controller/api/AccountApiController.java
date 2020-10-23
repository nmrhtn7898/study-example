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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.*;

@RestController
@RequiredArgsConstructor
public class AccountApiController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final AccountValidator accountValidator;

    private static final AccountApiController controller = methodOn(AccountApiController.class);

    @PreAuthorize("isAuthenticated()")
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
        GetMeResource resource = new GetMeResource(response);
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/api/v1/account")
    public ResponseEntity queryAccount() {
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
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
        GetAccountResource resource = new GetAccountResource(response);
        return ResponseEntity
                .ok()
                .body(resource);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/api/v1/account")
    public ResponseEntity generateAccount(@RequestBody @Valid GenerateAccountRequest request, Errors errors) {
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
        GenerateAccountResource resource = new GenerateAccountResource(account.getId());
        resource.getLink("self");
        return ResponseEntity
                .created(resource.getLink("self").get().toUri())
                .body(resource);
    }

    @PreAuthorize("isAuthenticated()")
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

    @PreAuthorize("isAuthenticated()")
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
        private final Long id;
        private final String email;
        private final String nickname;
        private final String name;
        private final Role role;
        private final String profileImage;

        public GetAccountResponse(Account account) {
            this.id = account.getId();
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
    public static class GenerateAccountResource extends EntityModel<Void> {
        public GenerateAccountResource(Long id) {
            add(
                    linkTo(controller.generateAccount(null, null))
                            .withSelfRel()
                            .withType(POST.name())
            );
            add(
                    linkTo(controller.getAccount(id, null))
                            .withRel("get")
                            .withType(GET.name())
            );
            add(
                    linkTo(controller.updateAccount(null, null, id, null))
                            .withRel("update")
                            .withType(PATCH.name())
            );
            add(
                    linkTo(controller.deleteAccount(id, null))
                            .withRel("delete")
                            .withType(DELETE.name())
            );
        }
    }

    @Getter
    public static class GetMeResource extends EntityModel<GetAccountResponse> {
        private final GetAccountResponse content;

        public GetMeResource(GetAccountResponse response) {
            this.content = response;
            add(
                    linkTo(controller.getMe(null))
                            .withSelfRel()
                            .withType(GET.name())
            );
            add(
                    linkTo(controller.updateAccount(null, null, response.getId(), null))
                            .withRel("update")
                            .withType(PATCH.name()));
            add(
                    linkTo(controller.deleteAccount(response.getId(), null))
                            .withRel("delete")
                            .withType(DELETE.name())
            );
        }
    }

    @Getter
    public static class GetAccountResource extends EntityModel<GetAccountResponse> {
        private final GetAccountResponse content;

        public GetAccountResource(GetAccountResponse response) {
            this.content = response;
            add(
                    linkTo(controller.getAccount(response.getId(), null))
                            .withSelfRel()
                            .withType(GET.name())
            );
            add(
                    linkTo(controller.updateAccount(null, null, response.getId(), null))
                            .withRel("update")
                            .withType(PATCH.name())
            );
            add(
                    linkTo(controller.deleteAccount(response.getId(), null))
                            .withRel("delete")
                            .withType(DELETE.name())
            );
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
