// QuizResultRepository.java - VERSION CORRECTE SANS DOUBLONS
package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.QuizResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    
    // ========== MÉTHODES DE BASE ==========
    
    List<QuizResult> findByStudentId(Long studentId);
    
    List<QuizResult> findByQuizId(Long quizId);
    
    QuizResult findByStudentIdAndQuizId(Long studentId, Long quizId);
    
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    
    long countByQuizIdAndPassedTrue(Long quizId);
    
    // ========== MÉTHODES DE CLASSEMENT ==========
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.quizId = :quizId ORDER BY qr.percentage DESC")
    List<QuizResult> findTop5ByQuizIdOrderByPercentageDesc(@Param("quizId") Long quizId);
    
    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.quizId = :quizId")
    Double getAveragePercentageByQuizId(@Param("quizId") Long quizId);
    
    // ========== MÉTHODES POUR UN ÉTUDIANT ==========
    
    List<QuizResult> findByStudentIdOrderByCompletedAtDesc(Long studentId);
    
    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.studentId = :studentId")
    Double getAveragePercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT MAX(qr.percentage) FROM QuizResult qr WHERE qr.studentId = :studentId")
    Double getMaxPercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT MIN(qr.percentage) FROM QuizResult qr WHERE qr.studentId = :studentId")
    Double getMinPercentageByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.studentId = :studentId AND qr.passed = true")
    long countPassedQuizzesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.studentId = :studentId AND qr.passed = false")
    long countFailedQuizzesByStudentId(@Param("studentId") Long studentId);
    
    // ========== MÉTHODES STATISTIQUES GLOBALES ==========
    
    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr")
    Double getGlobalAveragePercentage();
    
    @Query("SELECT COUNT(qr) FROM QuizResult qr")
    long countTotalQuizResults();
    
    // ========== MÉTHODES AVEC PÉRIODE ==========
    
    List<QuizResult> findByStudentIdAndCompletedAtBetween(Long studentId, LocalDateTime start, LocalDateTime end);
    
    // ========== MÉTHODES AVEC PAGINATION ==========
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.studentId = :studentId ORDER BY qr.completedAt DESC")
    List<QuizResult> findTopNByStudentIdOrderByCompletedAtDesc(@Param("studentId") Long studentId, Pageable pageable);
    
    // ========== MÉTHODES DE CALCUL ==========
    
    @Query("SELECT (COUNT(CASE WHEN qr.passed = true THEN 1 END) * 100.0 / COUNT(qr)) FROM QuizResult qr WHERE qr.studentId = :studentId")
    Double getSuccessRateByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT new map(qr.studentId as studentId, AVG(qr.percentage) as avgScore, COUNT(qr) as totalQuizzes, SUM(CASE WHEN qr.passed = true THEN 1 ELSE 0 END) as passed, SUM(CASE WHEN qr.passed = false THEN 1 ELSE 0 END) as failed) FROM QuizResult qr WHERE qr.studentId = :studentId GROUP BY qr.studentId")
    List<Map<String, Object>> getQuizStatisticsByStudentId(@Param("studentId") Long studentId);
    
    // ========== MÉTHODE POUR LE DERNIER QUIZ ==========
    
    @Query("SELECT qr FROM QuizResult qr WHERE qr.studentId = :studentId AND qr.quizId = :quizId ORDER BY qr.completedAt DESC")
    List<QuizResult> findLatestByStudentIdAndQuizId(@Param("studentId") Long studentId, @Param("quizId") Long quizId);
}