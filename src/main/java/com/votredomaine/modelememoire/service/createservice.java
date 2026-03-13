package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class createservice {
    
    @Autowired
    private UserRepository userRepository;
    
    public String registerUser(Utilisateur user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }
        userRepository.save(user);
        return "Account created successfully";
    }
}