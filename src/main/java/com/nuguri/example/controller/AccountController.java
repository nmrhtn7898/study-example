package com.nuguri.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class AccountController {

    @RequestMapping("/account/join")
    public String join(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "join";
    }

    @RequestMapping("/account/login")
    public String login() {
        return "login";
    }

}
