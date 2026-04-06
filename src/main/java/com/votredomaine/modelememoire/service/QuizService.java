package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Question;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.repository.QuestionRepository;
import com.votredomaine.modelememoire.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    /**
     * Crée un nouveau quiz avec ses questions
     */
    @Transactional
    public Quiz createQuiz(Quiz quiz, List<Question> questions) {
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setTotalQuestions(questions.size());
        
        Quiz savedQuiz = quizRepository.save(quiz);
        
        for (Question question : questions) {
            question.setQuiz(savedQuiz);
            questionRepository.save(question);
        }
        
        return savedQuiz;
    }
    
    /**
     * Récupère tous les quiz d'un enseignant
     */
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        return quizRepository.findByTeacherId(teacherId);
    }
    
    /**
     * Récupère les quiz d'un cours (uniquement actifs)
     */
    public List<Quiz> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseIdAndStatus(courseId, "ACTIVE");
    }
    
    /**
     * Récupère tous les quiz actifs
     */
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByStatus("ACTIVE");
    }
    
    /**
     * Récupère un quiz par son ID
     */
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }
    
    /**
     * Récupère les questions d'un quiz
     */
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }
    
    /**
     * Supprime un quiz et ses questions
     */
    @Transactional
    public void deleteQuiz(Long id) {
        questionRepository.deleteByQuizId(id);
        quizRepository.deleteById(id);
    }
    
    /**
     * ⭐ Compte le nombre de quiz dans un cours
     */
    public long countQuizzesByCourse(Long courseId) {
        return quizRepository.countByCourseId(courseId);
    }
    
    /**
     * Met à jour un quiz existant
     */
    @Transactional
    public Quiz updateQuiz(Quiz quiz) {
        quiz.setUpdatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }
}