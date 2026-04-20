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
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    List<Enrollment> findByTeacherId(Long teacherId);
    
    // ========== MÉTHODES DE COMPTAGE ==========
    
    long countByCourseId(Long courseId);
    
    long countByStudentId(Long studentId);
    
    long countByTeacherId(Long teacherId);
    
    // ========== REQUÊTES PERSONNALISÉES ==========
    
    @Query("SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId")
    List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Course c WHERE c.id IN (SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId)")
    List<com.votredomaine.modelememoire.model.Course> findCoursesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(DISTINCT e.studentId) FROM Enrollment e WHERE e.teacherId = :teacherId")
    long countDistinctStudentsByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT e FROM Enrollment e ORDER BY e.downloadedAt DESC")
    List<Enrollment> findTopNByOrderByDownloadedAtDesc(org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId ORDER BY e.downloadedAt DESC")
    List<Enrollment> findByStudentIdOrderByDownloadedAtDesc(@Param("studentId") Long studentId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.courseId = :courseId ORDER BY e.downloadedAt DESC")
    List<Enrollment> findByCourseIdOrderByDownloadedAtDesc(@Param("courseId") Long courseId);
    
    @Query("DELETE FROM Enrollment e WHERE e.studentId = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);
    
    @Query("DELETE FROM Enrollment e WHERE e.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT e.courseId, COUNT(e) FROM Enrollment e GROUP BY e.courseId")
    List<Object[]> countEnrollmentsByCourse();
    
    @Query("SELECT e.studentId, COUNT(e) FROM Enrollment e GROUP BY e.studentId")
    List<Object[]> countEnrollmentsByStudent();
}