package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * Trouver les questions d'un quiz triées par ordre
     */
    List<Question> findByQuizIdOrderByOrderNumberAsc(Long quizId);
    
    /**
     * Supprimer toutes les questions d'un quiz
     * Cette méthode utilise le naming convention de Spring Data JPA
     */
    @Modifying
    @Transactional
    void deleteByQuizId(Long quizId);
    
    /**
     * Compter les questions d'un quiz
     */
    long countByQuizId(Long quizId);
    
    /**
     * Trouver les questions par quizId (sans tri)
     */
    List<Question> findByQuizId(Long quizId);
}