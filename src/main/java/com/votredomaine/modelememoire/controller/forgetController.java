package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.forgetpassservice;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class forgetController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private forgetpassservice forgetPasswordService;

    @PostMapping("/forget")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email cannot be empty");
            }

            Utilisateur utilisateur = userRepository.findByEmail(email).orElse(null);

            if (utilisateur == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
            }

            String token = UUID.randomUUID().toString();
            utilisateur.setResetToken(token);
            userRepository.save(utilisateur);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Reset link sent to email");
            response.put("token", token); // À retirer en production

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        
        try {
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Token cannot be empty");
            }

            if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body("Password must be at least 6 characters long");
            }

            Utilisateur utilisateur = userRepository.findByResetToken(token).orElse(null);

            if (utilisateur == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired token");
            }

            utilisateur.setPassword(newPassword);
            utilisateur.setResetToken(null);
            userRepository.save(utilisateur);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Token cannot be empty");
            }

            boolean isValid = userRepository.findByResetToken(token).isPresent();
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", isValid);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
}