package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscription")
@CrossOrigin("*")
public class InscriptionController {
    private final UserRepository userRepository;

    public InscriptionController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public String inscription(@RequestBody Utilisateur utilisateur) {
        if (userRepository.existsByEmail(utilisateur.getEmail())) {
            return "Email already exists";
        }
        userRepository.save(utilisateur);
        return "Account created successfully";
    }
}