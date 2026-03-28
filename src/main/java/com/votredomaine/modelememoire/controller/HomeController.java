package com.votredomaine.modelememoire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    
    // Page d'accueil
    @GetMapping({"/", "/home"})
    public String home() {
        return "welcom";
    }
    
    // Page de création de compte (inscription)
    @GetMapping("/create")
    public String create() {
        return "create";
    }
    
    // Page mot de passe oublié
    @GetMapping("/forgot")
    public String forgot() {
        return "forgetpss";
    }
    
    // Page de connexion
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // API REST pour vérifier le statut
    @GetMapping("/api/status")
    @ResponseBody
    public String apiStatus() {
        return "API is running";
    }
}