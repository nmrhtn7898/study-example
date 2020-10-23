package com.nuguri.example.controller.view;

import com.nuguri.example.controller.api.AccountApiController.GenerateAccountRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {

    @PreAuthorize("isAnonymous()")
    @RequestMapping("/account/sign-up")
    public String signUp(@ModelAttribute("account") GenerateAccountRequest request) {
        return "sign-up";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping("/account/sign-in")
    public String signIn() {
        return "sign-in";
    }

}
