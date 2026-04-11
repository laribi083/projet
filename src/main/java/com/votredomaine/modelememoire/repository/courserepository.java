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
    
    List<Course> findByNiveauAndStatus(String niveau, String status);
    
    List<Course> findByStatus(String status);
    
    long countByTeacherId(Long teacherId);
    
    List<Course> findByModuleAndNiveau(String module, String niveau);
    
    List<Course> findByModuleAndNiveauAndStatus(String module, String niveau, String status);
    
    List<Course> findByTitleContainingIgnoreCase(String title);
    
    List<Course> findByModule(String module);
    
    List<Course> findByTeacherNameContainingIgnoreCase(String teacherName);
    
    // ⭐ AJOUTER CETTE MÉTHODE MANQUANTE ⭐
    @Query("SELECT c FROM Course c WHERE c.status = 'ACTIVE' ORDER BY c.createdAt DESC")
    List<Course> findAllActiveCoursesOrderByDate();
    
    // ⭐ AJOUTER AUSSI CETTE MÉTHODE POUR LA RECHERCHE ⭐
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