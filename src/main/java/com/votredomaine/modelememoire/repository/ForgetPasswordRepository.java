package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.forgetmodel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<forgetmodel, Long> {
    
    // Recherche par token de réinitialisation
    Optional<forgetmodel> findByResetToken(String token);
    
    // Suppression par token
    void deleteByResetToken(String token);
    
    // Vérifier si un token existe
    boolean existsByResetToken(String token);
    
    // Recherche par email (utile pour la réinitialisation)
    Optional<forgetmodel> findByEmail(String email);
}