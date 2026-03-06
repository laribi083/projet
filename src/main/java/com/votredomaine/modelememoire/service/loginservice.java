package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class loginservice {
    
    @Autowired
    private UserRepository userRepository;

    public Utilisateur login(String email, String password) {
        Optional<Utilisateur> user = userRepository.findByEmail(email);
        
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        
        return null;
    }
}