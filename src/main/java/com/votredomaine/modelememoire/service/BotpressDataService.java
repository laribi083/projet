// BotpressDataService.java - VERSION 2 (BASED ON YOUR ACTUAL CLASSES)
package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.*;
import com.votredomaine.modelememoire.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BotpressDataService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private courserepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private QuizResultRepository quizResultRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private RatingRepository ratingRepository;
    
    // ========== STUDENT GRADES ==========
    public String getStudentGrades(Long userId) {
        List<QuizResult> results = quizResultRepository.findByStudentId(userId);
        
        if (results == null || results.isEmpty()) {
            return "You haven't taken any quizzes yet. Start by following a course!";
        }
        
        double average = results.stream()
            .mapToDouble(r -> r.getPercentage())
            .average()
            .orElse(0);
        
        long passedCount = results.stream().filter(r -> r.isPassed()).count();
        long failedCount = results.size() - passedCount;
        
        Optional<QuizResult> best = results.stream()
            .max(Comparator.comparing(r -> r.getPercentage()));
        Optional<QuizResult> worst = results.stream()
            .min(Comparator.comparing(r -> r.getPercentage()));
        
        String response = "Your Results:\n\n" +
            "Overall average: " + String.format("%.1f", average) + "%\n" +
            "Quizzes passed: " + passedCount + "\n" +
            "Quizzes to retake: " + failedCount + "\n";
        
        if (best.isPresent()) {
            response += "Best score: " + String.format("%.1f", best.get().getPercentage()) + "%\n";
        }
        if (worst.isPresent()) {
            response += "Score to improve: " + String.format("%.1f", worst.get().getPercentage()) + "%\n";
        }
        
        return response;
    }
    
    // ========== STUDENT COURSES ==========
    public String getStudentCourses(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
        
        if (enrollments == null || enrollments.isEmpty()) {
            return "You are not enrolled in any courses.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Your Courses:\n\n");
        
        for (Enrollment enrollment : enrollments) {
            Optional<Course> courseOpt = courseRepository.findById(enrollment.getCourseId());
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                response.append("- ").append(course.getTitle());
                if (enrollment.getProgress() != null && enrollment.getProgress() > 0) {
                    response.append(" (").append(enrollment.getProgress()).append("% completed)");
                }
                if (Boolean.TRUE.equals(enrollment.getIsCompleted())) {
                    response.append(" - COMPLETED");
                }
                response.append("\n");
            }
        }
        
        return response.toString();
    }
    
    // ========== STUDENT PROGRESS ==========
    public String getStudentProgress(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
        
        if (enrollments == null || enrollments.isEmpty()) {
            return "Start by enrolling in courses to track your progress!";
        }
        
        double avgProgress = enrollments.stream()
            .mapToInt(e -> e.getProgress() != null ? e.getProgress() : 0)
            .average()
            .orElse(0);
        
        long completedCount = enrollments.stream()
            .filter(e -> Boolean.TRUE.equals(e.getIsCompleted()))
            .count();
        
        List<QuizResult> results = quizResultRepository.findByStudentId(userId);
        double successRate = 0;
        if (results != null && !results.isEmpty()) {
            successRate = (double) results.stream().filter(r -> r.isPassed()).count() / results.size() * 100;
        }
        
        return "Your Progress:\n\n" +
            "Course completion: " + String.format("%.0f", avgProgress) + "%\n" +
            "Courses completed: " + completedCount + "/" + enrollments.size() + "\n" +
            "Quiz success rate: " + String.format("%.0f", successRate) + "%\n";
    }
    
    // ========== AVAILABLE QUIZZES ==========
    public String getAvailableQuizzes(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
        
        if (enrollments == null || enrollments.isEmpty()) {
            return "First enroll in courses to access quizzes!";
        }
        
        List<Quiz> allQuizzes = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            List<Quiz> courseQuizzes = quizRepository.findByCourseId(enrollment.getCourseId());
            if (courseQuizzes != null) {
                allQuizzes.addAll(courseQuizzes);
            }
        }
        
        List<QuizResult> results = quizResultRepository.findByStudentId(userId);
        Set<Long> takenQuizIds = new HashSet<>();
        if (results != null) {
            takenQuizIds = results.stream().map(r -> r.getQuizId()).collect(Collectors.toSet());
        }
        
        List<Quiz> available = allQuizzes.stream()
            .filter(q -> !takenQuizIds.contains(q.getId()))
            .collect(Collectors.toList());
        
        if (available.isEmpty()) {
            return "You have taken all available quizzes!";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Available Quizzes:\n\n");
        for (Quiz quiz : available) {
            response.append("- ").append(quiz.getTitle()).append("\n");
        }
        return response.toString();
    }
    
    // ========== CERTIFICATES ==========
    public String getCertificateInfo(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
        
        if (enrollments == null || enrollments.isEmpty()) {
            return "Complete a course to get your certificate!";
        }
        
        List<Enrollment> completed = enrollments.stream()
            .filter(e -> Boolean.TRUE.equals(e.getIsCompleted()))
            .collect(Collectors.toList());
        
        if (completed.isEmpty()) {
            return "You don't have any certificates yet. Complete a course!";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Your Certificates:\n\n");
        for (Enrollment enrollment : completed) {
            Optional<Course> courseOpt = courseRepository.findById(enrollment.getCourseId());
            if (courseOpt.isPresent()) {
                response.append("- ").append(courseOpt.get().getTitle()).append("\n");
            }
        }
        return response.toString();
    }
    
    // ========== GENERAL STATISTICS ==========
    public String getGeneralStats() {
        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        long publishedCourses = courseRepository.countByStatus("PUBLISHED");
        long totalEnrollments = enrollmentRepository.count();
        
        Double avgGrade = quizResultRepository.getGlobalAveragePercentage();
        if (avgGrade == null) avgGrade = 0.0;
        
        long totalQuizzes = quizRepository.count();
        long totalRatings = ratingRepository.count();
        
        return "BrainLearning Statistics:\n\n" +
            "- Users: " + totalUsers + "\n" +
            "- Courses: " + publishedCourses + "/" + totalCourses + "\n" +
            "- Enrollments: " + totalEnrollments + "\n" +
            "- Quizzes: " + totalQuizzes + "\n" +
            "- Reviews: " + totalRatings + "\n" +
            "- Average grade: " + String.format("%.1f", avgGrade) + "%\n";
    }
    
    // ========== COURSE DETAILS ==========
    public String getCourseDetails(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (!courseOpt.isPresent()) {
            return "Course not found.";
        }
        
        Course course = courseOpt.get();
        long studentCount = enrollmentRepository.countByCourseId(courseId);
        long quizCount = quizRepository.countByCourseId(courseId);
        
        Double avgRating = ratingRepository.getAverageRatingByCourseId(courseId);
        if (avgRating == null) avgRating = 0.0;
        
        return "Course: " + course.getTitle() + "\n\n" +
            "Description: " + course.getDescription() + "\n" +
            "Module: " + course.getModule() + "\n" +
            "Level: " + course.getNiveau() + "\n" +
            "Status: " + course.getStatus() + "\n" +
            "Students: " + studentCount + "\n" +
            "Quizzes: " + quizCount + "\n" +
            "Rating: " + String.format("%.1f", avgRating) + "/5\n";
    }
    
    // ========== TEACHER CONTACT ==========
    public String getTeacherContactInfo() {
        return "To contact a teacher: Go to course page -> Ratings -> Leave a comment.";
    }
    
    // ========== WELCOME MESSAGE ==========
    public String getWelcomeMessage(Long userId) {
        Optional<Utilisateur> userOpt = userRepository.findById(userId);
        String name = userOpt.map(u -> u.getName()).orElse("Student");
        
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
        
        if (enrollments == null || enrollments.isEmpty()) {
            return "Hello " + name + "! Welcome to BrainLearning!";
        }
        
        long completed = enrollments.stream()
            .filter(e -> Boolean.TRUE.equals(e.getIsCompleted()))
            .count();
        
        return "Hello " + name + "! You have completed " + completed + "/" + enrollments.size() + " courses.";
    }
    
    // ========== RECENT COURSES ==========
    public String getRecentlyDownloadedCourses(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
        
        if (enrollments == null || enrollments.isEmpty()) {
            return "No courses found.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("Your Courses:\n\n");
        
        int count = 0;
        for (Enrollment enrollment : enrollments) {
            if (count >= 5) break;
            Optional<Course> courseOpt = courseRepository.findById(enrollment.getCourseId());
            if (courseOpt.isPresent()) {
                response.append("- ").append(courseOpt.get().getTitle()).append("\n");
                count++;
            }
        }
        
        return response.toString();
    }
}