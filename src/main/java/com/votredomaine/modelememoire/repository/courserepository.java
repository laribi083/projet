package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface courserepository extends JpaRepository<Course, Long> {
    
    // ========== MÉTHODES EXISTANTES ==========
    
    List<Course> findByTeacherId(Long teacherId);
    
    List<Course> findByNiveau(String niveau);
    
    // ⭐ MÉTHODE CORRIGÉE : findByNiveauAndStatus
    List<Course> findByNiveauAndStatus(String niveau, String status);
    
    List<Course> findByStatus(String status);
    
    long countByTeacherId(Long teacherId);
    
    long countByStatus(String status);
    
    long countByNiveau(String niveau);
    
    List<Course> findByModuleAndNiveau(String module, String niveau);
    
    List<Course> findByModuleAndNiveauAndStatus(String module, String niveau, String status);
    
    List<Course> findByTitleContainingIgnoreCase(String title);
    
    List<Course> findByModule(String module);
    
    List<Course> findByTeacherNameContainingIgnoreCase(String teacherName);
    
    // ========== MÉTHODES POUR LES STATUTS ==========
    
    List<Course> findByTeacherIdAndStatus(Long teacherId, String status);
    
    @Query("SELECT DISTINCT c.status FROM Course c WHERE c.teacherId = :teacherId")
    List<String> findDistinctStatusByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.createdAt DESC")
    List<Course> findAllPublishedCoursesOrderByDate();
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PENDING' ORDER BY c.createdAt DESC")
    List<Course> findAllPendingCoursesOrderByDate();
    
    @Query("SELECT c FROM Course c WHERE c.status = 'VALIDATED' ORDER BY c.createdAt DESC")
    List<Course> findAllValidatedCoursesOrderByDate();
    
    @Query("SELECT c FROM Course c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:niveau IS NULL OR c.niveau = :niveau) AND " +
           "(:module IS NULL OR c.module = :module) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.teacherName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Course> searchCourses(@Param("status") String status,
                               @Param("niveau") String niveau,
                               @Param("module") String module,
                               @Param("search") String search);
}