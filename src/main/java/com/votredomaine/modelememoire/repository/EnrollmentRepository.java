package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    // ========== MÉTHODES DE BASE ==========
    
    /**
     * Trouve une inscription par ID d'étudiant et ID de cours
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * Vérifie si un étudiant est inscrit à un cours
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * Récupère toutes les inscriptions d'un étudiant
     */
    List<Enrollment> findByStudentId(Long studentId);
    
    /**
     * Récupère toutes les inscriptions d'un cours
     */
    List<Enrollment> findByCourseId(Long courseId);
    
    /**
     * Récupère toutes les inscriptions d'un enseignant
     */
    List<Enrollment> findByTeacherId(Long teacherId);
    
    // ========== MÉTHODES DE COMPTAGE ==========
    
    /**
     * Compte le nombre d'inscriptions à un cours
     */
    long countByCourseId(Long courseId);
    
    /**
     * Compte le nombre d'inscriptions d'un étudiant
     */
    long countByStudentId(Long studentId);
    
    /**
     * Compte le nombre d'inscriptions d'un enseignant
     */
    long countByTeacherId(Long teacherId);
    
    // ========== REQUÊTES PERSONNALISÉES ==========
    
    /**
     * ⭐ Récupère les IDs des cours auxquels un étudiant est inscrit
     * (UNE SEULE REQUÊTE - TRÈS PERFORMANT)
     */
    @Query("SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId")
    List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Récupère les cours complets auxquels un étudiant est inscrit
     */
    @Query("SELECT c FROM Course c WHERE c.id IN (SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId)")
    List<com.votredomaine.modelememoire.model.Course> findCoursesByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Compte le nombre d'étudiants uniques inscrits aux cours d'un enseignant
     */
    @Query("SELECT COUNT(DISTINCT e.studentId) FROM Enrollment e WHERE e.teacherId = :teacherId")
    long countDistinctStudentsByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * Récupère les N dernières inscriptions
     */
    @Query("SELECT e FROM Enrollment e ORDER BY e.downloadedAt DESC")
    List<Enrollment> findTopNByOrderByDownloadedAtDesc(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Récupère les inscriptions d'un étudiant avec les détails du cours
     */
    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId ORDER BY e.downloadedAt DESC")
    List<Enrollment> findByStudentIdOrderByDownloadedAtDesc(@Param("studentId") Long studentId);
    
    /**
     * Récupère les inscriptions d'un cours avec les détails des étudiants
     */
    @Query("SELECT e FROM Enrollment e WHERE e.courseId = :courseId ORDER BY e.downloadedAt DESC")
    List<Enrollment> findByCourseIdOrderByDownloadedAtDesc(@Param("courseId") Long courseId);
    
    /**
     * Supprime toutes les inscriptions d'un étudiant
     */
    @Query("DELETE FROM Enrollment e WHERE e.studentId = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Supprime toutes les inscriptions d'un cours
     */
    @Query("DELETE FROM Enrollment e WHERE e.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
    
    // ========== STATISTIQUES AVANCÉES ==========
    
    /**
     * Récupère le nombre d'inscriptions par cours (pour les statistiques)
     */
    @Query("SELECT e.courseId, COUNT(e) FROM Enrollment e GROUP BY e.courseId")
    List<Object[]> countEnrollmentsByCourse();
    
    /**
     * Récupère le nombre d'inscriptions par étudiant (pour les statistiques)
     */
    @Query("SELECT e.studentId, COUNT(e) FROM Enrollment e GROUP BY e.studentId")
    List<Object[]> countEnrollmentsByStudent();
}