package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    
    List<QuizResult> findByStudentId(Long studentId);
    
    List<QuizResult> findByQuizId(Long quizId);
    
    QuizResult findByStudentIdAndQuizId(Long studentId, Long quizId);
    
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    
    long countByQuizIdAndPassedTrue(Long quizId);
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.quizId = :quizId ORDER BY qr.percentage DESC")
    List<QuizResult> findTop5ByQuizIdOrderByPercentageDesc(@Param("quizId") Long quizId);
    
    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.quizId = :quizId")
    Double getAveragePercentageByQuizId(@Param("quizId") Long quizId);
}