package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.forgetmodel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<forgetmodel, Long> {
    
   
    Optional<forgetmodel> findByResetToken(String token);
    
    
    void deleteByResetToken(String token);
    
   
    boolean existsByResetToken(String token);
    
   
    Optional<forgetmodel> findByEmail(String email);
}