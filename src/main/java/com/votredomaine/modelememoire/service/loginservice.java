package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class loginservice {
    
    @Autowired
    private UserRepository userRepository;
    
    // Créer un encodeur BCrypt (le même que dans InscriptionController)
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // ✅ MÉTHODE DE CONNEXION CORRIGÉE
    public Utilisateur login(String email, String password) {
        Optional<Utilisateur> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            
            // Afficher les informations de débogage
            System.out.println("📝 Détails de connexion:");
            System.out.println("   Email: " + email);
            System.out.println("   Password en DB (hashé): " + user.getPassword());
            System.out.println("   Password fourni (clair): " + password);
            
            // ✅ IMPORTANT: Utiliser matches() au lieu de equals()
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("   Mot de passe valide: " + passwordMatches);
            
            if (passwordMatches) {
                return user;
            } else {
                System.out.println("❌ Mot de passe incorrect");
            }
        } else {
            System.out.println("❌ Utilisateur non trouvé avec email: " + email);
        }
        
        return null;
    }
}