package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Question;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.repository.QuestionRepository;
import com.votredomaine.modelememoire.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Transactional
    public Quiz createQuiz(Quiz quiz, List<Question> questions) {
        Quiz savedQuiz = quizRepository.save(quiz);
        
        for (Question question : questions) {
            question.setQuiz(savedQuiz);
            questionRepository.save(question);
        }
        
        savedQuiz.setTotalQuestions(questions.size());
        return quizRepository.save(savedQuiz);
    }
    
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        return quizRepository.findByTeacherId(teacherId);
    }
    
    public List<Quiz> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId);
    }
    
    public List<Quiz> getQuizzesByModule(String module, String niveau) {
        return quizRepository.findByCourseModuleAndCourseNiveau(module, niveau);
    }
    
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }
    
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }
}