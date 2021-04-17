package com.jimetevenard.jimnotesback.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {
    @GetMapping("/login")
    String login() {
        return "login";
    }
}