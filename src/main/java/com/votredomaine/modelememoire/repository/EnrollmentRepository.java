// EnrollmentRepository.java - COMPLET
package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    // ========== MÉTHODES EXISTANTES ==========
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    List<Enrollment> findByTeacherId(Long teacherId);
    
    long countByCourseId(Long courseId);
    
    long countByStudentId(Long studentId);
    
    long countByTeacherId(Long teacherId);
    
    @Query("SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId")
    List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Course c WHERE c.id IN (SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId)")
    List<com.votredomaine.modelememoire.model.Course> findCoursesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(DISTINCT e.studentId) FROM Enrollment e WHERE e.teacherId = :teacherId")
    long countDistinctStudentsByTeacherId(@Param("teacherId") Long teacherId);
    
    // ========== NOUVELLES MÉTHODES POUR LE CHATBOT ==========
    
    /**
     * Récupère les inscriptions d'un étudiant triées par date de téléchargement décroissante
     */
    List<Enrollment> findByStudentIdOrderByDownloadedAtDesc(Long studentId);
    
    /**
     * Récupère les N dernières inscriptions d'un étudiant
     */
    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId ORDER BY e.downloadedAt DESC")
    List<Enrollment> findTopNByStudentIdOrderByDownloadedAtDesc(@Param("studentId") Long studentId, 
                                                                 org.springframework.data.domain.Pageable pageable);
    
    /**
     * Compte le nombre de cours complétés par un étudiant
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.studentId = :studentId AND e.isCompleted = true")
    long countCompletedByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Récupère les inscriptions d'un étudiant triées par date d'inscription décroissante
     */
    List<Enrollment> findByStudentIdOrderByEnrollmentDateDesc(Long studentId);
    
    /**
     * Récupère les inscriptions d'un étudiant pour un cours spécifique avec statut complété
     */
    Optional<Enrollment> findByStudentIdAndCourseIdAndIsCompletedTrue(Long studentId, Long courseId);
    
    /**
     * Récupère toutes les inscriptions avec progression > 0 (cours commencés)
     */
    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId AND e.progress > 0 ORDER BY e.progress DESC")
    List<Enrollment> findStartedCoursesByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Calcule la progression moyenne d'un étudiant sur tous ses cours
     */
    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.studentId = :studentId")
    Double getAverageProgressByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Récupère les inscriptions récentes (derniers 30 jours)
     */
    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId AND e.enrollmentDate >= :date")
    List<Enrollment> findRecentEnrollmentsByStudentId(@Param("studentId") Long studentId, 
                                                       @Param("date") LocalDateTime date);
    
    /**
     * Compte le nombre total d'inscriptions actives (non complétées)
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.studentId = :studentId AND e.isCompleted = false")
    long countActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Récupère les cours les plus populaires (par nombre d'inscriptions)
     */
    @Query("SELECT e.courseId, COUNT(e) as count FROM Enrollment e GROUP BY e.courseId ORDER BY count DESC")
    List<Object[]> findMostPopularCourses(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Vérifie si un étudiant a complété un cours
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e WHERE e.studentId = :studentId AND e.courseId = :courseId AND e.isCompleted = true")
    boolean hasCompletedCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}