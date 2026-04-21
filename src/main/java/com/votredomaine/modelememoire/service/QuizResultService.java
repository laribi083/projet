package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.QuizResult;
import com.votredomaine.modelememoire.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizResultService {
    
    @Autowired
    private QuizResultRepository quizResultRepository;
    
    public QuizResult saveResult(QuizResult result) {
        return quizResultRepository.save(result);
    }
    
    public List<QuizResult> getResultsByStudentId(Long studentId) {
        if (studentId == null) return List.of();
        return quizResultRepository.findByStudentId(studentId);
    }
    
    public List<Long> findCompletedQuizIdsByStudentId(Long studentId) {
        if (studentId == null) return List.of();
        return quizResultRepository.findByStudentId(studentId).stream()
            .map(QuizResult::getQuizId)
            .collect(Collectors.toList());
    }
    
    public boolean hasStudentCompletedQuiz(Long studentId, Long quizId) {
        if (studentId == null || quizId == null) return false;
        return quizResultRepository.existsByStudentIdAndQuizId(studentId, quizId);
    }
    
    public QuizResult getResultByStudentAndQuiz(Long studentId, Long quizId) {
        if (studentId == null || quizId == null) return null;
        return quizResultRepository.findByStudentIdAndQuizId(studentId, quizId);
    }
    
    public List<QuizResult> getResultsByQuizId(Long quizId) {
        if (quizId == null) return List.of();
        return quizResultRepository.findByQuizId(quizId);
    }
    
    public double getAverageScoreForQuiz(Long quizId) {
        if (quizId == null) return 0;
        Double avg = quizResultRepository.getAveragePercentageByQuizId(quizId);
        return avg != null ? avg : 0;
    }
    
    public long getPassedCountForQuiz(Long quizId) {
        if (quizId == null) return 0;
        return quizResultRepository.countByQuizIdAndPassedTrue(quizId);
    }
    
    public List<QuizResult> getTopScoresForQuiz(Long quizId, int limit) {
        if (quizId == null) return List.of();
        return quizResultRepository.findTop5ByQuizIdOrderByPercentageDesc(quizId);
    }
    
    // ========== NOUVELLES MÉTHODES POUR LE DASHBOARD ==========
    
    /**
     * Calcule la moyenne des scores pour un étudiant (sur 20)
     */
    public double getAverageScoreForStudent(Long studentId) {
        if (studentId == null) return 0;
        
        List<QuizResult> results = quizResultRepository.findByStudentId(studentId);
        if (results.isEmpty()) return 0;
        
        double sum = results.stream().mapToInt(QuizResult::getPercentage).sum();
        double average = sum / results.size();
        
        // Convertir en note sur 20 (le pourcentage est sur 100)
        return (average / 100) * 20;
    }
    
    /**
     * Récupère le meilleur score d'un étudiant
     */
    public double getBestScoreForStudent(Long studentId) {
        if (studentId == null) return 0;
        
        List<QuizResult> results = quizResultRepository.findByStudentId(studentId);
        if (results.isEmpty()) return 0;
        
        int bestPercentage = results.stream()
            .mapToInt(QuizResult::getPercentage)
            .max()
            .orElse(0);
        
        // Convertir en note sur 20
        return (bestPercentage / 100) * 20;
    }
    
    /**
     * Récupère le dernier quiz complété par un étudiant
     */
    public QuizResult getLastQuizResultForStudent(Long studentId) {
        if (studentId == null) return null;
        
        List<QuizResult> results = quizResultRepository.findByStudentIdOrderByCompletedAtDesc(studentId);
        return results.isEmpty() ? null : results.get(0);
    }
}