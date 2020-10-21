package com.nuguri.example.model;

import com.nuguri.example.entity.ProfileImage;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AccountDto {

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
