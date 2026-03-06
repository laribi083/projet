package com.votredomaine.modelememoire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index() {
        return "welcom"; // Correspond à welcom.html
    }
    
    @GetMapping("/create")
    public String create() {
        return "create"; // Correspond à create.html
    }
    
    @GetMapping("/forgot")
    public String forgot() {
        return "forgetpss"; // Correspond à forgetpss.html (corrigé)
    }
    
    @GetMapping("/login")
    public String login() {
        return "login"; // Correspond à login.html
    }
}