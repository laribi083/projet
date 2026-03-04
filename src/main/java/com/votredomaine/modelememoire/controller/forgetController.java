package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
public class forgetController {  // Gardez ce nom

    private final UserRepository userRepository;

    // CORRECTION: Le constructeur doit avoir le même nom que la classe
    public forgetController(UserRepository userRepository) {  // OK maintenant
        this.userRepository = userRepository;
    }

    @PostMapping("/forget")
    public String forgotPassword(@RequestParam String email) {

        Utilisateur utilisateur = userRepository.findByEmail(email)
                .orElse(null);

        if (utilisateur == null) {
            return "Email not found";
        }

        String token = UUID.randomUUID().toString();
        utilisateur.setResetToken(token);
        userRepository.save(utilisateur);

        return "Reset link sent to email (token: " + token + ")";
    }

    @PostMapping("/reset")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        Utilisateur utilisateur = userRepository.findByResetToken(token)
                .orElse(null);

        if (utilisateur == null) {
            return "Invalid token";
        }

        utilisateur.setPassword(newPassword);
        utilisateur.setResetToken(null);
        userRepository.save(utilisateur);

        return "Password updated successfully";
    }
}