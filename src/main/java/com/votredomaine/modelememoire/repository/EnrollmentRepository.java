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
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    List<Enrollment> findByTeacherId(Long teacherId);
    
    long countByCourseId(Long courseId);
    
    long countByStudentId(Long studentId);
    
    long countByTeacherId(Long teacherId);
    
    // ⭐ Méthode pour récupérer les IDs des cours d'un étudiant
    @Query("SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId")
    List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Course c WHERE c.id IN (SELECT e.courseId FROM Enrollment e WHERE e.studentId = :studentId)")
    List<com.votredomaine.modelememoire.model.Course> findCoursesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(DISTINCT e.studentId) FROM Enrollment e WHERE e.teacherId = :teacherId")
    long countDistinctStudentsByTeacherId(@Param("teacherId") Long teacherId);
}