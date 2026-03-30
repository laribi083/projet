package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.repository.courserepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class Courseservice {
    
    @Autowired
    private courserepository courseRepository;
    
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/courses/";
    
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }
    
    public List<Course> getCoursesByNiveau(String niveau) {
        return courseRepository.findByNiveauAndStatus(niveau, "ACTIVE");
    }
    
    public List<Course> getAllActiveCourses() {
        return courseRepository.findByStatus("ACTIVE");
    }
    
    public Course createCourse(Course course, List<MultipartFile> files) throws IOException {
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        if (course.getStatus() == null) {
            course.setStatus("ACTIVE");
        }
        
     
        if (files != null && !files.isEmpty()) {
            List<String> filePaths = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            
            
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(fileName);
                    
                    Files.copy(file.getInputStream(), filePath);
                    
                    filePaths.add(filePath.toString());
                    fileNames.add(originalFileName);
                }
            }
            
            course.setFilePaths(filePaths);
            course.setFileNames(fileNames);
        }
        
        return courseRepository.save(course);
    }
    
    public Course updateCourse(Long id, Course courseDetails, List<MultipartFile> newFiles) throws IOException {
        Course existingCourse = courseRepository.findById(id).orElse(null);
        if (existingCourse == null) return null;
        
        existingCourse.setTitle(courseDetails.getTitle());
        existingCourse.setDescription(courseDetails.getDescription());
        existingCourse.setNiveau(courseDetails.getNiveau());
        existingCourse.setFiliere(courseDetails.getFiliere());
        existingCourse.setUpdatedAt(LocalDateTime.now());
        
        // Handle new file uploads
        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> filePaths = existingCourse.getFilePaths();
            List<String> fileNames = existingCourse.getFileNames();
            
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(fileName);
                    
                    Files.copy(file.getInputStream(), filePath);
                    
                    filePaths.add(filePath.toString());
                    fileNames.add(originalFileName);
                }
            }
            
            existingCourse.setFilePaths(filePaths);
            existingCourse.setFileNames(fileNames);
        }
        
        return courseRepository.save(existingCourse);
    }
    
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            // Delete associated files
            for (String filePath : course.getFilePaths()) {
                try {
                    Files.deleteIfExists(Paths.get(filePath));
                } catch (IOException e) {
                    // Log error but continue
                    System.err.println("Could not delete file: " + filePath);
                }
            }
            courseRepository.deleteById(id);
        }
    }
    
    public long getTotalCoursesByTeacher(Long teacherId) {
        return courseRepository.countByTeacherId(teacherId);
    }
}