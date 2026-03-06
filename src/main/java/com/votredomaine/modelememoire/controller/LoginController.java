package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.service.loginservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private loginservice loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utilisateur user) {
        
        Utilisateur authenticatedUser = loginService.login(user.getEmail(), user.getPassword());
        
        if (authenticatedUser != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Success");
            response.put("userId", authenticatedUser.getId());
            response.put("userName", authenticatedUser.getName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Email or Password incorrect");
        }
    }
}