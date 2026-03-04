package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByResetToken(String resetToken);
}