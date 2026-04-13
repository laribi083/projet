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
     * @param quiz le quiz à créer
     * @param questions la liste des questions à associer
     * @return le quiz sauvegardé avec son ID
     */
    @Transactional
    public Quiz createQuiz(Quiz quiz, List<Question> questions) {
        // Initialisation des dates
        if (quiz.getCreatedAt() == null) {
            quiz.setCreatedAt(LocalDateTime.now());
        }
        quiz.setUpdatedAt(LocalDateTime.now());
        
        // Initialisation des valeurs par défaut
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
        
        // Sauvegarde du quiz
        Quiz savedQuiz = quizRepository.save(quiz);
        
        // Sauvegarde des questions si elles existent
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
    
    /**
     * Sauvegarde un quiz (création ou mise à jour)
     * @param quiz le quiz à sauvegarder
     * @return le quiz sauvegardé
     */
    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
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
        
        return quizRepository.save(quiz);
    }
    
    /**
     * Récupère tous les quiz d'un enseignant
     * @param teacherId l'ID de l'enseignant
     * @return liste des quiz
     */
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }
        return quizRepository.findByTeacherId(teacherId);
    }
    
    /**
     * Récupère les quiz d'un cours
     * @param courseId l'ID du cours
     * @return liste des quiz du cours
     */
    public List<Quiz> getQuizzesByCourse(Long courseId) {
        if (courseId == null) {
            return List.of();
        }
        return quizRepository.findByCourseId(courseId);
    }
    
    /**
     * Récupère les quiz actifs d'un cours
     * @param courseId l'ID du cours
     * @return liste des quiz actifs
     */
    public List<Quiz> getActiveQuizzesByCourse(Long courseId) {
        if (courseId == null) {
            return List.of();
        }
        return quizRepository.findByCourseIdAndStatus(courseId, "ACTIVE");
    }
    
    /**
     * Récupère tous les quiz actifs
     * @return liste des quiz actifs
     */
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByStatus("ACTIVE");
    }
    
    /**
     * Récupère un quiz par son ID
     * @param id l'ID du quiz
     * @return le quiz trouvé ou null
     */
    public Quiz getQuizById(Long id) {
        if (id == null) {
            return null;
        }
        return quizRepository.findById(id).orElse(null);
    }
    
    /**
     * Récupère les questions d'un quiz
     * @param quizId l'ID du quiz
     * @return liste des questions du quiz
     */
    public List<Question> getQuestionsByQuizId(Long quizId) {
        if (quizId == null) {
            return List.of();
        }
        return questionRepository.findByQuizIdOrderByOrderNumberAsc(quizId);
    }
    
    /**
     * Ajoute une question à un quiz
     * @param quizId l'ID du quiz
     * @param question la question à ajouter
     * @return la question sauvegardée
     * @throws RuntimeException si le quiz n'existe pas
     */
    @Transactional
    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = getQuizById(quizId);
        if (quiz == null) {
            throw new RuntimeException("Quiz non trouvé avec l'ID: " + quizId);
        }
        
        // Définir le orderNumber si non défini
        if (question.getOrderNumber() == null) {
            long currentCount = questionRepository.countByQuizId(quizId);
            question.setOrderNumber((int) currentCount + 1);
        }
        
        // Définir les valeurs par défaut
        if (question.getPoints() == null) {
            question.setPoints(1);
        }
        
        if (question.getQuestionType() == null) {
            question.setQuestionType("SINGLE_CHOICE");
        }
        
        question.setQuizId(quizId);
        Question savedQuestion = questionRepository.save(question);
        
        // Mettre à jour le nombre total de questions
        long count = questionRepository.countByQuizId(quizId);
        quiz.setTotalQuestions((int) count);
        quizRepository.save(quiz);
        
        return savedQuestion;
    }
    
    /**
     * Supprime un quiz et toutes ses questions
     * @param id l'ID du quiz à supprimer
     */
    @Transactional
    public void deleteQuiz(Long id) {
        if (id == null) {
            return;
        }
        
        try {
            // Supprimer d'abord toutes les questions associées
            questionRepository.deleteByQuizId(id);
        } catch (Exception e) {
            // Méthode alternative si deleteByQuizId échoue
            List<Question> questions = questionRepository.findByQuizIdOrderByOrderNumberAsc(id);
            for (Question question : questions) {
                questionRepository.delete(question);
            }
        }
        
        // Puis supprimer le quiz
        quizRepository.deleteById(id);
    }
    
    /**
     * Compte le nombre de quiz dans un cours
     * @param courseId l'ID du cours
     * @return nombre de quiz
     */
    public long countQuizzesByCourse(Long courseId) {
        if (courseId == null) {
            return 0;
        }
        return quizRepository.countByCourseId(courseId);
    }
    
    /**
     * Met à jour un quiz existant
     * @param quiz le quiz avec les nouvelles valeurs
     * @return le quiz mis à jour
     * @throws RuntimeException si le quiz est invalide
     */
    @Transactional
    public Quiz updateQuiz(Quiz quiz) {
        if (quiz == null || quiz.getId() == null) {
            throw new RuntimeException("Quiz invalide");
        }
        
        Quiz existingQuiz = getQuizById(quiz.getId());
        if (existingQuiz == null) {
            throw new RuntimeException("Quiz non trouvé avec l'ID: " + quiz.getId());
        }
        
        // Mettre à jour les champs
        existingQuiz.setTitle(quiz.getTitle());
        existingQuiz.setDescription(quiz.getDescription());
        existingQuiz.setTimeLimit(quiz.getTimeLimit());
        existingQuiz.setPassingScore(quiz.getPassingScore());
        existingQuiz.setModule(quiz.getModule());
        existingQuiz.setNiveau(quiz.getNiveau());
        existingQuiz.setUpdatedAt(LocalDateTime.now());
        
        return quizRepository.save(existingQuiz);
    }
    
    /**
     * Supprime une question
     * @param questionId l'ID de la question à supprimer
     */
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (questionId == null) {
            return;
        }
        
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question != null) {
            Long quizId = question.getQuizId();
            
            // Supprimer la question
            questionRepository.deleteById(questionId);
            
            // Mettre à jour le nombre total de questions du quiz
            long count = questionRepository.countByQuizId(quizId);
            Quiz quiz = getQuizById(quizId);
            if (quiz != null) {
                quiz.setTotalQuestions((int) count);
                quizRepository.save(quiz);
            }
        }
    }
    
    /**
     * Recherche des quiz par titre
     * @param title le titre à rechercher
     * @return liste des quiz dont le titre contient la chaîne
     */
    public List<Quiz> searchQuizzesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return List.of();
        }
        return quizRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Met à jour le statut d'un quiz
     * @param quizId l'ID du quiz
     * @param status le nouveau statut (ACTIVE, INACTIVE, DRAFT)
     * @return le quiz mis à jour
     * @throws RuntimeException si le quiz n'existe pas
     */
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
    
    /**
     * Met à jour le nombre total de questions d'un quiz
     * @param quizId l'ID du quiz
     */
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
    
    /**
     * Récupère le nombre total de questions d'un quiz
     * @param quizId l'ID du quiz
     * @return nombre de questions
     */
    public long getTotalQuestionsCount(Long quizId) {
        if (quizId == null) {
            return 0;
        }
        return questionRepository.countByQuizId(quizId);
    }
    
    /**
     * Vérifie si un quiz existe
     * @param quizId l'ID du quiz
     * @return true si le quiz existe
     */
    public boolean quizExists(Long quizId) {
        if (quizId == null) {
            return false;
        }
        return quizRepository.existsById(quizId);
    }
    
    /**
     * Récupère les quiz par module
     * @param module le module
     * @return liste des quiz du module
     */
    public List<Quiz> getQuizzesByModule(String module) {
        if (module == null || module.trim().isEmpty()) {
            return List.of();
        }
        return quizRepository.findByModule(module);
    }
    
    /**
     * Récupère les quiz par niveau
     * @param niveau le niveau (1year, 2year, 3year)
     * @return liste des quiz du niveau
     */
    public List<Quiz> getQuizzesByNiveau(String niveau) {
        if (niveau == null || niveau.trim().isEmpty()) {
            return List.of();
        }
        return quizRepository.findByNiveau(niveau);
    }
}