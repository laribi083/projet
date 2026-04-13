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
    
    // Vérifier si un étudiant est déjà inscrit à un cours
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    // Compter le nombre d'étudiants inscrits à un cours
    long countByCourseId(Long courseId);
    
    // Compter le nombre d'étudiants inscrits aux cours d'un teacher
    @Query("SELECT COUNT(DISTINCT e.studentId) FROM Enrollment e WHERE e.teacherId = :teacherId")
    long countDistinctStudentsByTeacherId(@Param("teacherId") Long teacherId);
    
    // Récupérer tous les étudiants inscrits à un cours
    List<Enrollment> findByCourseId(Long courseId);
    
    // Récupérer tous les cours auxquels un étudiant est inscrit
    List<Enrollment> findByStudentId(Long studentId);
    
    // Vérifier si un étudiant a téléchargé un cours
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}