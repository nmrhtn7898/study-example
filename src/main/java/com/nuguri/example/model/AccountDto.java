package com.nuguri.example.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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

    @NotBlank
    private String profileImage;

}
