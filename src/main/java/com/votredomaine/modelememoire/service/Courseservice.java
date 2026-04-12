package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.repository.courserepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class Courseservice {
    
    @Autowired
    private courserepository courseRepository;
    
    @Autowired
    private QuizService quizService;
    
    // ========== MÉTHODES DE BASE ==========
    
    public Course save(Course course) {
        if (course.getCreatedAt() == null) {
            course.setCreatedAt(LocalDateTime.now());
        }
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }
    
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public List<Course> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }
    
    public List<Course> findByNiveau(String niveau) {
        return courseRepository.findByNiveau(niveau);
    }
    
    // ========== MÉTHODES SPÉCIFIQUES ==========
    
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    
    public List<Course> getAllActiveCourses() {
        return courseRepository.findByStatus("ACTIVE");
    }
    
    public List<Course> getCoursesByNiveau(String niveau) {
        return courseRepository.findByNiveau(niveau);
    }
    
    public long getTotalCoursesByTeacher(Long teacherId) {
        return courseRepository.countByTeacherId(teacherId);
    }
    
    @Transactional
    public Course createCourse(Course course, List<MultipartFile> files) {
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        if (course.getStatus() == null) {
            course.setStatus("ACTIVE");
        }
        
        if (files != null && !files.isEmpty()) {
            List<String> filePaths = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    fileNames.add(file.getOriginalFilename());
                    filePaths.add("/uploads/courses/" + file.getOriginalFilename());
                }
            }
            
            course.setFilePaths(filePaths);
            course.setFileNames(fileNames);
            
            if (!filePaths.isEmpty()) {
                course.setFilePath(filePaths.get(0));
                course.setFileName(fileNames.get(0));
            }
        }
        
        return courseRepository.save(course);
    }
    
    @Transactional
    public Course updateCourse(Long id, Course courseDetails, List<MultipartFile> newFiles) {
        Course existingCourse = getCourseById(id);
        
        if (existingCourse == null) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        
        existingCourse.setTitle(courseDetails.getTitle());
        existingCourse.setDescription(courseDetails.getDescription());
        existingCourse.setModule(courseDetails.getModule());
        existingCourse.setNiveau(courseDetails.getNiveau());
        existingCourse.setUpdatedAt(LocalDateTime.now());
        
        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> filePaths = existingCourse.getFilePaths();
            List<String> fileNames = existingCourse.getFileNames();
            
            if (filePaths == null) {
                filePaths = new ArrayList<>();
                fileNames = new ArrayList<>();
            }
            
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    fileNames.add(file.getOriginalFilename());
                    filePaths.add("/uploads/courses/" + file.getOriginalFilename());
                }
            }
            
            existingCourse.setFilePaths(filePaths);
            existingCourse.setFileNames(fileNames);
            
            if (!filePaths.isEmpty()) {
                existingCourse.setFilePath(filePaths.get(0));
                existingCourse.setFileName(fileNames.get(0));
            }
        }
        
        return courseRepository.save(existingCourse);
    }
    
    @Transactional
    public void deleteCourse(Long courseId) {
        List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
        for (Quiz quiz : quizzes) {
            quizService.deleteQuiz(quiz.getId());
        }
        courseRepository.deleteById(courseId);
    }
    
    public long countQuizzesByCourse(Long courseId) {
        return quizService.countQuizzesByCourse(courseId);
    }
}