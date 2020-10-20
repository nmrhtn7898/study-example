package com.nuguri.example.controller.view;

import com.nuguri.example.model.AccountDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {

    @RequestMapping("/account/sign-up")
    public String signUp(@ModelAttribute("account") AccountDto accountDto) {
        return "sign-up";
    }

    @RequestMapping("/account/sign-in")
    public String signIn() {
        return "sign-in";
    }

}
