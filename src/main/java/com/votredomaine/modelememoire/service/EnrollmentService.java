package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Enrollment;
import com.votredomaine.modelememoire.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    
    /**
     * Enregistre le téléchargement d'un cours par un étudiant
     * @param studentId ID de l'étudiant
     * @param studentName Nom de l'étudiant
     * @param courseId ID du cours
     * @return true si l'inscription est nouvelle, false si déjà existante
     */
    @Transactional
    public boolean registerDownload(Long studentId, String studentName, Long courseId) {
        System.out.println("========================================");
        System.out.println("🚨 [EnrollmentService] registerDownload CALLED");
        System.out.println("   studentId: " + studentId);
        System.out.println("   studentName: " + studentName);
        System.out.println("   courseId: " + courseId);
        System.out.println("========================================");
        
        // Vérifier si studentId est null
        if (studentId == null) {
            System.err.println("❌ ERREUR: studentId est NULL !");
            return false;
        }
        
        // Vérifier si l'étudiant est déjà inscrit à ce cours
        Optional<Enrollment> existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        if (existing.isPresent()) {
            System.out.println("⚠️ Étudiant déjà inscrit, mise à jour de la date");
            Enrollment enrollment = existing.get();
            enrollment.setDownloadedAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
            return false; // Pas nouveau
        }
        
        // Récupérer les informations du cours
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            System.err.println("❌ Cours non trouvé avec ID: " + courseId);
            throw new RuntimeException("Cours non trouvé avec l'ID: " + courseId);
        }
        
        System.out.println("📚 Cours trouvé: " + course.getTitle());
        System.out.println("   Teacher ID: " + course.getTeacherId());
        System.out.println("   Teacher Name: " + course.getTeacherName());
        
        // Créer une nouvelle inscription
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setCourseTitle(course.getTitle());
        enrollment.setStudentName(studentName);
        enrollment.setTeacherId(course.getTeacherId());
        enrollment.setTeacherName(course.getTeacherName());
        enrollment.setDownloadedAt(LocalDateTime.now());
        
        System.out.println("💾 Sauvegarde de l'inscription...");
        Enrollment saved = enrollmentRepository.save(enrollment);
        System.out.println("✅ Inscription sauvegardée avec ID: " + saved.getId());
        
        return true; // Nouvelle inscription
    }
    
    // ========== ⭐ MÉTHODE PRINCIPALE POUR LA SOLUTION 1 ==========
    
    /**
     * ⭐ Récupère les IDs des cours déjà téléchargés par un étudiant
     * (UNE SEULE REQUÊTE - TRÈS PERFORMANT)
     * @param studentId ID de l'étudiant
     * @return Liste des IDs des cours
     */
    public List<Long> getDownloadedCourseIds(Long studentId) {
        System.out.println("🔍 [getDownloadedCourseIds] studentId: " + studentId);
        
        if (studentId == null) {
            System.out.println("⚠️ studentId est null, retourne liste vide");
            return List.of();
        }
        
        List<Long> courseIds = enrollmentRepository.findCourseIdsByStudentId(studentId);
        System.out.println("📚 IDs des cours trouvés: " + (courseIds != null ? courseIds.size() : 0));
        
        return courseIds != null ? courseIds : List.of();
    }
    
    // ========== MÉTHODES COMPLÉMENTAIRES ==========
    
    /**
     * Récupère tous les enrollments (inscriptions) d'un étudiant
     * @param studentId ID de l'étudiant
     * @return Liste des enrollments
     */
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        System.out.println("🔍 [getEnrollmentsByStudent] studentId: " + studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        System.out.println("📚 Nombre d'enrollments trouvés: " + (enrollments != null ? enrollments.size() : 0));
        return enrollments != null ? enrollments : List.of();
    }
    
    /**
     * Récupère tous les cours téléchargés par un étudiant (objets Course complets)
     * @param studentId ID de l'étudiant
     * @return Liste des cours
     */
    public List<Course> getCoursesByStudent(Long studentId) {
        System.out.println("🔍 [getCoursesByStudent] studentId: " + studentId);
        
        if (studentId == null) {
            return List.of();
        }
        
        List<Course> courses = enrollmentRepository.findCoursesByStudentId(studentId);
        System.out.println("📚 Cours trouvés: " + (courses != null ? courses.size() : 0));
        return courses != null ? courses : List.of();
    }
    
    /**
     * Compte le nombre d'étudiants inscrits à un cours
     * @param courseId ID du cours
     * @return nombre d'étudiants uniques
     */
    public long countStudentsByCourse(Long courseId) {
        long count = enrollmentRepository.countByCourseId(courseId);
        System.out.println("📊 [countStudentsByCourse] courseId=" + courseId + ", count=" + count);
        return count;
    }
    
    /**
     * Compte le nombre total d'étudiants inscrits aux cours d'un teacher
     * @param teacherId ID du teacher
     * @return nombre d'étudiants uniques
     */
    public long countTotalStudentsByTeacher(Long teacherId) {
        long count = enrollmentRepository.countDistinctStudentsByTeacherId(teacherId);
        System.out.println("📊 [countTotalStudentsByTeacher] teacherId=" + teacherId + ", count=" + count);
        return count;
    }
    
    /**
     * Vérifie si un étudiant a déjà téléchargé un cours
     * @param studentId ID de l'étudiant
     * @param courseId ID du cours
     * @return true si déjà téléchargé
     */
    public boolean hasDownloaded(Long studentId, Long courseId) {
        if (studentId == null || courseId == null) {
            return false;
        }
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
    
    /**
     * Récupère les statistiques des téléchargements pour un cours
     * @param courseId ID du cours
     * @return nombre de téléchargements
     */
    public long getDownloadCountByCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }
    
    /**
     * Récupère les derniers téléchargements
     * @param limit Nombre maximum de résultats
     * @return Liste des derniers enrollments
     */
    public List<Enrollment> getRecentDownloads(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return enrollmentRepository.findTopNByOrderByDownloadedAtDesc(pageable);
    }
    
    /**
     * Supprime toutes les inscriptions d'un étudiant
     * @param studentId ID de l'étudiant
     */
    @Transactional
    public void deleteEnrollmentsByStudent(Long studentId) {
        System.out.println("🗑️ [deleteEnrollmentsByStudent] studentId: " + studentId);
        enrollmentRepository.deleteByStudentId(studentId);
    }
    
    /**
     * Supprime toutes les inscriptions d'un cours
     * @param courseId ID du cours
     */
    @Transactional
    public void deleteEnrollmentsByCourse(Long courseId) {
        System.out.println("🗑️ [deleteEnrollmentsByCourse] courseId: " + courseId);
        enrollmentRepository.deleteByCourseId(courseId);
    }
}