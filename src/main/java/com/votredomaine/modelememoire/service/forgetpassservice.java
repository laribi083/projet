package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Optional;

@Service
public class forgetpassservice {
    
    @Autowired
    private UserRepository userRepository;

    public String forgotPassword(String email) {
        Optional<Utilisateur> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return "Email not found";
        }

        String token = UUID.randomUUID().toString();
        user.get().setResetToken(token);
        userRepository.save(user.get());

        return "Reset token generated: " + token;
    }
    
    public boolean resetPassword(String token, String newPassword) {
        Optional<Utilisateur> user = userRepository.findByResetToken(token);
        
        if (user.isPresent()) {
            user.get().setPassword(newPassword);
            user.get().setResetToken(null);
            userRepository.save(user.get());
            return true;
        }
        
        return false;
    }
}