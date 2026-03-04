package com.votredomaine.modelememoire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.votredomaine.modelememoire.model.Utilisateur;  // Utilisez votre classe existante

public interface UserRepository extends JpaRepository<Utilisateur, Long> {
    boolean existsByEmail(String email);
}