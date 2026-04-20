package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.QuizResult;
import com.votredomaine.modelememoire.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizResultService {
    
    @Autowired
    private QuizResultRepository quizResultRepository;
    
    @Transactional
    public QuizResult saveResult(QuizResult result) {
        if (result.getCompletedAt() == null) {
            result.setCompletedAt(java.time.LocalDateTime.now());
        }
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
}