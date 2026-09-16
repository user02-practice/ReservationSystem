package com.example.reservationsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // 管理者ログイン画面を表示する
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}