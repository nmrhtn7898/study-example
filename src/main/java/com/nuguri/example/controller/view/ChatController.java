package com.nuguri.example.controller.view;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ChatController {

    @PreAuthorize("isAuthenticated()")
    @RequestMapping({"/", "/chat"})
    public String chat() {
        return "chat";
    }

}
