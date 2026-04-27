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
    
    List<Question> findByQuizIdOrderByOrderNumberAsc(Long quizId);
    
    List<Question> findByQuizId(Long quizId);
    
    long countByQuizId(Long quizId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Question q WHERE q.quizId = :quizId")
    void deleteByQuizId(@Param("quizId") Long quizId);
}