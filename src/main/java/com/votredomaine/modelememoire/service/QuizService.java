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
    
    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        System.out.println("=== QuizService.saveQuiz() ===");
        System.out.println("Titre: " + quiz.getTitle());
        System.out.println("CourseId: " + quiz.getCourseId());
        System.out.println("TeacherId: " + quiz.getTeacherId());
        
        if (quiz.getCreatedAt() == null) {
            quiz.setCreatedAt(LocalDateTime.now());
        }
        quiz.setUpdatedAt(LocalDateTime.now());
        
        if (quiz.getTotalQuestions() == null) {
            quiz.setTotalQuestions(0);
        }
        
        if (quiz.getStatus() == null) {
            quiz.setStatus("ACTIVE");
        }
        
        if (quiz.getTimeLimit() == null) {
            quiz.setTimeLimit(30);
        }
        
        if (quiz.getPassingScore() == null) {
            quiz.setPassingScore(70);
        }
        
        Quiz savedQuiz = quizRepository.save(quiz);
        System.out.println("✅ Quiz sauvegardé avec ID: " + savedQuiz.getId());
        return savedQuiz;
    }
    
    @Transactional
    public Quiz createQuiz(Quiz quiz, List<Question> questions) {
        Quiz savedQuiz = saveQuiz(quiz);
        
        if (questions != null && !questions.isEmpty()) {
            int order = 1;
            for (Question question : questions) {
                question.setQuizId(savedQuiz.getId());
                if (question.getOrderNumber() == null) {
                    question.setOrderNumber(order++);
                }
                if (question.getPoints() == null) {
                    question.setPoints(1);
                }
                if (question.getQuestionType() == null) {
                    question.setQuestionType("SINGLE_CHOICE");
                }
                questionRepository.save(question);
            }
            savedQuiz.setTotalQuestions(questions.size());
            savedQuiz = quizRepository.save(savedQuiz);
        }
        
        return savedQuiz;
    }
    
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }
        return quizRepository.findByTeacherId(teacherId);
    }
    
    public List<Quiz> getQuizzesByCourse(Long courseId) {
        if (courseId == null) {
            return List.of();
        }
        return quizRepository.findByCourseId(courseId);
    }
    
    public List<Quiz> getActiveQuizzesByCourse(Long courseId) {
        if (courseId == null) {
            return List.of();
        }
        return quizRepository.findByCourseIdAndStatus(courseId, "ACTIVE");
    }
    
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByStatus("ACTIVE");
    }
    
    public Quiz getQuizById(Long id) {
        if (id == null) {
            return null;
        }
        return quizRepository.findById(id).orElse(null);
    }
    
    public List<Question> getQuestionsByQuizId(Long quizId) {
        if (quizId == null) {
            return List.of();
        }
        return questionRepository.findByQuizIdOrderByOrderNumberAsc(quizId);
    }
    
    @Transactional
    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = getQuizById(quizId);
        if (quiz == null) {
            throw new RuntimeException("Quiz non trouvé avec l'ID: " + quizId);
        }
        
        if (question.getOrderNumber() == null) {
            long currentCount = questionRepository.countByQuizId(quizId);
            question.setOrderNumber((int) currentCount + 1);
        }
        
        if (question.getPoints() == null) {
            question.setPoints(10);
        }
        
        if (question.getQuestionType() == null) {
            question.setQuestionType("SINGLE_CHOICE");
        }
        
        question.setQuizId(quizId);
        Question savedQuestion = questionRepository.save(question);
        
        long count = questionRepository.countByQuizId(quizId);
        quiz.setTotalQuestions((int) count);
        quizRepository.save(quiz);
        
        return savedQuestion;
    }
    
    @Transactional
    public void deleteQuiz(Long id) {
        if (id == null) {
            return;
        }
        
        try {
            questionRepository.deleteByQuizId(id);
        } catch (Exception e) {
            List<Question> questions = questionRepository.findByQuizIdOrderByOrderNumberAsc(id);
            for (Question question : questions) {
                questionRepository.delete(question);
            }
        }
        
        quizRepository.deleteById(id);
    }
    
    public long countQuizzesByCourse(Long courseId) {
        if (courseId == null) {
            return 0;
        }
        return quizRepository.countByCourseId(courseId);
    }
    
    @Transactional
    public Quiz updateQuiz(Quiz quiz) {
        if (quiz == null || quiz.getId() == null) {
            throw new RuntimeException("Quiz invalide");
        }
        
        Quiz existingQuiz = getQuizById(quiz.getId());
        if (existingQuiz == null) {
            throw new RuntimeException("Quiz non trouvé avec l'ID: " + quiz.getId());
        }
        
        existingQuiz.setTitle(quiz.getTitle());
        existingQuiz.setDescription(quiz.getDescription());
        existingQuiz.setTimeLimit(quiz.getTimeLimit());
        existingQuiz.setPassingScore(quiz.getPassingScore());
        existingQuiz.setModule(quiz.getModule());
        existingQuiz.setNiveau(quiz.getNiveau());
        existingQuiz.setUpdatedAt(LocalDateTime.now());
        
        return quizRepository.save(existingQuiz);
    }
    
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (questionId == null) {
            return;
        }
        
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question != null) {
            Long quizId = question.getQuizId();
            questionRepository.deleteById(questionId);
            
            long count = questionRepository.countByQuizId(quizId);
            Quiz quiz = getQuizById(quizId);
            if (quiz != null) {
                quiz.setTotalQuestions((int) count);
                quizRepository.save(quiz);
            }
        }
    }
    
    public List<Quiz> searchQuizzesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return List.of();
        }
        return quizRepository.findByTitleContainingIgnoreCase(title);
    }
    
    @Transactional
    public Quiz updateQuizStatus(Long quizId, String status) {
        Quiz quiz = getQuizById(quizId);
        if (quiz == null) {
            throw new RuntimeException("Quiz non trouvé avec l'ID: " + quizId);
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new RuntimeException("Le statut est obligatoire");
        }
        
        quiz.setStatus(status);
        quiz.setUpdatedAt(LocalDateTime.now());
        return quizRepository.save(quiz);
    }
    
    @Transactional
    public void updateTotalQuestions(Long quizId) {
        if (quizId == null) {
            return;
        }
        
        Quiz quiz = getQuizById(quizId);
        if (quiz != null) {
            long count = questionRepository.countByQuizId(quizId);
            quiz.setTotalQuestions((int) count);
            quizRepository.save(quiz);
        }
    }
    
    public long getTotalQuestionsCount(Long quizId) {
        if (quizId == null) {
            return 0;
        }
        return questionRepository.countByQuizId(quizId);
    }
    
    public boolean quizExists(Long quizId) {
        if (quizId == null) {
            return false;
        }
        return quizRepository.existsById(quizId);
    }
    
    public List<Quiz> getQuizzesByModule(String module) {
        if (module == null || module.trim().isEmpty()) {
            return List.of();
        }
        return quizRepository.findByModule(module);
    }
    
    public List<Quiz> getQuizzesByNiveau(String niveau) {
        if (niveau == null || niveau.trim().isEmpty()) {
            return List.of();
        }
        return quizRepository.findByNiveau(niveau);
    }
}