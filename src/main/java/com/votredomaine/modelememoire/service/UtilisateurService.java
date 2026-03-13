package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Utilisateur> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<Utilisateur> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Utilisateur saveUser(Utilisateur user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}