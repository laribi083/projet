package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Enrollment;
import com.votredomaine.modelememoire.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private Courseservice courseService;
    
    @Transactional
    public boolean registerDownload(Long studentId, String studentName, Long courseId) {
        System.out.println("=== registerDownload ===");
        System.out.println("studentId: " + studentId);
        System.out.println("courseId: " + courseId);
        
        if (studentId == null) {
            System.err.println("❌ studentId est NULL !");
            return false;
        }
        
        Optional<Enrollment> existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        if (existing.isPresent()) {
            System.out.println("⚠️ Étudiant déjà inscrit");
            Enrollment enrollment = existing.get();
            enrollment.setDownloadedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
            return false;
        }
        
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            System.err.println("❌ Cours non trouvé: " + courseId);
            return false;
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setCourseTitle(course.getTitle());
        enrollment.setStudentName(studentName);
        enrollment.setTeacherId(course.getTeacherId());
        enrollment.setTeacherName(course.getTeacherName());
        enrollment.setDownloadedAt(LocalDateTime.now());
        
        enrollmentRepository.save(enrollment);
        System.out.println("✅ Inscription sauvegardée");
        return true;
    }
    
    // ⭐ Méthode principale pour récupérer les IDs des cours téléchargés
    public List<Long> getDownloadedCourseIds(Long studentId) {
        if (studentId == null) {
            return List.of();
        }
        List<Long> courseIds = enrollmentRepository.findCourseIdsByStudentId(studentId);
        System.out.println("getDownloadedCourseIds(" + studentId + ") -> " + (courseIds != null ? courseIds.size() : 0) + " cours");
        return courseIds != null ? courseIds : List.of();
    }
    
    public List<Enrollment> findByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }
        return enrollmentRepository.findByStudentId(studentId);
    }
    
    public long countStudentsByCourse(Long courseId) {
        if (courseId == null) {
            return 0;
        }
        return enrollmentRepository.countByCourseId(courseId);
    }
    
    public long countTotalStudentsByTeacher(Long teacherId) {
        if (teacherId == null) {
            return 0;
        }
        return enrollmentRepository.countDistinctStudentsByTeacherId(teacherId);
    }
    
    public List<Course> getCoursesByStudent(Long studentId) {
        if (studentId == null) return List.of();
        return enrollmentRepository.findCoursesByStudentId(studentId);
    }
    
    public boolean hasDownloaded(Long studentId, Long courseId) {
        if (studentId == null || courseId == null) return false;
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
    
    public long getDownloadCountByCourse(Long courseId) {
        if (courseId == null) return 0;
        return enrollmentRepository.countByCourseId(courseId);
    }
}